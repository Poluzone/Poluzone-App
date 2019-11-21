package com.example.beaconscanner;

import org.json.JSONObject;

public interface CallbackRegistro {
    public void callbackRegistro (boolean resultadoRegistro, JSONObject response);
    public void callbackId(boolean result, String entrada);
}
