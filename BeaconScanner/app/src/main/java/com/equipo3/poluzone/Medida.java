package com.equipo3.poluzone;

// -----------------------------------------------------------------------
// Medida.java
// POJO de medida
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 10/10/19
// CopyRight:
// -----------------------------------------------------------------------

import android.location.Location;

public class Medida {
    private float medida;
    private long tiempo;
    private Location posicion;

    public float getMedida() {
        return medida;
    }

    public void setMedida(float medida) {
        this.medida = medida;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    public Location getPosicion() {
        return posicion;
    }

    public void setPosicion(Location posicion) {
        this.posicion = posicion;
    }

}
