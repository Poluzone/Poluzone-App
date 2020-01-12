package com.equipo3.poluzone;

import android.app.FragmentManager;
import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.equipo3.poluzone.ui.inicio.InicioConductorFragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

// -----------------------------------------------------------------------
// ServidorFake.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 13/10/19
// CopyRight:
// -----------------------------------------------------------------------


public class ServidorFake {

    android.app.Activity activity;

    RequestQueue queue;
    public Callback callback;
    CallbackRegistro callbackRegistro;
    //Callback callbackMedidas;
    CallbackMisMedidas callbackMisMedidas;

    //String IP = "192.168.1.107";
    //String IP = "192.168.43.125"; //Red Matthew
    //String IP = "192.168.43.18"; //Red Rosa
    //String URL = "http://192.168.0.109:8080";
    String URL = "https://juconol.upv.edu.es"; //Red Matthew
    //  "172.20.10.5";
    int puerto = 8080;
    private SharedPreferences loginPreferences;

    // ---------------------------------------------------------------------------
    // Constructor
    // activity -> ServidorFake() ->
    // ---------------------------------------------------------------------------
    public ServidorFake(android.app.Activity activity) {


        Log.d("pruebas", "constructor ServidorFake()");

        this.activity = activity;
        loginPreferences = activity.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Si el servidor se ha creado desde loginactivity buscamos el callback
        if (activity.getClass() == LoginActivity.class) {
            Log.d("pruebas", "issa loginactivity");
            callback = (LoginActivity) activity;
        }

        // Si el servidor se ha creado desde loginactivity buscamos el callback
        if (activity.getClass() == RegistrarUsuarioActivity.class) {
            Log.d("pruebas", "issa registrousuarioactivity");
            callbackRegistro = (RegistrarUsuarioActivity) activity;
        }
        // Si el servidor se ha creado desde loginactivity buscamos el callback
        if (activity.getClass() == MainMisMedidas.class) {
            Log.d("pruebas", "issa registrousuarioactivity");
            callbackMisMedidas = (MainMisMedidas) activity;
        }

        // Si el servidor se ha creado desde el inicio buscamos el callback
    /*    if (activity.getClass() == NavigationDrawerActivity.class) {
            Log.d("pruebas", "issa navdraweractivity");
            FragmentManager fragmentManager = activity.getFragmentManager();
            callback = fragmentManager.findFragmentById(R.id.nav_inicio);
        }*/

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(activity);
    }

