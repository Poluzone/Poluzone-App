package com.example.beaconscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// -----------------------------------------------------------------------
// RegistrarUsuarioActivity.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// CopyRight:
// -----------------------------------------------------------------------
public class RegistrarUsuarioActivity extends Activity implements CallbackRegistro, GoogleApiClient.ConnectionCallbacks {

    ServidorFake servidorFake;
    GoogleApiClient googleApiClient;
    //Site key string:
    String SiteKey = "6Lds58IUAAAAAM9RcjpumLh7XGwWe-AMOiNyFS3P";
    // Para recordar que se ha registrado
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private CircularProgressView progressView;
    CheckBox checkBox;
    Button buttonRegistrarse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Indicamos dónde se guardarán las preferencias
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        setContentView(R.layout.registro);


        servidorFake = new ServidorFake(this);

        buttonRegistrarse = findViewById(R.id.button_register);

        progressView = (CircularProgressView) findViewById(R.id.progress_view);

        checkBox = (CheckBox) findViewById(R.id.check_box_ReCaptcha);

        //
        startReCaptcha();

        TextView loginaqui = findViewById(R.id.loginaqui);

        // Para el link de ir al login
        loginaqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
            }
        });


        // Para el botón registrarse
        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
    }

    // ---------------------------------------------------------------------------
    // -> registrar() ->
    // ---------------------------------------------------------------------------
    public void registrar() {

        TextInputEditText inputEmail = findViewById(R.id.texto_email_registrar);

        String email = inputEmail.getText().toString();

        TextInputEditText inputPass = findViewById(R.id.texto_contrasenya_registrar);
        String pass = inputPass.getText().toString();

        TextInputEditText inputPass2 = findViewById(R.id.texto_contrasenyaotravez_registrar);
        String pass2 = inputPass2.getText().toString();

        TextInputEditText inputPhone = findViewById(R.id.texto_telefono_registrar);
        String phone = inputPhone.getText().toString();

        if (validarSiEstanVacios(email, pass, pass2, phone) && validarEmail(email) && validarContrasenya(pass, pass2) && validarReCaptcha()==true) {
            // Guardamos los datos en las preferencias
            mostrarProgress(true);
            servidorFake.insertarUsuario(email, pass, Integer.parseInt(phone));
        }
    }

    // ---------------------------------------------------------------------------
    // email: texto -> validarEmail() -> boolean
    // Comprueba si el email tiene el formato correcto
    // ---------------------------------------------------------------------------
    private boolean validarEmail(String email) {
        TextInputLayout inputEmailLayout = findViewById(R.id.texto_email_registrar_layout);
        if (!email.contains("@") || !email.contains(".") || email.contains(" ")) {
            inputEmailLayout.setError(getString(R.string.errorEmail));
            mostrarProgress(false);
            return false;
        } else return true;
    }

    // ---------------------------------------------------------------------------
    // pass1, pass2: texto -> validarContrasenya() -> boolean
    // Comprueba que la contraseña tiene el formato correcto
    // ---------------------------------------------------------------------------
    private boolean validarContrasenya(String pass1, String pass2) {
        TextInputLayout inputPassLayout = findViewById(R.id.texto_contrasenya_registrar_layout);
        TextInputLayout inputPassAgainLayout = findViewById(R.id.texto_contrasenyaotravez_registrar_layout);

        Log.d("pruebas", pass1);
        // Comprobar que tenga al menos una mayúscula, un número y una minúscula
        Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])");
        Matcher m = p.matcher(pass1);
        boolean regextrue = m.find();
        if (regextrue) {
            // Comprobar que las dos contraseñas coincidan
            if (!pass1.equals(pass2)) {
                inputPassAgainLayout.setError(getString(R.string.errorContrasenyaNoCoincide));
                mostrarProgress(false);
                return false;
            } else return true;
        } else {
            // Mostrar error si no coincide
            inputPassLayout.setError(getString(R.string.falloFormatoPass));
            mostrarProgress(false);
            return false;
        }

    }

    // ---------------------------------------------------------------------------
    // email, pass1, pass2, telefono: texto-> validarSiEstanVacios() -> boolean
    // Comprueba que haya completado todos los campos
    // ---------------------------------------------------------------------------
    private boolean validarSiEstanVacios(String email, String pass1, String pass2, String telefono) {
        TextInputLayout inputEmailLayout = findViewById(R.id.texto_email_registrar_layout);
        TextInputLayout texto_contrasenya_registrar_layout = findViewById(R.id.texto_contrasenya_registrar_layout);
        TextInputLayout texto_contrasenyaotravez_registrar_layout = findViewById(R.id.texto_contrasenyaotravez_registrar_layout);
        TextInputLayout texto_telefono_registrar_layout = findViewById(R.id.texto_telefono_registrar_layout);

        inputEmailLayout.setError(null);
        texto_contrasenya_registrar_layout.setError(null);
        texto_contrasenyaotravez_registrar_layout.setError(null);
        texto_telefono_registrar_layout.setError(null);

        if (email.equals("")) {
            inputEmailLayout.setError(getString(R.string.completar));
            mostrarProgress(false);
            return false;
        } else if (pass1.equals("")) {
            texto_contrasenya_registrar_layout.setError(getString(R.string.completar));
            mostrarProgress(false);
            return false;
        } else if (pass2.equals("")) {
            texto_contrasenyaotravez_registrar_layout.setError(getString(R.string.completar));
            mostrarProgress(false);
            return false;
        } else if (telefono.equals("")) {
            texto_telefono_registrar_layout.setError(getString(R.string.completar));
            mostrarProgress(false);
            return false;
        } else {
            return true;
        }
    }


    // ---------------------------------------------------------------------------
    // V/F, respuesta -> callbackRegistro() -> boolean
    // ---------------------------------------------------------------------------
    @Override
    public void callbackRegistro(boolean resultadoRegistro, JSONObject response) {
        // Si se ha hecho correctamente
        if (resultadoRegistro) {
            TextInputEditText inputEmail = findViewById(R.id.texto_email_registrar);
            String email = inputEmail.getText().toString();

            TextInputEditText inputPass = findViewById(R.id.texto_contrasenya_registrar);
            String pass = inputPass.getText().toString();

            TextInputEditText inputPhone = findViewById(R.id.texto_telefono_registrar);
            String phone = inputPhone.getText().toString();

            // Guardamos las preferencias (cookie)
            loginPrefsEditor.putString("email", email);
            loginPrefsEditor.putString("pass", pass);
            loginPrefsEditor.putString("telefono", phone);
            loginPrefsEditor.commit();

            Intent i = new Intent(this, NavigationDrawerActivity.class);
            Log.d("pruebas", "intent main");
            this.startActivity(i);
            this.finish();
        } else {
            TextInputLayout inputEmailLayout = findViewById(R.id.texto_email_registrar_layout);
            mostrarProgress(false);
            // Convenio de devolver response = null cuando el error es de conexión
            if (response == null)
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
            else inputEmailLayout.setError(getString(R.string.yaExiste));
        }
    }

    // ---------------------------------------------------------------------------
    // V/F -> mostrarProgress() ->
    // Ruedecita de carga
    // ---------------------------------------------------------------------------
    private void mostrarProgress(Boolean mostrar) {
        if (mostrar) {
            progressView.resetAnimation();
            progressView.setVisibility(View.VISIBLE);
            buttonRegistrarse.setTextColor(getResources().getColor(R.color.colorAccent));
            progressView.startAnimation();
        } else {
            progressView.resetAnimation();
            progressView.setVisibility(View.INVISIBLE);
            progressView.stopAnimation();
            buttonRegistrarse.setTextColor(Color.WHITE);
        }
    }

    // ---------------------------------------------------------------------------
    // startReCaptcha()
    // Al pulsar en el checkbox que valide ReCaptcha
    // ---------------------------------------------------------------------------
    private void startReCaptcha() {

        //Connectar con google
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(RegistrarUsuarioActivity.this)
                .build();
        googleApiClient.connect();

        //Cuando clickas el checkbox empieza el ReCaptcha
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {

                    SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient, SiteKey)
                            .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                                @Override
                                public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult recaptchaTokenResult) {
                                    Status statusReCaptcha = recaptchaTokenResult.getStatus();

                                    if (statusReCaptcha.isSuccess() && statusReCaptcha != null) {

                                        checkBox.setChecked(true);
                                        checkBox.setClickable(false);

                                    } else {
                                        checkBox.setClickable(true);
                                        checkBox.setChecked(false);
                                    }
                                }
                            });
                } else {
                    checkBox.setClickable(true);
                    checkBox.setChecked(false);

                }
            }
        });

    }

    // ---------------------------------------------------------------------------
    // validarReCaptcha()
    // validar si se ha verificado que no es un robot
    // ---------------------------------------------------------------------------
    private boolean validarReCaptcha(){
        if(!checkBox.isChecked()){

            Toast toast1 =
                Toast.makeText(getApplicationContext(),
                        "Verifica que no eres un robot", Toast.LENGTH_SHORT);
            toast1.show();
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
