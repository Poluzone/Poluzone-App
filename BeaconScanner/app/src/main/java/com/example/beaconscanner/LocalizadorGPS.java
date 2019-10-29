package com.example.beaconscanner;

// -----------------------------------------------------------------------
// LocalizadorGPS.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 10/10/19
// CopyRight:
// -----------------------------------------------------------------------

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocalizadorGPS extends AsyncTask<Void, Void, Location> {
    private Location ultimaPosicionMedida;
    private FusedLocationProviderClient fusedLocationClient;
    private NavigationDrawerActivity activity;

    // -----------------------------------------------------------------------
    // -> getUltimaPosicionMedida -> Location
    // -----------------------------------------------------------------------
    public Location getUltimaPosicionMedida() {
        return ultimaPosicionMedida;
    }

    // -----------------------------------------------------------------------
    // -> Constructor ->
    // -----------------------------------------------------------------------
    public LocalizadorGPS(NavigationDrawerActivity activity) {
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        ultimaPosicionMedida = new Location("");
        ultimaPosicionMedida.setLatitude(1234);
        ultimaPosicionMedida.setLongitude(1235);
        this.execute();
    }

    // -----------------------------------------------------------------------
    // -> obtenerMiPosicionGPS ->
    // -----------------------------------------------------------------------
    public void obtenerMiPosicionGPS() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            ultimaPosicionMedida = location;
                        }
                    }
                }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("pruebas", e.toString());
            }
        });
    }

    // -----------------------------------------------------------------------
    // Posicion -> meHeMovido -> V/F
    // -----------------------------------------------------------------------
    public boolean meHeMovido(Location posicion) {
        calcularDistancia(posicion);

        // algoritmo

        return true;
    }

    // -----------------------------------------------------------------------
    // Posicion -> calcularDistancia -> N
    // -----------------------------------------------------------------------
    private int calcularDistancia(Location posicionAnterior) {
        // algoritmo con ultimaPosicionMedida

        return 0;
    }



    // No funcional
    @Override
    protected Location doInBackground(Void... voids) {
        obtenerMiPosicionGPS();
        // Log.d("pruebas", "" + ultimaPosicionMedida);
        while (ultimaPosicionMedida.getLatitude()==0) { }
        Log.d("pruebas", "doinbackground terminado");
        return ultimaPosicionMedida;
    }

    @Override
    protected void onPostExecute(Location posicion) {
        ultimaPosicionMedida = posicion;
    }
}
