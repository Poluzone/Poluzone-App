package com.example.beaconscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

// -----------------------------------------------------------------------
// LoginActivity.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// CopyRight:
// -----------------------------------------------------------------------
public class LoginActivity extends AppCompatActivity implements CallbackLogin {

    // Servidor
    ServidorFake servidorFake;

    Button botonLogearse;

    TextInputLayout inputEmailLayout;
    TextInputLayout inputPassLayout;

    // Para recordar que se ha logeado
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private CircularProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        //  If the activity has never started before...
        if (isFirstStart) {

            //  Launch app intro
            final Intent i = new Intent(LoginActivity.this, IntroActivity.class);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                }
            });
        }

        // Si no es la primera vez
        else {
            servidorFake = new ServidorFake(this);

            // Primero comprobamos si ya hizo login anteriormente
            // Recogemos las preferencias de la app
            loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            loginPrefsEditor = loginPreferences.edit();

            String email = loginPreferences.getString("email", "");
            String pass = loginPreferences.getString("passSinEncriptar", "");

            Log.d("pruebas", " preferencias: " + email);
            Log.d("pruebas", " preferencias: " + pass);


            // Si hay loginprefs (se ha logeado antes)
            if (validarSiEstanVacios(email, pass, 1)) {
                servidorFake.comprobarUsuarioPorEmail(email, pass);
            }

            // Si no se ha logeado antes
            else {
                // Cambiamos el tema para quitar el launch screen background
                setTheme(R.style.LoginTheme);
                setContentView(R.layout.login);

                inputPassLayout = findViewById(R.id.texto_password_layout);
                inputEmailLayout = findViewById(R.id.texto_email_layout);

                TextView registrateaqui = findViewById(R.id.registrateaqui);
                botonLogearse = findViewById(R.id.botonLogin);

                progressView = (CircularProgressView) findViewById(R.id.progress_view);

                final TextInputLayout inputPassLayout = findViewById(R.id.texto_password_layout);
                final TextInputEditText inputEmail = findViewById(R.id.texto_email);
                final TextInputEditText inputPass = findViewById(R.id.texto_pass);

                // El link de registrarse
                registrateaqui.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), RegistrarUsuarioActivity.class);
                        startActivity(i);
                    }
                });


                // El botón de Login
                botonLogearse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logearse();
                    }
                });


                // ---------------------------------------------------------------------------------------------
                // Para vaciar los edittexts cuando los seleccionas después de que ocurra un error etc
                // ---------------------------------------------------------------------------------------------
                inputEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        inputEmailLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

                inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            inputEmail.setText(null);
                        }
                    }
                });

                inputPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        inputPassLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

                inputPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            inputPass.setText(null);
                        }
                    }
                });
                // ---------------------------------------------------------------------------------------------

            } // else si no se ha logeado antes

        }// else si no es primera vez
    }


    // ---------------------------------------------------------------------------
    // -> logearse() ->
    // ---------------------------------------------------------------------------
    public void logearse() {
        // Borra los errores por si había de antes
        inputPassLayout.setError(null);
        inputEmailLayout.setError(null);
        TextInputEditText inputEmail = findViewById(R.id.texto_email);
        TextInputEditText inputPass = findViewById(R.id.texto_pass);
        // Recoge los datos escritos
        String email = inputEmail.getText().toString();
        String pass = inputPass.getText().toString();
        // Valida si ha completado todos los campos
        if (validarSiEstanVacios(email, pass, 0)) {
            // Ruedecita de carga
            mostrarProgress(true);
            // Llamamos al método correspondiente del servidor
            servidorFake.comprobarUsuarioPorEmail(email, pass);
        }
    }

    // ---------------------------------------------------------------------------
    // -> errorLogin() ->
    // ---------------------------------------------------------------------------
    private void errorLogin() {
        inputEmailLayout.setError(" ");
        inputPassLayout.setError("Email y/o contraseña incorrecta");
    }

    // ---------------------------------------------------------------------------
    // email, pass, N -> validarSiEstanVacios() -> boolean
    // ---------------------------------------------------------------------------
    private boolean validarSiEstanVacios(String email, String pass, int primeravez) {
        if (email.equals("")) {
            if (primeravez == 0) inputEmailLayout.setError(getString(R.string.completar));
            return false;
        } else if (pass.equals("")) {
            if (primeravez == 0) inputPassLayout.setError(getString(R.string.completar));
            return false;
        } else {
            return true;
        }
    }


    // ---------------------------------------------------------------------------
    // resultadoLogin: V/F, respuesta -> callbackLogin() ->
    // ---------------------------------------------------------------------------
    @Override
    public void callbackLogin(boolean resultadoLogin, JSONObject response) {
        if (resultadoLogin) {
            // Guardamos los datos de la consulta
            try {
                TextInputEditText inputPass = findViewById(R.id.texto_pass);
                String pass;
                if (inputPass != null) pass = inputPass.getText().toString();
                else pass = loginPreferences.getString("passSinEncriptar", "");


                Log.d("pruebas", "Ha iniciado sesión");

                loginPrefsEditor.putString("email", response.getJSONArray("Usuario").getJSONObject(0).get("Email").toString());
                loginPrefsEditor.putString("passEncriptado", response.getJSONArray("Usuario").getJSONObject(0).get("Password").toString());
                loginPrefsEditor.putInt("idUsuario", response.getJSONArray("Usuario").getJSONObject(0).getInt("IdUsuario"));
                loginPrefsEditor.putString("tipousuario", response.getJSONArray("Usuario").getJSONObject(0).get("TipoUsuario").toString());
                
                // Pass sin encriptar es temporal hasta que implementemos la encriptación de la contrasenya desde el móvil
                loginPrefsEditor.putString("passSinEncriptar", pass);
                loginPrefsEditor.putString("telefono", response.getJSONArray("Usuario").getJSONObject(0).get("Telefono").toString());
            } catch (JSONException e) {
                Log.d("pruebas", "error json: " + e);
            }

            // Guardamos los cambios en las preferencias (cookie)
            loginPrefsEditor.commit();

            // Empezamos la nueva actividad
            Intent i = new Intent(this, NavigationDrawerActivity.class);
            Log.d("pruebas", "intent main");
            this.startActivity(i);
            this.finish();
        } else {
            mostrarProgress(false);
            // Mostramos los mensajes de error en pantalla
            if (response == null) {
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
            }
            else errorLogin();
        }
    }

    // ---------------------------------------------------------------------------
    // V/F -> mostrarProgress() ->
    // Es la ruedecita de carga que se muestra si mostrar = true
    // ---------------------------------------------------------------------------
    private void mostrarProgress(Boolean mostrar) {
        if (mostrar) {
            progressView.resetAnimation();
            progressView.setVisibility(View.VISIBLE);
            botonLogearse.setTextColor(getResources().getColor(R.color.colorAccent));
            progressView.startAnimation();
        } else {
            progressView.resetAnimation();
            progressView.setVisibility(View.INVISIBLE);
            progressView.stopAnimation();
            botonLogearse.setTextColor(Color.WHITE);
        }
    }

}
