package com.example.beaconscanner;

//Imports para hacer imagen redonda
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

}
