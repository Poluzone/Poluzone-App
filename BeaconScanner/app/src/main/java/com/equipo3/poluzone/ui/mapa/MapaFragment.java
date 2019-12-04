/**
 * Autor: Matthew Conde Oltra
 * Fecha: 28-11-2019
 *
 * Fichero creación del mapa, modificación de opciones del fragment del mapa,
 * de el XML correspondiente.
 */

package com.equipo3.poluzone.ui.mapa;

import android.content.res.Resources;
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
import com.equipo3.poluzone.NavigationDrawerActivity;
import com.equipo3.poluzone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.leinardi.android.speeddial.SpeedDialView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapaFragment extends Fragment implements OnMapReadyCallback, Callback {
    //Etiqueta para el debugging
    String TAG = "MAPA";
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Mapa
    //---------------------------------------------------------------------------
    GoogleMap map;
    private MapaViewModel mapaViewModel;
    private SpeedDialView speedDialView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapaViewModel =
                ViewModelProviders.of(this).get(MapaViewModel.class);
        View root = inflater.inflate(R.layout.activity_maps, container, false);
        // acceder speed dial
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.show();

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
        //Configuración inicial del mapa
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

    }


    private void addHeatMap() {
        List<LatLng> list = null;
        // Get the data: latitude/longitude positions of police stations.
        try {
            list = readItems(R.raw.police_stations);

        } catch (JSONException e) {
            Toast.makeText(this.getActivity(), "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        Object mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        //Log.d(TAG, "HeatMap"+list.toString());
        return list;
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
            List<LatLng> list = new ArrayList<LatLng>();
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
                    latitud = formatearDecimales(Double.parseDouble(medida.getString("Latitud")),6);
                    longitud = formatearDecimales(Double.parseDouble(medida.getString("Longitud")), 6);
                    coords = new LatLng(latitud, longitud);

                    list.add(coords);

                    //Log.d(TAG, "Coords: "+coords.toString());
                    // Guardamos el valor de la medida
                    valor = Double.parseDouble(medida.getString("Valor"));
                    //Log.d(TAG, "Valor: "+medida.getString("Valor"));
                    //Configuración del marcador
                    option.position(coords).title("UPV").draggable(true).
                            snippet("Contaminación:"+valor).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    map.addMarker(option);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, list.toString());
            // Create a heat map tile provider, passing it the latlngs of the police stations.
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(list)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            Object mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

            addHeatMap();
        }
        else
        {
            Log.d("MAPA", "Las medidas no existen.");
        }
    }

    public static Double formatearDecimales(Double numero, Integer numeroDecimales) {
        return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
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


            int length;
            LatLng coords;
            double latitud;
            double longitud;
            String nombre;
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

                    //Log.d(TAG, "Latitud: "+estacion.getString("Latitud"));
                    //Log.d(TAG, "Longitud: "+estacion.getString("Longitud"));
                    // Guardamos la latitud de cada una cogiendo de la medida
                    latitud = Double.parseDouble(estacion.getString("Latitud"));
                    longitud = Double.parseDouble(estacion.getString("Longitud"));
                    coords = new LatLng(latitud, longitud);
                    //Log.d(TAG, "Coords: "+coords.toString());
                    // Guardamos el valor de la estacion
                    nombre = estacion.getString("Nombre");
                    //Log.d(TAG, "Valor: "+nombre);
                    //Configuración del marcador
                    option.position(coords).title("Estación oficial").draggable(true).
                            snippet("Estación: "+nombre).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
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