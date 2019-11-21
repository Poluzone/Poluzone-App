package com.example.beaconscanner;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

// -----------------------------------------------------------------------
// ServidorFake.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 13/10/19
// CopyRight:
// -----------------------------------------------------------------------


public class ServidorFake {

    Activity activity;

    RequestQueue queue;
    CallbackLogin callbackLogin;
    CallbackRegistro callbackRegistro;
    //String IP = "192.168.1.107";
    //String IP = "192.168.0.104"; //Red Matthew
    String IP = "192.168.1.110"; //Red Rosa
   //  "172.20.10.5";
    int puerto = 8080;
    private String id;
    private SharedPreferences loginPreferences;

    // ---------------------------------------------------------------------------
    // Constructor
    // IP, puerto -> ServidorFake() ->
    // ---------------------------------------------------------------------------
    public ServidorFake(Activity activity) {


        Log.d("pruebas", "constructor ServidorFake()");

        this.activity = activity;

        // Si el servidor se ha creado desde loginactivity buscamos el callback
        if (activity.getClass() == LoginActivity.class) {
            Log.d("pruebas", "issa loginactivity");
            callbackLogin = (LoginActivity) activity;
        }

        // Si el servidor se ha creado desde loginactivity buscamos el callback
        if (activity.getClass() == RegistrarUsuarioActivity.class) {
            Log.d("pruebas", "issa loginactivity");
            callbackRegistro = (RegistrarUsuarioActivity) activity;
        }

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(activity);
    }

    // ---------------------------------------------------------------------------
    // Medida -> insertarMedida() ->
    // ---------------------------------------------------------------------------
    public void insertarMedida(Medida medidaContaminacion)  {
        Log.d("pruebas", "guardarContaminacion()");
        String url = "http://"+IP+":"+puerto+"/insertarMedida/"; /*+medidaContaminacion.getMedida(); */

        JSONObject datos = new JSONObject();

        loginPreferences = activity.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
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
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas",response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebas",error.toString());
                    }
                }
        );

        queue.add(jsonobj);
    }

    // ---------------------------------------------------------------------------
    // -> getContaminacion() ->
    // ---------------------------------------------------------------------------
    public void getContaminacion() {
        final Medida medida = new Medida();
        Log.d("pruebas", "getContaminacion()");
        String url = "http://"+IP+":"+puerto+"/contaminacion";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("pruebas","Response is: " + response);
                        JSONObject object;
                        try {
                            object = new JSONObject(response);
                            medida.setMedida((float)object.getDouble("valor"));
                            medida.setTiempo((long)object.getDouble("tiempo"));
                            int lati = object.getInt("lat");
                            int longi = object.getInt("long");
                            Location posicion = new Location("");
                            posicion.setLongitude(longi);
                            posicion.setLatitude(lati);
                            medida.setPosicion(posicion);
                           // activity.mostrarDelServidor(medida);


                        }catch (JSONException e) {
                            Log.e("pruebas", e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("pruebas","That didn't work! " + error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    // ---------------------------------------------------------------------------
    // mail:texto, password:texto, telefono:N -> insertarUsuario() ->
    // ---------------------------------------------------------------------------
    public void insertarUsuario(String email, String password, int telefono,String tipoUsuario) {
        Log.d("pruebas", "insertarUsuario()");
        String url = "http://"+IP+":"+puerto+"/insertarUsuario/";

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
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pruebas",response.toString());
                        try {
                            if (response.get("status").equals(true)) {
                                callbackRegistro.callbackRegistro(true, response);
                            }
                            else {
                                callbackRegistro.callbackRegistro(false, response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("pruebas", e.toString());
                            callbackRegistro.callbackRegistro(false, null);
                        }
                    //    callbackLogin.callbackLogin(true, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("pruebas",error.toString());
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            callbackRegistro.callbackRegistro(false, null);
                        }
                        else if (networkResponse.statusCode == 404){
                            Log.d("pruebas",networkResponse.statusCode + "");
                            callbackRegistro.callbackRegistro(false, null);

                        }
                        else if (networkResponse.statusCode == 401){
                            Log.d("pruebas",networkResponse.statusCode + "");
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
    public void comprobarUsuarioPorEmail (String email, String pass) {
        Log.d("pruebas", "GetUsuarioPorEmail()");
        String url = "http://"+IP+":"+puerto+"/ComprobarLogin";

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
                        Log.d("pruebas",response.toString());
                        try {
                            if (response.get("status").equals(true)) {
                                callbackLogin.callbackLogin(true, response);
                            }
                            else {
                                callbackLogin.callbackLogin(false, response);
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
                        Log.d("pruebas",error.toString());
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            callbackLogin.callbackLogin(false, null);
                        }
                        else if (networkResponse.statusCode == 404){
                            Log.d("pruebas",networkResponse.statusCode + "");
                            JSONObject response = new JSONObject();
                            callbackLogin.callbackLogin(false, response);
                        }
                        else if (networkResponse.statusCode == 401){
                            JSONObject object = new JSONObject();
                            Log.d("pruebas",networkResponse.statusCode + "");
                            callbackLogin.callbackLogin(false, object);
                        }
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }


    /**
     * getUsuario : devuelve el id del usuario.
     * email: string ->
     *                  getIdUsuario()
     *                                  -> idUsuario: N
     *
     * @param email
     *
     *  - Matthew Conde Oltra -
     */
    public void getUsuario (String email) {
        Log.d("GETUSUARIO", "GetUsuario() con"+email);
        String url = "http://"+IP+":"+puerto+"/GetUsuarioPorEmail";

        JSONObject datos = new JSONObject();

        // Anyadimos los datos al json
        try {
            datos.put("Email", email);

            Log.d("GETUSUARIO", datos.toString());
        } catch (JSONException e) {
            Log.d("GETUSUARIOERROR", "Error de email:"+e.toString());
        }

        // Hacemos la peticion
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("GETUSUARIO",response.toString());
                        if(response == null)
                        {
                            // No hace nada
                        }
                        else
                        {
                            callbackRegistro.callbackUsuario(true, response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("GETUSUARIO",error.toString());

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
    public void vincularIDdeUsuarioConSensor(int idUsuario, int idSensor) {
        Log.d("pruebas", "vincularIDdeUsuarioConSensor()");
        String url = "http://"+IP+":"+puerto+"/insertarIdUsuarioConIdsensor";

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
                        Log.d("pruebasIDUSuario",response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pruebasIDUSuario",error.toString());
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }

    /**
     * Esta función sirve para comparar caracteres, uno a uno de forma que te diga si son o no numericos.
     *
     * cadena: string ->
     *                   isNumeric()
     *                              -> boolean
     * @param cadena
     * @return
     *
     *  - Matthew Conde Oltra -
     */
    public static boolean isNumeric(String cadena) {

        boolean resultado;

        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }
    // ---------------------------------------------------------------------------
    // -> cerrarConexion() ->
    // ---------------------------------------------------------------------------
    public void cerrarConexion() {

    }
}
