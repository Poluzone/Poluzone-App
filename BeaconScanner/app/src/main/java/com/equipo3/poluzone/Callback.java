package com.equipo3.poluzone;

import org.json.JSONObject;

public interface Callback {
    public void callbackLogin (boolean resultadoLogin, JSONObject response);
    public void callbackMediaCalidadAire (double media);

}
