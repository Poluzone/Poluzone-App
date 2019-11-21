package com.example.beaconscanner;

// -----------------------------------------------------------------------
// LocalizadorGPS.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 10/10/19
// CopyRight:
// -----------------------------------------------------------------------

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocalizadorGPS {
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
                            Log.d("pruebas", "localizacion: " + ultimaPosicionMedida.getLatitude() + " " + ultimaPosicionMedida.getLongitude());
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
        float distancia = calcularDistancia(posicion);
        Log.d("pruebas", "me he movido " + distancia + " metros");
        if (distancia >= 2) return true;
        else return false;
    }

    // -----------------------------------------------------------------------
    // Posicion -> calcularDistancia -> R
    // -----------------------------------------------------------------------
    private float calcularDistancia(Location posicionAnterior) {
        // algoritmo con ultimaPosicionMedida
        float distancia = ultimaPosicionMedida.distanceTo(posicionAnterior);
        Log.d("pruebas", "anterior" + posicionAnterior.toString() + "dsp" + ultimaPosicionMedida.toString());
        return distancia;
    }

}
