package com.equipo3.poluzone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.equipo3.poluzone.ui.mapa.MapaFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------
// NavigationDrawerActivity.java
// Equipo 3
// Autor: Iván Romero, Emilia Rosa van der Heide
// CopyRight:
// El MainActivity mezclado con el navigation drawer
// -----------------------------------------------------------------------
public class NavigationDrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    SpeedDialView speedDialView;

    /*
       Variable donde guardamos si los items del menu del filtro
       están activos o no.
       CO = 0
       NOX = 1
       SO2 = 2
       Ozono = 3
    */
    public Boolean[] showOnMap = new Boolean[4];
    public PopupMenu popup;

    // Bluetooth
    public String nuestroUUID = "EQUIPO-3XURODIMI";
    ReceptorBLE receptorBLE;

    // Interfaz
    public Button button;

    // Para recordar que se ha logeado
    public SharedPreferences loginPreferences;

    // Servidor
    public ServidorFake servidorFake;

    // Para alarma 1 min
    int contador = 0;
    Handler handler;
    Runnable runnable;

    // Para alarma 10 min
    int contador10 = 0;
    Handler handler10;
    Runnable runnable10;

    // Para alarma inactividad
    int contadorInactividad = 0;
    Handler handlerInactividad;
    Runnable runnableInactividad;

    // Datos del user
    public String tipoUser;
    public int idUser;

    public JSONObject medidas;
    public GoogleMap map;
    public HeatmapTileProvider mProvider;
    public TileOverlay mOverlay;
    List<WeightedLatLng> list = new ArrayList<>();

    Polyline polyline = null;
    Marker markerDestino = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        tipoUser = loginPreferences.getString("tipousuario", "o");
        idUser = loginPreferences.getInt("idUsuario", 0);

        Log.d("pruebasssss", tipoUser);

        // Codigo relacionado con el navigation drawer
        Toolbar toolbar;

        DrawerLayout drawer;
        NavigationView navigationView;

        // Creamos el servidorFake indicando la direccion ip y el puerto
        servidorFake = new ServidorFake(this);

        NavController navController;

        // Places sdk
        // Initialize Places.
        Places.initialize(this, getString(R.string.google_maps_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Para los usuarios conductor
        if (tipoUser.equals("Conductor")) {
            setContentView(R.layout.activity_navigation_drawerc);
            toolbar = findViewById(R.id.toolbarc);
            setSupportActionBar(toolbar);
            drawer = findViewById(R.id.drawer_layoutc);
            navigationView = findViewById(R.id.nav_viewc);
            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_inicio,
                    R.id.nav_home, R.id.nav_foto, R.id.nav_info, R.id.nav_ajustes, R.id.nav_perfil, R.id.nav_sesion)
                    .setDrawerLayout(drawer)
                    .build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragmentc);

            // ---------------------------------- BACKEND ------------------------------------------
            // Creamos el receptorBLE indicando la actividad y el uuid que buscamos
            receptorBLE = new ReceptorBLE(this, nuestroUUID);
            Log.d("pruebas", "receptor creado");

            // Empezar temporizadores para mandar al servidor
            alarmaQueSuenaCadaMinuto();
            alarmaQueSuenaCada10Minutos();

            // Empezar temporizador para contar cuánto tiempo lleva
            // inactivo el nodo
            alarmaQueSuenaCuandoEstaInactivo();
        }

        // Para los usuarios normales
        else {
            setContentView(R.layout.activity_navigation_drawer);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home,
                    R.id.nav_foto, R.id.nav_info, R.id.nav_ajustes, R.id.nav_perfil, R.id.nav_sesion)
                    .setDrawerLayout(drawer)
                    .build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // Codigo relacionado con el navigation drawer

        crearFabSpeedDial();
    }

    //------------------------------------------------------------------------------
    // Método de la actividad del Navigation Drawer que se llama al abrir el menu
    // -----------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.navigation_drawer, menu);*/

        // Mostramos los datos del usuario en el nav drawer
        String email = loginPreferences.getString("email", "");

        TextView textoNombre = findViewById(R.id.nombre_nav);
        textoNombre.setText(email);

        return true;
    }

    //------------------------------------------------------------------------------
    // Método de la actividad del Navigation Drawer
    // -----------------------------------------------------------------------
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController;
        if (tipoUser.equals("Conductor")) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragmentc);
        }
        else {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        }
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // -----------------------------------------------------------------------
    // -> hayQueActualizarMedicionesYEnviarlasAlServidor ->
    // Enviar las mediciones al servidor
    // -----------------------------------------------------------------------
    public void hayQueActualizarMedicionesYEnviarlasAlServidor() {
        // calculamos la media
        receptorBLE.calcularMediaMedidas();
        Medida medida = receptorBLE.obtenerContaminacion();
        Log.d("pruebas", "valor: " + medida.getMedida() + " tiempo: " + medida.getTiempo() + " lati: " + medida.getPosicion().getLatitude());
        servidorFake.insertarMedida(medida);
        servidorFake.indicarActividadNodo("Activo");
        TextView activoNodo = findViewById(R.id.texviewactivonodo);
        if (activoNodo != null) activoNodo.setText(R.string.activo);
        receptorBLE.ultimaTramaEncontrada = null;
    }

    // -----------------------------------------------------------------------
    // -> hayQueAvisarDeInactividad ->
    // Avisar de inactividad del nodo al servidor
    // -----------------------------------------------------------------------
    public void hayQueAvisarDeInactividad() {
        String actividad = "Inactivo";
        TextView activoNodo = findViewById(R.id.texviewactivonodo);
        if (activoNodo != null) activoNodo.setText(R.string.inactivo);
        servidorFake.indicarActividadNodo(actividad);
    }


    // -----------------------------------------------------------------------
    // -> crearFabSpeedDial ->
    // -----------------------------------------------------------------------
    private void crearFabSpeedDial() {
        Log.d("pruebas", "crearFabSpeedDial()");
        // ---------- FAB SPEED DIAL -----------------------------------------

        // acceder speed dial
        speedDialView = findViewById(R.id.fab);

        // cambiar icono del fab principal
        speedDialView.setMainFabClosedDrawable(MaterialDrawableBuilder.with(this.getBaseContext()) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.DOTS_HORIZONTAL) // provide an icon
                .setColor(Color.WHITE) // set the icon color
                .setToActionbarSize() // set the icon size
                .build());

        // rotacion de abrir/cerrar fab a 90º para que gire de hor a vert
        speedDialView.setMainFabAnimationRotateAngle(90);

        // action item filtro, añade icono de filter
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder((R.id.filter), MaterialDrawableBuilder.with(this.getBaseContext()) // provide a context
                        .setIcon(MaterialDrawableBuilder.IconValue.FILTER_VARIANT) // provide an icon
                        .setColor(Color.WHITE) // set the icon color
                        .setToActionbarSize() // set the icon size
                        .build())
                        // texto al lado del fab
                        .setLabel(getString(R.string.filter))
                        .create()
        );

        // action item info, añade icono de rutas
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.routes, MaterialDrawableBuilder.with(this.getBaseContext()) // provide a context
                        .setIcon(MaterialDrawableBuilder.IconValue.DIRECTIONS) // provide an icon
                        .setColor(Color.WHITE) // set the icon color
                        .setToActionbarSize() // set the icon size
                        .build())
                        // texto al lado del fab
                        .setLabel(getString(R.string.route))
                        .create()
        );

        // callback listener de pulsar settings o filtro
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.filter:
                        // filter action
                        boolean open = showFilterMenu(findViewById(R.id.filter));
                        return true; // cierra el fab sin animacion
                    case R.id.routes:
                        // Set the fields to specify which types of place data to
                        // return after the user has made a selection.
                        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                        // Start the autocomplete intent.
                        Intent intent = new Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.OVERLAY, fields)
                                .setCountry("ES")
                                .setTypeFilter(TypeFilter.ADDRESS)
                                .build(getApplicationContext());

                        startActivityForResult(intent, 1);

                        // cerrar el fab con animacion cuando pulsas
                        speedDialView.close();
                        return true;
                    default:
                        return true; // true to keep the Speed Dial open
                }
            }
        });

    }


    // -----------------------------------------------------------------------
    // view -> showFilterMenu -> v/f
    // -----------------------------------------------------------------------
    public boolean showFilterMenu(View anchor) {
        Log.d("pruebas", "showFilterMenu()");
        popup = new PopupMenu(this, anchor, R.style.FilterPopup);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        // Antes de mostrar el menu del popup miramos si estaba checked o no, y lo mostramos como tal
        for (int i = 0; i < showOnMap.length; i++) {
            if (showOnMap[i]) {
                // Mostramos que sea checked
                popup.getMenu().getItem(i).setChecked(true);
            }
        }
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Cambia el checked del item cuando es pulsado
                item.setChecked(!item.isChecked());

                // Keep the popup menu open -------------------------------------------------------
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                item.setActionView(new View(getBaseContext()));
                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return false;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return false;
                    }
                });
                // --------------------------------------------------------------------------------

                // El switch cambia el checked del item dependiendo del item
                switch (item.getItemId()) {
                    case R.id.coFilter:
                        if (item.isChecked()) {
                            showOnMap[0] = true;
                        }
                        else{
                            showOnMap[0] = false;
                            // borrar esas medidas
                        }
                        refrescarHeatMap();
                        return false;
                    case R.id.noxFilter:
                        if (item.isChecked()) {
                            showOnMap[1] = true;
                        }
                        else{
                            showOnMap[1] = false;
                        }
                        refrescarHeatMap();
                        return false;
                    case R.id.azufreFilter:
                        if (item.isChecked()) {
                            showOnMap[2] = true;
                        }
                        else{
                            showOnMap[2] = false;
                        }
                        refrescarHeatMap();
                        return false;
                    case R.id.ozonoFilter:
                        if (item.isChecked()) {
                            showOnMap[3] = true;
                        }
                        else{
                            showOnMap[3] = false;
                        }
                        refrescarHeatMap();
                        return false;
                    default:
                        return false;
                }
            }
        });

        return true;
    }

    // -----------------------------------------------------------------------
    // -> mostrarTodosLosGases ->
    // Hacemos checked todos los filtros al iniciar la app (aparecen todos los tipos de gas)
    // -----------------------------------------------------------------------
    public void mostrarTodosLosGases() {
        Log.d("pruebas", "mostrarTodosLosGases()");
        for (int i = 0; i < showOnMap.length; i++) {
            showOnMap[i] = true;
        }
        mostrarMedidasDeEsteTipoDeGas(2);
        mostrarMedidasDeEsteTipoDeGas(3);
        mostrarMedidasDeEsteTipoDeGas(4);
        mostrarMedidasDeEsteTipoDeGas(5);
        addHeatMap(list);
    }
    // -----------------------------------------------------------------------
    // -> alarmaQueSuenaCadaMinuto ->
    // Crea el handler para el timer de hayqueactualizarmediciones
    // A modo de prueba suena cada 10 segundos
    // -----------------------------------------------------------------------
    public void alarmaQueSuenaCadaMinuto() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                metodo_timer();
            }
        };
        runnable.run();
    }

    // -----------------------------------------------------------------------
    // -> metodo_timer ->
    // El timer
    // -----------------------------------------------------------------------
    public void metodo_timer() {
        contador++;

        // Cada 10 segundos envía al servidor
        if (contador > 10) {
            receptorBLE.BTAdapter.startLeScan(receptorBLE.callbackLeScan);
            Log.d("pruebas", "alarmaminuto");
            // Sólo se hace si está conectado al beacon y si se ha movido
         //   receptorBLE.localizadorGPS.obtenerMiPosicionGPS();
            Location posicionAnterior = receptorBLE.posicion;
            if (receptorBLE.ultimaTramaEncontrada != null && receptorBLE.localizadorGPS.meHeMovido(posicionAnterior))
                hayQueActualizarMedicionesYEnviarlasAlServidor();
            contador = 0;
        }

        // Se suma 1 cada 1000 milisegundos
        handler.postDelayed(runnable, 1000);
    }

    // -----------------------------------------------------------------------
    // -> alarmaQueSuenaCada10Minutos ->
    // Crea el handler para el timer de hayqueactualizarmediciones
    // A modo de prueba suena cada 30 segundos
    // -----------------------------------------------------------------------
    public void alarmaQueSuenaCada10Minutos() {
        handler10 = new Handler();
        runnable10 = new Runnable() {
            @Override
            public void run() {
                metodo_timer10();
            }
        };
        runnable10.run();
    }

    // -----------------------------------------------------------------------
    // -> metodo_timer10 ->
    // El timer
    // -----------------------------------------------------------------------
    public void metodo_timer10() {
        contador10++;
        // Cada 30 segundos comprueba si se ha movido
        if (contador10 > 30) {
            Log.d("pruebas", "alarma10minutos");
            // Sólo se hace si está conectado al beacon
            if (receptorBLE.ultimaTramaEncontrada != null)
                hayQueActualizarMedicionesYEnviarlasAlServidor();
            contador10 = 0;
        }

        // Se suma 1 cada 1000 milisegundos
        handler10.postDelayed(runnable10, 1000);
    }


    // -----------------------------------------------------------------------
    // -> alarmaQueSuenaCuandoEstaInactivo ->
    // Crea el handler para el timer de hayqueavisardeinactividad
    // A modo de prueba suena cuando lleva 30s inactivo
    // -----------------------------------------------------------------------
    public void alarmaQueSuenaCuandoEstaInactivo() {
        handlerInactividad = new Handler();
        runnableInactividad = new Runnable() {
            @Override
            public void run() {
                timerAlarmaQueSuenaCuandoEstaInactivo();
            }
        };
        runnableInactividad.run();
    }

    // -----------------------------------------------------------------------
    // -> timerAlarmaQueSuenaCuandoEstaInactivo ->
    // -----------------------------------------------------------------------
    public void timerAlarmaQueSuenaCuandoEstaInactivo() {
        contadorInactividad++;
        // Cada 30 segundos cambia el estado a inactivo
        if (contadorInactividad > 30) {
            Log.d("pruebas", "timerAlarmaQueSuenaCuandoEstaInactivo");
            hayQueAvisarDeInactividad();
            contadorInactividad = 0;
        }

        // Se suma 1 cada 1000 milisegundos
        handlerInactividad.postDelayed(runnableInactividad, 1000);
    }

    // -----------------------------------------------------------------------
    // idTipoDeGas: N -> mostrarMedidasDeEsteTipoDeGas ->
    // -----------------------------------------------------------------------
    public void mostrarMedidasDeEsteTipoDeGas(int idTipoDeGas) {
        int length;
        MarkerOptions option = new MarkerOptions();
        LatLng coords;
        double latitud;
        double longitud;
        double valor;

        try {
            //Recogemos el tamaño del array con las medidas en JSON
            length = medidas.getJSONArray("medidas").length();

            // Dibujamos marcadores para cada una de las medidas
            for (int i = 0; i<length; i++) {

                //Log.d("pruebas", medidas.getJSONArray("medidas").getJSONObject(i).toString());

                //Guardamos cada una de las medidas en una variable auxiliar
                JSONObject medida = medidas.getJSONArray("medidas").getJSONObject(i);

                if (medida.getInt("IdTipoMedida") == idTipoDeGas) {
                    //Log.d(TAG, "Latitud: "+medida.getString("Latitud"));
                    //Log.d(TAG, "Longitud: "+medida.getString("Longitud"));
                    // Guardamos la latitud de cada una cogiendo de la medida
                    latitud = Double.parseDouble(medida.getString("Latitud"));
                    longitud = Double.parseDouble(medida.getString("Longitud"));
                    coords = new LatLng(latitud, longitud);

                    // Guardamos el valor de la medida
                    valor = Double.parseDouble(medida.getString("Valor"));
                    list.add(new WeightedLatLng(coords, valor));
                    //Log.d("pruebas", "Valor: " + medida.getString("Valor"));
                    //Configuración del marcador
                    option.position(coords).title("UPV").draggable(true).
                            snippet("Contaminación:" + valor).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    //map.addMarker(option);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------
    // -> refrescarHeatMap ->
    // -----------------------------------------------------------------------
    public void refrescarHeatMap() {
        list.clear();
        // CO
        if (showOnMap[0]) {
            mostrarMedidasDeEsteTipoDeGas(2);
        }
        // NOX
        if (showOnMap[1]) {
            mostrarMedidasDeEsteTipoDeGas(3);
        }
        // SO2
        if (showOnMap[2]) {
            mostrarMedidasDeEsteTipoDeGas(4);
        }
        // OZONO
        if (showOnMap[3]) {
            mostrarMedidasDeEsteTipoDeGas(5);
        }
        if (!list.isEmpty()) {
            mProvider.setWeightedData(list);
            mOverlay.clearTileCache();
        }
        else {
            mOverlay.remove();
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

    public void addHeatMap(List<WeightedLatLng> l) {
        // Radius alternativo
        final int ALT_HEATMAP_RADIUS = 28;

        // Opacidad alternativa
        final double ALT_HEATMAP_OPACITY = 0.5;

        //Gradiente alternativo (green -> red) 5 niveles
        final int[] COLORS = {
                Color.argb(0, 0, 255, 0),// transparent
                Color.rgb(0, 255, 0), // green
                Color.rgb(255, 255, 0), // yellow
                Color.rgb(255, 0, 0) // red
        };
        final float[] START_POINTS = {
                0.0f,  //0-50 //transparent
                0.005f, //51-100 //green
                0.01f, //101-150 //yellow
                0.015f, //151-200 //red
        };

        final Gradient HEATMAP_GRADIENT = new Gradient(COLORS, START_POINTS);
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


    // Cuando ha seleccionado un destino de la ruta
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                // Cogemos la información del searchbar
                final Place place = Autocomplete.getPlaceFromIntent(data);

                // Quitamos el marker anterior
                if (markerDestino != null) markerDestino.remove();

                // Añadimos un markador con la búsqueda
                markerDestino = map.addMarker(new MarkerOptions().position(place.getLatLng()))/*.title(place.getName()).snippet(place.getName())).showInfoWindow()*/;

                // Hacemos que no se pueda hacer clic a este marker
                GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.getPosition().equals(place.getLatLng()))
                            // No hace nada mas
                        return true;
                            // Hace el click de siempre (para estaciones oficiales)
                        else return false;
                    }
                };
                map.setOnMarkerClickListener(onMarkerClickListener);

                // Movemos la cámara hacia el marcador
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                // Cogemos la localización actual
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                // I suppressed the missing-permission warning because this wouldn't be executed in my
                // case without location services being enabled
                @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                // Hacemos un request de rutas
                DateTime now = new DateTime();
                try {
                    DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                            .mode(TravelMode.WALKING).origin(new com.google.maps.model.LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                            .destination(new com.google.maps.model.LatLng(place.getLatLng().latitude, place.getLatLng().longitude)).departureTime(now)
                            .await();
                    Log.d("pruebas", result.toString());

                    // Calculamos la mejor ruta
                    calcularMejorRuta(result);
                    addPolyline(result, map);
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("pruebas", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        // Quitamos la ruta anterior
        if (polyline != null) polyline.remove();
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        // Pintamos la ruta en el mapa
        polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
                // Añadimos estilo a la ruta
            .color(Color.parseColor("#F88E52")).startCap(new RoundCap()).jointType(JointType.ROUND).width(20).endCap(new RoundCap()));
    }

    private void calcularMejorRuta(DirectionsResult result) throws JSONException {
        for (int i = 0; i < result.routes[0].overviewPolyline.decodePath().size(); i++) {
            com.google.maps.model.LatLng puntoRuta = result.routes[0].overviewPolyline.decodePath().get(i);
            for (int j = 0; j < medidas.getJSONArray("medidas").length(); j++) {
                JSONObject medida = medidas.getJSONArray("medidas").getJSONObject(i);
                if (Double.parseDouble(medida.getString("Valor")) > 0) {
                    float[] results = new float[1];
                    LatLng puntoContaminacion = new LatLng(Double.parseDouble(medida.getString("Latitud")), Double.parseDouble(medida.getString("Longitud")));
                    Location.distanceBetween(puntoRuta.lat, puntoRuta.lng, puntoContaminacion.latitude, puntoContaminacion.longitude, results);
                    if (results[0] < 200) {
                        // Quitar el waypoint de la ruta
                    }
                }
            }
        }
    }

}
