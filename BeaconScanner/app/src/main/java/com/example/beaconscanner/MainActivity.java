package com.example.beaconscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// -----------------------------------------------------------------------
// MainActivity.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 01/10/19
// CopyRight:
// -----------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {

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

    // Servidor
    ServidorFake servidorFake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        textView = findViewById(R.id.text);
        textView2 = findViewById(R.id.text2);
        button = findViewById(R.id.button);
        buttonRecibir = findViewById(R.id.button2);
        textViewRecibir = findViewById(R.id.textViewDatoServer);

        // Creamos el receptorBLE indicando la actividad y el uuid que buscamos
        receptorBLE = new ReceptorBLE(this, nuestroUUID);
        Log.d("pruebas", "receptor creado");

        // Creamos el servidorFake indicando la direccion ip y el puerto
        servidorFake = new ServidorFake(this);


        // Para los botones de enviar y recibir
        button.setClickable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hayQueActualizarMedicionesYEnviarlasAlServidor();
            }
        });

        buttonRecibir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recibirMedicionesDelServidor();
            }
        });


    }

    // -----------------------------------------------------------------------
    // uuid, major -> mostrarUUID ->
    // Mostrar las lecturas del BLE por la pantalla del movil
    // -----------------------------------------------------------------------
    public void mostrarUUID (String uuid, String major) {
        button.setClickable(true);
        textView.setText("UUID del device: " + uuid);
        textView2.setText("Major: " + major);
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



}
