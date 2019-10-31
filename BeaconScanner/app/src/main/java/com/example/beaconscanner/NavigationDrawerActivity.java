package com.example.beaconscanner;

//Imports para hacer imagen redonda
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class NavigationDrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    SpeedDialView speedDialView;

    Integer[] showOnMap = new Integer[4];

    // Bluetooth
    public String nuestroUUID = "EQUIPO-3XURODIMI";
    ReceptorBLE receptorBLE;

    // Mostrar por pantalla
    public TextView textView;
    public TextView textView2;
    public TextView textViewRecibir;

    // Interfaz
    public Button button;
    public Button buttonRecibir;

    // Para recordar que se ha logeado
    private SharedPreferences loginPreferences;

    // Servidor
    ServidorFake servidorFake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);


        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);


        //Funcion que redondea la imagen del menú
        //redondearImagen();

        // Codigo relacionado con el navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mostrarTodosLosGases();
        crearFabSpeedDial();

        /*FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_foto, R.id.nav_ajustes, R.id.nav_perfil, R.id.nav_sesion)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // /Codigo relacionado con el navigation drawer

        //.............................................................................
        // Backend
        //.............................................................................


            // Creamos el receptorBLE indicando la actividad y el uuid que buscamos
            receptorBLE = new ReceptorBLE(this, nuestroUUID);
            Log.d("pruebas", "receptor creado");

            // Creamos el servidorFake indicando la direccion ip y el puerto
            servidorFake = new ServidorFake(this);

        //.............................................................................
        // /Backend
        //.............................................................................

    }

    //------------------------------------------------------------------------------
    // Método de la actividad del Navigation Drawer
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // -----------------------------------------------------------------------
    // uuid, major -> mostrarUUID ->
    // Mostrar las lecturas del BLE por la pantalla del movil
    // -----------------------------------------------------------------------
    public void mostrarUUID (String uuid, String major) {
        /*button.setClickable(true);
        textView.setText("UUID del device: " + uuid);
        textView2.setText("Major: " + major);*/
    }

    // -----------------------------------------------------------------------
    // -> hayQueActualizarMedicionesYEnviarlasAlServidor ->
    // Enviar las mediciones al servidor
    // -----------------------------------------------------------------------
    public void hayQueActualizarMedicionesYEnviarlasAlServidor() {
        Medida medida = receptorBLE.obtenerContaminacion();
        Log.d ("pruebas", "valor: " + medida.getMedida() + " tiempo: " + medida.getTiempo() + " lati: " + medida.getPosicion().getLatitude());
        servidorFake.guardarContaminacion(medida);
    }

    // -----------------------------------------------------------------------
    // -> recibirMedicionesDelServidor ->
    // Recibir las mediciones del servidor
    // -----------------------------------------------------------------------
    public void recibirMedicionesDelServidor () {
        servidorFake.getContaminacion();
    }


    // -----------------------------------------------------------------------
    // Medida -> mostrarDelServidor ->
    // Mostrar las lecturas del servidor por la pantalla del movil
    // -----------------------------------------------------------------------
    public void mostrarDelServidor(Medida medida) {
        textViewRecibir.setText("Dato del servidor: \n" + "Valor: "+ medida.getMedida() + "\nPosicion: " + medida.getPosicion().getLatitude() + " " + medida.getPosicion().getLongitude() + "\nTiempo: " + medida.getTiempo() );
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
        // ---------- FAB SPEED DIAL ------------------------------------------------------------------------------------

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
                switch(item.getItemId()){
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

}
