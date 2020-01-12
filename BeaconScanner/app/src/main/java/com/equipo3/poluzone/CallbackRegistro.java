// -----------------------------------------------------------------------
// CallbackRegistro.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 10/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone;

import org.json.JSONObject;

public interface CallbackRegistro {
    public void callbackRegistro (boolean resultadoRegistro, JSONObject response);
    public void callbackUsuario(boolean result, JSONObject entrada);
}
