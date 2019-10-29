package com.example.beaconscanner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

// -----------------------------------------------------------------------
// ReceptorBLE.java
// Receptor de dispositivos BLE para obtener los datos del sparkfun
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 01/10/19
// CopyRight:
// -----------------------------------------------------------------------

public class ReceptorBLE {

    private NavigationDrawerActivity nuestroActivity;
    public LocalizadorGPS localizadorGPS;

    // Bluetooth variables
    public Intent enableBT;
    public BluetoothAdapter BTAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    public BluetoothAdapter.LeScanCallback callbackLeScan = null;

    // Beacon variables
    public String nuestroUUID;
    public TramaIBeacon ultimaTramaEncontrada;

    // Medida variables
    public long instante;
    public Location posicion;


    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
    public ReceptorBLE(NavigationDrawerActivity activity, String uuid) {
        // Check if phone uses bluetooth
        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        this.nuestroActivity = activity;
        this.nuestroUUID = uuid;

        // Si está el bluetooth desactivado, lo activamos
        if (!estaElBluetoothActivado()) activarBluetooth();
        actualizarMediciones();

        localizadorGPS = new LocalizadorGPS(nuestroActivity);
        Log.d("pruebas", "constructor de receptorble");
    }


    // -----------------------------------------------------------------------
    // -> actualizarMediciones ->
    // -----------------------------------------------------------------------
    private void actualizarMediciones() {
        Log.d("pruebas", "actualizarmediciones()");
        callbackLeScan = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] bytesTrama) {
                TramaIBeacon tramaIBeacon = new TramaIBeacon(bytesTrama);
                //Log.d("pruebas", "device scanned: " + Utilidades.bytesToString(tramaIBeacon.getUUID()));

                if (buscarEsteDispositivoBTLE(Utilidades.bytesToString(tramaIBeacon.getUUID()))) {
                    Log.d("pruebas", "device found: " + nuestroUUID + " " + Utilidades.bytesToInt(tramaIBeacon.getMajor()));

                    // asignamos los datos de nuestro device encontrado
                    ultimaTramaEncontrada = new TramaIBeacon(bytesTrama);

                    posicion = localizadorGPS.getUltimaPosicionMedida();
                    //Log.d("pruebas", Double.toString(posicion.getLatitude()));

                /*    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    instante = simpleDateFormat.format(new Date()); */

                    instante = System.currentTimeMillis();
                    nuestroActivity.mostrarUUID(Utilidades.bytesToString(ultimaTramaEncontrada.getUUID()), Integer.toString(Utilidades.bytesToInt(ultimaTramaEncontrada.getMajor())));

                    // !! COMENTAR DESPUES DE LAS PRUEBAS
                    // stopEscanearDispositivosBLE();
                }
            }
        };

        // Empieza el scan (callback)
        BTAdapter.startLeScan(callbackLeScan);

    }


    // -----------------------------------------------------------------------
    // texto:uuid -> buscarEsteDispositivoBLE() -> v/f
    // -----------------------------------------------------------------------
    private boolean buscarEsteDispositivoBTLE (String uuid) {
        if (uuid.equals(nuestroUUID)) return true;
        else return false;
    }


    // -----------------------------------------------------------------------
    // -> estaElBluetoothActivado -> bool
    // -----------------------------------------------------------------------
    public boolean estaElBluetoothActivado() {
        if (!BTAdapter.isEnabled()) return false;
        else return true;
    }


    // -----------------------------------------------------------------------
    // -> activarBluetooth ->
    // -----------------------------------------------------------------------
    public void activarBluetooth() {
        // Permisos de bluetooth y localizacion
        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(nuestroActivity.getBaseContext())
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert);
        }

        // Check si esta el bluetooth activado
        if (!estaElBluetoothActivado()) {
            // Intenta encender bluetooth
            enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            nuestroActivity.startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(nuestroActivity.getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(nuestroActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(nuestroActivity,
                        new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }
        } else {
            // Permission has already been granted
        }

        // Fin permisos
    }


    // -----------------------------------------------------------------------
    // -> obtenerContaminacion -> Medida
    // -----------------------------------------------------------------------
    public Medida obtenerContaminacion() {
        Log.d("pruebas", "obtenerContaminacion()");
        Medida medida = new Medida();
        medida.setMedida(Utilidades.bytesToInt(ultimaTramaEncontrada.getMajor()));
        medida.setPosicion(posicion);
        medida.setTiempo(instante);
        return medida;
    }


    // -----------------------------------------------------------------------
    // -> stopEscanearDispositivosBLE() ->
    // -----------------------------------------------------------------------
    public void stopEscanearDispositivosBLE() {
        Log.d("pruebas", "stopEscanearDispositivosBLE()");
        BTAdapter.stopLeScan(callbackLeScan);
    }

}
