package com.example.beaconscanner;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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

    String IP = "192.168.1.21";
    int puerto = 8080;

    // ---------------------------------------------------------------------------
    // Constructor
    // IP, puerto -> ServidorFake() ->
    // ---------------------------------------------------------------------------
    public ServidorFake(Activity activity) {

        Log.d("pruebas", "constructor ServidorFake()");

        this.activity = activity;

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(activity);
    }

    // ---------------------------------------------------------------------------
    // Medida -> guardarContaminacion() ->
    // ---------------------------------------------------------------------------
    public void guardarContaminacion(Medida medidaContaminacion)  {
        Log.d("pruebas", "guardarContaminacion()");
        String url = "http://"+IP+":"+puerto+"/guardarContaminacion/"; /*+medidaContaminacion.getMedida(); */

        JSONObject datos = new JSONObject();

        // Anyadimos los datos al json
        try {
            datos.put("valor", medidaContaminacion.getMedida());
            datos.put("tiempo", medidaContaminacion.getTiempo());
            datos.put("lat", medidaContaminacion.getPosicion().getLatitude());
            datos.put("long", medidaContaminacion.getPosicion().getLongitude());
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
                          //  activity.mostrarDelServidor(medida);

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
    public void insertarUsuario(String email, String password, int telefono) {
        Log.d("pruebas", "insertarUsuario()");
        String url = "http://"+IP+":"+puerto+"/insertarUsuario/";

        JSONObject datos = new JSONObject();


        // Anyadimos los datos al json
        try {
            datos.put("IdUsuario", null);
            datos.put("email", email);
            datos.put("password", password);
            datos.put("telefono", telefono);
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
                        Intent i = new Intent(activity, MainActivity.class);
                        Log.d("pruebas", "intent main");
                        activity.startActivity(i);
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
    // -> cerrarConexion() ->
    // ---------------------------------------------------------------------------
    public void cerrarConexion() {

    }
}



