/**
 * Autor: Matthew Conde Oltra
 * Fecha: 28-11-2019
 *
 * Fichero creación del mapa, modificación de opciones del fragment del mapa,
 * del XML correspondiente.
 */

package com.equipo3.poluzone.ui.mapa;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.equipo3.poluzone.Callback;
import com.equipo3.poluzone.CustomInfoWindowAdapter;
import com.equipo3.poluzone.NavigationDrawerActivity;
import com.equipo3.poluzone.R;
import com.github.mikephil.charting.data.DataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.leinardi.android.speeddial.SpeedDialView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class MapaFragment extends Fragment implements OnMapReadyCallback, Callback {

    /**
     * Radius alternativo
     */
    private static final int ALT_HEATMAP_RADIUS = 28;

    /**
     * Opacidad alternativa
     */
    private static final double ALT_HEATMAP_OPACITY = 0.5;

    /****************************************************************
     *
     *          Gradiente alternativo (green -> red) 5 niveles
     *
     *                         ESTÁ EN USO!
     ****************************************************************/
    public static final int[] COLORS = {
            Color.argb(0, 0, 255, 0),// transparent
            //Color.argb(255 / 3 * 2, 0, 255, 0),
            Color.rgb(0, 255, 0), // green
            Color.rgb(255, 255, 0), // yellow
            //Color.rgb(207, 174, 72), // brown
            Color.rgb(255, 190, 0), // orange
            Color.rgb(255, 0, 0) // red
    };
    public static final float[] START_POINTS = {
            0.0f,
            0.10f,
            0.20f,
            0.60f,
            1.0f
    };

    /*private static final int[] COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };*/
    /*public static final float[] START_POINTS = {
            0.0f,    //0-50
            0.20f,   //101-150
            0.40f,   //201-250
            0.60f,   //301-350
            0.80f,   //401-450
            1.0f     //501-550
    };*/

    public static final Gradient HEATMAP_GRADIENT = new Gradient(COLORS, START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    //


    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    private HashMap<String, DataSet> mLists = new HashMap<>();


    //Etiqueta para el debugging
    String TAG = "MAPA";
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Mapa
    //---------------------------------------------------------------------------
    GoogleMap map;
    private MapaViewModel mapaViewModel;
    private SpeedDialView speedDialView;
    CustomInfoWindowAdapter infoWindow;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapaViewModel =
                ViewModelProviders.of(this).get(MapaViewModel.class);
        View root = inflater.inflate(R.layout.activity_maps, container, false);
        // acceder speed dial
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.show();
        //speedDialView.
        //Ventana para la información de los markers
        infoWindow = new CustomInfoWindowAdapter(LayoutInflater.from(getActivity()));

        /**
         *  - Matthew Conde Oltra -
         *
         * CONCEPTO - COMO ENVIAR LOS DATOS CON CALLBACK A UN FRAGMENT DESDE SERVIDORFAKE?
         *
         * 1. Añadimos en el fragment al que vamos a pasarle los datos la referencia a la
         * actividad del navigation drawer. Con las siguiente línea:
         *
         *   NavigationDrawerActivity navigation = (NavigationDrawerActivity) getParentFragment().getActivity();
         *
         * 2. Dentro del SERVIDORFAKE debemos tener un objeto Callback al que le introducimos la actividad
         * de la cual estamos cogiendo el objeto ServidorFake, en este caso de navigationDrawerActivity.
         * Por ello hacemos referencia al callback que tenemos en el servidorfake pero de esa actividad, y
         * le añadimos this - haciendo referencia al método del Callback de este fragment.
         *
         *   navigation.servidorFake.callback = this;
         *
         * 3. Añadimos el Callback al fragment con un <<implements Callback>>(en este ejemplo se llama Callback,
         * pero depende del nombre de la INTERFACE que hayas creado para el Callback).
         *
         * Por último, dentro de servidorFake necesitamos un objeto de el tipo de Callback que vayamos
         * a utilizar y llamarlo donde nosotros queramos que devuelva los datos.
         *
         * LISTO! YA PUEDES PASAR LOS DATOS!
         *
         */

        NavigationDrawerActivity navigation = (NavigationDrawerActivity) getParentFragment().getActivity();
        navigation.servidorFake.callback = this;

        long primeraFecha = 0;
        long fechaActual= 0;

        navigation.servidorFake.getTodasLasMedidasPorFecha(primeraFecha, fechaActual);
        navigation.servidorFake.getEstacionesOficiales();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng pp = new LatLng(38.996100, -0.166439);

        try {
            // Para customizar el mapa, añadimos un estilo en un fichero JSON
            // que contiene el estilo del mapa, podemos personalizarlo nosotros
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getContext(), R.raw.style_map_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "No se ha encontrado el estilo. Error: ", e);
        }
        /******************************************************************************
        *               ----- Configuración inicial del mapa -----
        *  Eliminamos el compas de la visualización del mapa, y los botones que
        * aparecen en el mapa al darle a un marker. A parte añadimos la funcionalidad
        * de que se aleje del mapa al darle a la vez dos veces seguidas con dos dedos.
         *****************************************************************************/
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setIndoorLevelPickerEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pp, 15.0f));
        map.setInfoWindowAdapter(infoWindow);
    }

    @Override
    public void callbackLogin(boolean resultadoLogin, JSONObject response) {

    }

    /**
     * callbackMedidas()
     * Función callback que recibe todas las medidas de la BBDD del servidor.
     *
     * @param resultado
     * @param medidas
     *
     *  - Matthew Conde Oltra -
     */
    @Override
    public void callbackMedidas(boolean resultado, JSONObject medidas) {
        Log.d("MAPA", "Estamos en el callback medidas");

        if(resultado)
        {
            Log.d("MAPA", "Tenemos las medidas.");
            //Log.d("MAPA", medidas.toString());
            MarkerOptions option = new MarkerOptions();
            int length;
            LatLng coords;
            double latitud;
            double longitud;
            double valor;
            List<WeightedLatLng> list = new ArrayList<WeightedLatLng>();


            try {
                //Recogemos el tamaño del array con las medidas en JSON
                length = medidas.getJSONArray("medidas").length();

                // Dibujamos marcadores para cada una de las medidas
                for (int i = 0; i<length; i++)
                {

                    // Observamos las medidas en el logcat
                    //Log.d("MAPA", medidas.getJSONArray("medidas").getJSONObject(i).toString());
                    //Guardamos cada una de las medidas en una variable auxiliar
                    JSONObject medida = medidas.getJSONArray("medidas").getJSONObject(i);

                    //Log.d(TAG, "Latitud: "+medida.getString("Latitud"));
                    //Log.d(TAG, "Longitud: "+medida.getString("Longitud"));
                    // Guardamos la latitud de cada una cogiendo de la medida
                    latitud = Double.parseDouble(medida.getString("Latitud"));
                    longitud = Double.parseDouble(medida.getString("Longitud"));
                    coords = new LatLng(latitud, longitud);

                    //Log.d(TAG, "Coords: "+coords.toString());
                    // Guardamos el valor de la medida
                    valor = Double.parseDouble(medida.getString("Valor"));
                    list.add(new WeightedLatLng(coords,valor));
                    //Log.d(TAG, "Valor: "+medida.getString("Valor"));
                    //Configuración del marcador
                    option.position(coords).title("UPV").draggable(true).
                            snippet("Contaminación:"+valor).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    //map.addMarker(option);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, list.toString());
            addHeatMap(list);
        }
        else
        {
            Log.d("MAPA", "Las medidas no existen.");
        }
    }

    /**
     * Lista -> addHeatMap()
     *
     *   Creación del mapa de calor, introduciendo una lista de valores, dichos contienen
     * colecciones formadas por las coordenadas y el valor de cada una de ellas.
     *
     * @param l
     *
     * - Matthew Conde Oltra -
     */

    public void addHeatMap(List<WeightedLatLng> l)
    {
        // Creación del mapa de calor con sus coordenadas(latlng) y los valores de cada uno
        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(l)
                .radius(ALT_HEATMAP_RADIUS)
                .gradient(HEATMAP_GRADIENT)
                .opacity(ALT_HEATMAP_OPACITY)
                .build();
        // Agregando superposición al mapa
        mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    /**
     * callbackEstaciones()
     * Función callback que recibe todas las medidas de la BBDD del servidor.
     *
     * @param resultado
     * @param estaciones
     *
     *  - Matthew Conde Oltra -
     */
    @Override
    public void callbackEstaciones(boolean resultado, JSONObject estaciones) {
        Log.d("MAPA", "Estamos en el callback estaciones");
        if(resultado)
        {
            Log.d("MAPA", "Tenemos las estaciones.");
            //Log.d("MAPA", estaciones.toString());
            MarkerOptions option = new MarkerOptions();

            int height = 120;
            int width = 80;
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.pin_estacion);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
            int length;
            LatLng coords;
            double latitud;
            double longitud;
            String nombre;
            String info;
            String so2;
            String co;
            String nox;
            String no;
            String no2;
            String o3;
            try {
                //Recogemos el tamaño del array con las medidas en JSON
                length = estaciones.getJSONArray("estaciones").length();

                // Dibujamos marcadores para cada una de las medidas
                for (int i = 0; i<length; i++)
                {
                    // Observamos las medidas en el logcat
                    //Log.d("MAPA", estaciones.getJSONArray("estaciones").getJSONObject(i).toString());
                    //Guardamos cada una de las medidas en una variable auxiliar
                    JSONObject estacion = estaciones.getJSONArray("estaciones").getJSONObject(i);
                    JSONObject medida;
                    // Guardamos el valor de la estacion
                    nombre = estacion.getString("Nombre");
                    Log.d(TAG, "Nombre: "+nombre);

                    if(estacion.getInt("ID")==50)
                    {
                        medida = estacion.getJSONObject("Medidas");
                        Log.d(TAG, medida.toString());
                        so2 = "SO2: "+medida.getString("s02")+"ppm";
                        co = "CO: "+medida.getString("co")+"ppm";
                        no = "NO: "+medida.getString("no")+"ppm";
                        no2 = "NO2: "+medida.getString("no2")+"ppm";
                        nox = "NOX: "+medida.getString("nox")+"ppm";
                        o3 = "O3: "+medida.getString("o3")+"ppm";

                        // Añadimos el texto a la ventana de infoWindowAdapter
                        infoWindow.n = nombre;
                        infoWindow.s = so2;
                        infoWindow.c = co;
                        infoWindow.no = no;
                        infoWindow.no2 = no2;
                        infoWindow.nox = nox;
                        infoWindow.o = o3;

                    }
                    //Log.d(TAG, "Latitud: "+estacion.getString("Latitud"));
                    //Log.d(TAG, "Longitud: "+estacion.getString("Longitud"));
                    // Guardamos la latitud de cada una cogiendo de la medida
                    latitud = Double.parseDouble(estacion.getString("Latitud"));
                    longitud = Double.parseDouble(estacion.getString("Longitud"));
                    coords = new LatLng(latitud, longitud);
                    //Log.d(TAG, "Coords: "+coords.toString());


                    //Configuración del marcador
                    option.position(coords)
                            .draggable(true)
                            //.snippet(nombre)
                            .icon(smallMarkerIcon);

                    map.addMarker(option);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



            //addHeatMap();
        }
        else
        {
            Log.d("MAPA", "Las estaciones no existen.");
        }
    }

    @Override
    public void callbackMediaCalidadAire(double media) {

    }
}