package com.example.beaconscanner;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    String IP = "192.168.1.104";//"172.20.10.5";
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
                            if (response.get("status").equals("true")) {
                                Intent i = new Intent(activity, MainActivity.class);
                                Log.d("pruebas", "intent main");
                                activity.startActivity(i);
                            }
                            else {
                                TextInputLayout inputEmailLayout = activity.findViewById(R.id.texto_email_layout);
                                inputEmailLayout.setError(" ");

                                TextInputLayout inputPassLayout = activity.findViewById(R.id.texto_password_layout);
                                inputPassLayout.setError("Email y/o contraseña incorrecta");
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
                        if (error instanceof NoConnectionError) {
                            TextInputLayout inputPassLayout = activity.findViewById(R.id.texto_password_layout);
                            Toast.makeText(activity, "Error de conexión", Toast.LENGTH_LONG).show();
                        }
                        else if (networkResponse.statusCode == 404){
                            Log.d("pruebas",networkResponse.statusCode + "");
                            TextInputLayout inputPassLayout = activity.findViewById(R.id.texto_password_layout);
                            inputPassLayout.setError("Email y/o contraseña incorrecta");
                            TextInputLayout inputEmailLayout = activity.findViewById(R.id.texto_email_layout);
                            inputEmailLayout.setError(" ");
                        }
                        else if (networkResponse.statusCode == 401){
                            Log.d("pruebas",networkResponse.statusCode + "");
                            TextInputLayout inputPassLayout = activity.findViewById(R.id.texto_password_layout);
                            inputPassLayout.setError("Email y/o contraseña incorrecta");
                            TextInputLayout inputEmailLayout = activity.findViewById(R.id.texto_email_layout);
                            inputEmailLayout.setError(" ");
                        }
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonobj);
    }



    // ---------------------------------------------------------------------------
    // -> cerrarConexion() ->
    // ---------------------------------------------------------------------------
    public void cerrarConexion() {

    }
}



