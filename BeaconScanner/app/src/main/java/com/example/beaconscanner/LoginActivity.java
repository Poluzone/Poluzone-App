package com.example.beaconscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
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
        setTheme(R.style.LoginTheme);
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
                        @Override public void run() {
                            startActivity(i);
                        }
                    });
                }

                else {
                    setContentView(R.layout.login);

                    servidorFake = new ServidorFake(this);
                    TextView registrateaqui = findViewById(R.id.registrateaqui);
                    inputPassLayout = findViewById(R.id.texto_password_layout);
                    inputEmailLayout = findViewById(R.id.texto_email_layout);
                    botonLogearse = findViewById(R.id.botonLogin);

                    progressView  = (CircularProgressView) findViewById(R.id.progress_view);

                    final TextInputLayout inputPassLayout = findViewById(R.id.texto_password_layout);
                    final TextInputEditText inputEmail = findViewById(R.id.texto_email);
                    final TextInputEditText inputPass = findViewById(R.id.texto_pass);

                    // Primero comprobamos si ya hizo login anteriormente
                    // Recogemos las preferencias de la app
                    loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    loginPrefsEditor = loginPreferences.edit();

                    String email = loginPreferences.getString("email", "");
                    String pass = loginPreferences.getString("pass", "");

                    Log.d("pruebas", " preferencias: " + email);
                    Log.d("pruebas", " preferencias: " + pass);

                    // Si se ha rellenado hacemos el login
                    if (validarSiEstanVacios(email, pass)) {
                        servidorFake.comprobarUsuarioPorEmail(email, pass);
                    }

                    registrateaqui.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getApplicationContext(), RegistrarUsuarioActivity.class);
                            startActivity(i);
                        }
                    });


                    botonLogearse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logearse();
                        }
                    });


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
                }
    }


    // ---------------------------------------------------------------------------
    // -> logearse() ->
    // ---------------------------------------------------------------------------
    public void logearse() {
        inputPassLayout.setError(null);
        inputEmailLayout.setError(null);
        TextInputEditText inputEmail = findViewById(R.id.texto_email);
        TextInputEditText inputPass = findViewById(R.id.texto_pass);
        String email = inputEmail.getText().toString();
        String pass = inputPass.getText().toString();
        if (validarSiEstanVacios(email, pass)) {
            mostrarProgress(true);
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
    // email, pass -> validarSiEstanVacios() -> boolean
    // ---------------------------------------------------------------------------
    private boolean validarSiEstanVacios(String email, String pass) {
        if (email.equals("")) {
            inputEmailLayout.setError(getString(R.string.completar));
            return false;
        }
        else if (pass.equals("")) {
            inputPassLayout.setError(getString(R.string.completar));
            return false;
        }
        else {
            return true;
        }
    }


    // ---------------------------------------------------------------------------
    // resultadoLogin: V/F, respuesta -> callbackLogin() ->
    // ---------------------------------------------------------------------------
    @Override
    public void callbackLogin(boolean resultadoLogin, JSONObject response){
        if (resultadoLogin) {
            // Guardamos los datos de la consulta
            try {
                loginPrefsEditor.putString("email", response.getJSONArray("Usuario").getJSONObject(0).get("Email").toString());
                loginPrefsEditor.putString("pass", response.getJSONArray("Usuario").getJSONObject(0).get("Password").toString());
                loginPrefsEditor.putString("telefono", response.getJSONArray("Usuario").getJSONObject(0).get("Telefono").toString());
            }
            catch (JSONException e) {
                Log.d("pruebas", "error json: " + e);
            }
            loginPrefsEditor.commit();

            // Empezamos la nueva actividad
            Intent i = new Intent(this, NavigationDrawerActivity.class);
            Log.d("pruebas", "intent main");
            this.startActivity(i);
            this.finish();
        }
        else {
            mostrarProgress(false);
            // Mostramos los mensajes de error en pantalla
            if (response == null) Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
            else errorLogin();
        }
    }

    // ---------------------------------------------------------------------------
    // V/F -> mostrarProgress() ->
    // ---------------------------------------------------------------------------
    private void mostrarProgress(Boolean mostrar) {
        if (mostrar) {
            progressView.resetAnimation();
            progressView.setVisibility(View.VISIBLE);
            botonLogearse.setText("");
            progressView.startAnimation();
        }
        else {
            progressView.resetAnimation();
            progressView.setVisibility(View.INVISIBLE);
            progressView.stopAnimation();
            botonLogearse.setText(R.string.login);
        }
    }

}
