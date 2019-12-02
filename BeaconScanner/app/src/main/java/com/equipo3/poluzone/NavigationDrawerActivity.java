package com.equipo3.poluzone;

import android.content.SharedPreferences;
import android.graphics.Color;


import android.location.Location;
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

import com.google.android.material.navigation.NavigationView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

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

    Integer[] showOnMap = new Integer[4];

    // Bluetooth
    public String nuestroUUID = "EQUIPO-3XURODIMI";
    ReceptorBLE receptorBLE;

    // Interfaz
    public Button button;
    public Button buttonRecibir;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        tipoUser = loginPreferences.getString("tipousuario", "o");
        idUser = loginPreferences.getInt("idUsuario", 0);

        Log.d("pruebasssss", tipoUser);

        //Funcion que redondea la imagen del menú
        //redondearImagen();

        // Codigo relacionado con el navigation drawer
        Toolbar toolbar;

        DrawerLayout drawer;
        NavigationView navigationView;

        // Creamos el servidorFake indicando la direccion ip y el puerto
        servidorFake = new ServidorFake(this);

        NavController navController;

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
        // /Codigo relacionado con el navigation drawer

        crearFabSpeedDial();
        mostrarTodosLosGases();

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
        receptorBLE.ultimaTramaEncontrada = null;
    }

    // -----------------------------------------------------------------------
    // -> hayQueAvisarDeInactividad ->
    // Avisar de inactividad del nodo al servidor
    // -----------------------------------------------------------------------
    public void hayQueAvisarDeInactividad() {
        String actividad = "Inactivo";
        servidorFake.indicarActividadNodo(actividad);
    }

    //--------------------------------------------------------------------------
    //En el caso de tener que redondear la imagen de perfil. No está comprobada la funcionalidad de este método porque todavía no ha surgido el problema
    //--------------------------------------------------------------------------
    /*void redondearImagen (){

        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.perfil);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setImageDrawable(roundedDrawable);

    }*/


    // -----------------------------------------------------------------------
    // -> crearFabSpeedDial ->
    // -----------------------------------------------------------------------
    private void crearFabSpeedDial() {
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
                        // info action
                        //startInfoActivity();
                        //presentActivity(findViewById(R.id.info));
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
        PopupMenu popup = new PopupMenu(this, anchor, R.style.FilterPopup);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        // Antes de mostrar el menu del popup miramos si estaba checked o no, y lo mostramos como tal
        for (int i = 0; i < showOnMap.length; i++) {
            if (showOnMap[i] == 1) {
                // Mostramos que sea checked
                popup.getMenu().getItem(i).setChecked(true);
            }
        }
        popup.show();


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // cambia el checked del item cuando es pulsado
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
                // -- falta implementar el filtrado real de los contenedores
                switch (item.getItemId()) {
                    case R.id.ozonoFilter:
                        if (item.isChecked()) showOnMap[0] = 1;
                        else showOnMap[0] = 0;
                        return false;
                    case R.id.irritantesFilter:
                        if (item.isChecked()) showOnMap[1] = 1;
                        else showOnMap[1] = 0;
                        return false;
                    case R.id.calidadFilter:
                        if (item.isChecked()) showOnMap[2] = 1;
                        else showOnMap[2] = 0;
                        return false;
                    case R.id.so2Filter:
                        if (item.isChecked()) showOnMap[3] = 1;
                        else showOnMap[3] = 0;
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
        for (int i = 0; i < showOnMap.length; i++) {
            showOnMap[i] = 1;
        }
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

}