    // ---------------------------------------------------------------------------
    // Medida -> insertarMedida() ->
    // ---------------------------------------------------------------------------
    public void insertarMedida(Medida medidaContaminacion) {
        Log.d("pruebas", "guardarContaminacion()");
        String url = URL + "/insertarMedida/"; /*+medidaContaminacion.getMedida(); */

        JSONObject datos = new JSONObject();

        int idUsuario = loginPreferences.getInt("idUsuario", 0);

        // Anyadimos los datos al json
        try {
            datos.put("Valor", medidaContaminacion.getMedida());
            datos.put("Tiempo", medidaContaminacion.getTiempo());
            datos.put("Latitud", medidaContaminacion.getPosicion().getLatitude());
            datos.put("Longitud", medidaContaminacion.getPosicion().getLongitude());
            datos.put("IdTipoMedida", 2);
            datos.put("IdUsuario", idUsuario);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebas", error.toString());
                    }
                }
        );

        queue.add(jsonobj);
    }

    // ------------------------------------------------------------------------------
    // desde: N, hasta: N, IdUsuario: N -> getMediaCalidadDelAireDeLaJornada() -> R
    // ------------------------------------------------------------------------------
    public void getMediaCalidadDelAireDeLaJornada(long desde, long hasta, int id) {
        Log.d("pruebas", "getMediaCalidadDelAireDeLaJornada()");
        String url = URL + "/getMediaCalidadDelAireDeLaJornada";

        // Creamos el intervalo de tiempo
        JSONObject intervalo = new JSONObject();
        try {
            intervalo.put("desde", desde);
            intervalo.put("hasta", hasta);
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Anyadimos los datos al json
        JSONObject datos = new JSONObject();
        try {
            datos.put("Intervalo", intervalo);
            datos.put("IdUsuario", id);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas", response.toString());
                        try {
                            callback.callbackMediaCalidadAire(response.getDouble("media"));
                        } catch (JSONException e) {
                            Log.d("pruebas", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebas", error.toString());
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }

    // ------------------------------------------------------------------------------
    // num: N , IdUsuario: N -> getUltimasNMedicionesPorUsuario() -> JSON
    // ------------------------------------------------------------------------------
    public void getMedidasPorUsuario(int num,int id) {
        Log.d("pruebas", "getUltimasNMedicionesPorUsuario()");
        String url = URL + "/getUltimasNMedicionesPorUsuario";

        // Anyadimos los datos al json
        JSONObject datos = new JSONObject();
        try {
            datos.put("num", num);
            datos.put("idUsuario", id);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas", response.toString());

                        callbackMisMedidas.callbackMisMedidas(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebas", error.toString());
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }


    // ---------------------------------------------------------------------------
    // mail:texto, password:texto, telefono:N -> insertarUsuario() ->
    // ---------------------------------------------------------------------------
    public void insertarUsuario(String email, String password, int telefono, String tipoUsuario) {
        Log.d("pruebas", "insertarUsuario()");
        String url = URL + "/insertarUsuario/";

        JSONObject datos = new JSONObject();


        // Anyadimos los datos al json
        try {
            datos.put("Email", email);
            datos.put("Password", password);
            datos.put("Telefono", telefono);
            datos.put("TipoUsuario", tipoUsuario);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas", response.toString());
                        try {
                            if (response.get("status").equals(true)) {
                                callbackRegistro.callbackRegistro(true, response);
                            } else {
                                callbackRegistro.callbackRegistro(false, response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("pruebas", e.toString());
                            callbackRegistro.callbackRegistro(false, null);
                        }
                        //    callback.callback(true, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("pruebas", error.toString());
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            callbackRegistro.callbackRegistro(false, null);
                        } else if (networkResponse.statusCode == 404) {
                            Log.d("pruebas", networkResponse.statusCode + "");
                            callbackRegistro.callbackRegistro(false, null);

                        } else if (networkResponse.statusCode == 401) {
                            Log.d("pruebas", networkResponse.statusCode + "");
                            callbackRegistro.callbackRegistro(false, null);
                        }
                    }
                }
        );

        queue.add(jsonobj);
    }


    // ---------------------------------------------------------------------------
    // email, pass -> comprobarUsuarioPorEmail() ->
    // ---------------------------------------------------------------------------
    public void comprobarUsuarioPorEmail(String email, String pass) {
        Log.d("pruebas", "GetUsuarioPorEmail()");
        String url = URL + "/ComprobarLogin";

        JSONObject datos = new JSONObject();


        // Anyadimos los datos al json
        try {
            datos.put("Email", email);
            datos.put("Password", pass);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas", response.toString());
                        try {
                            if (response.get("status").equals(true)) {
                                callback.callbackLogin(true, response);
                            } else {
                                callback.callbackLogin(false, response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("pruebas", e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("pruebas", error.toString());
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            callback.callbackLogin(false, null);
                        } else if (networkResponse.statusCode == 404) {
                            Log.d("pruebas", networkResponse.statusCode + "");
                            JSONObject response = new JSONObject();
                            callback.callbackLogin(false, response);
                        } else if (networkResponse.statusCode == 401) {
                            JSONObject object = new JSONObject();
                            Log.d("pruebas", networkResponse.statusCode + "");
                            callback.callbackLogin(false, object);
                        }
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }

    /**
     * getTodasLasMedidasPorFecha : devuelve todas las medidas desde, hasta una fecha
     * desde: N, hasta: N ->
     * getTodasLasMedidasPorFecha()
     * -> Medidas: JSON
     * <p>
     * - Matthew Conde Oltra -
     */
    public void getTodasLasMedidasPorFecha(long desde, long hasta) {
        Log.d("MEDIDAS", "/GetTodasLasMedidasPorFecha");
        String url = URL + "/GetTodasLasMedidasPorFecha";

        // Creamos el intervalo de tiempo
        JSONObject intervalo = new JSONObject();
        try {
            intervalo.put("desde", desde);
            intervalo.put("hasta", hasta);
        } catch (JSONException e) {
            Log.d("TODASMEDIDAS", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, intervalo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("SERVIDOR", response.toString());

                        if (response == null) {
                            // No hace nada
                            Log.d("SERVIDOR", "Response es null");
                            callback.callbackMedidas(false, response);
                        } else {
                            callback.callbackMedidas(true, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("ERRORSERVIDOR", error.toString());

                    }
                }
        );
        queue.add(jsonobj);
    }

    /**
     * getEstacionesOficiales : devuelve estaciones oficiales de la BBDD
     * <p>
     * getEstacionesOficiales() -> Estaciones: JSON
     * <p>
     * - Matthew Conde Oltra -
     */
    public void getEstacionesOficiales() {
        Log.d("MEDIDAS", "/GetEstacionesOficiales");
        String url = URL + "/getEstacionesOficiales";


        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("SERVIDOR", response.toString());

                        if (response == null) {
                            // No hace nada
                            Log.d("SERVIDOR", "Response es null");
                            callback.callbackEstaciones(false, response);
                        } else {
                            callback.callbackEstaciones(true, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("ERRORSERVIDOR", error.toString());

                    }
                }
        );
        queue.add(jsonobj);
    }

    /**
     * getUsuario : devuelve el id del usuario.
     * email: string ->
     * getUsuario()
     * -> idUsuario: N
     *
     * @param email - Matthew Conde Oltra -
     */
    public void getUsuario(String email) {
        Log.d("GETUSUARIO", "GetUsuario() con" + email);
        String url = URL + "/GetUsuarioPorEmail";

        JSONObject datos = new JSONObject();

        // Anyadimos los datos al json
        try {
            datos.put("Email", email);

            Log.d("GETUSUARIO", datos.toString());
        } catch (JSONException e) {
            Log.d("GETUSUARIOERROR", "Error de email:" + e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("GETUSUARIO", response.toString());
                        if (response == null) {
                            // No hace nada
                        } else {
                            callbackRegistro.callbackUsuario(true, response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("GETUSUARIO", error.toString());

                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
        //Log.d("GETUSUARIO", id);
        //return id;
    }

    // ---------------------------------------------------------------------------
    // idUsuario, idSensor -> vincularIDdeUsuarioConSensor() ->
    // ---------------------------------------------------------------------------
    public void vincularIDdeUsuarioConSensor(int idUsuario, final int idSensor) {
        Log.d("pruebas", "vincularIDdeUsuarioConSensor()");
        String url = URL + "/insertarIdUsuarioConIdsensor";

        JSONObject datos = new JSONObject();


        // Anyadimos los datos al json
        try {
            datos.put("IdUsuario", idUsuario);
            datos.put("IdSensor", idSensor);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebasIDUSuario", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebasIDUSuario", response.toString());
                        loginPreferences.edit().putInt("idSensor", idSensor);
                        loginPreferences.edit().commit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebasIDUSuario", error.toString());
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }


    // ---------------------------------------------------------------------------
    // activo/inactivo: texto -> indicarActividadNodo() ->
    // ---------------------------------------------------------------------------
    public void indicarActividadNodo(String activo) {
        Log.d("pruebas", "indicarActividadNodo()");
        String url = URL + "/indicarActividadNodo";

        int idSensor = loginPreferences.getInt("idSensor", 0);

        JSONObject datos = new JSONObject();

        // Anyadimos los datos al json
        try {
            datos.put("idSensor", idSensor);
            datos.put("estado", activo);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebas", error.toString());
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }

    // ---------------------------------------------------------------------------
    // imagen -> insertarImagen() ->
    // ---------------------------------------------------------------------------
    public void insertarImagen(Bitmap imageBitmap) throws IOException {
        //create a file to write bitmap data
        File f = new File(activity.getBaseContext().getCacheDir(), "imagen.png");
        f.createNewFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // Comprimimos la imagen
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] bitmapdata = bos.toByteArray();
        final String encodedImage = Base64.encodeToString(bitmapdata, Base64.DEFAULT);

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        String url = URL + "/insertarImagen/";
        Log.d("pruebas", url);

        JSONObject datos = new JSONObject();

        // Anyadimos los datos al json
        try {
            datos.put("imagen", encodedImage);
            Log.d("pruebas json", datos.toString());
        } catch (JSONException e) {
            Log.d("pruebas", e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebas", error.toString());
                    }
                }
        );

        queue.add(jsonobj);
    }

}
