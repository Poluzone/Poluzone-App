package com.example.beaconscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarUsuarioActivity extends Activity implements CallbackRegistro {

    ServidorFake servidorFake;
    // Para recordar que se ha registrado
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Indicamos dónde se guardarán las preferencias
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        setContentView(R.layout.registro);

        servidorFake = new ServidorFake(this);

        Button buttonRegistrarse = findViewById(R.id.button_register);

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

        if (validarSiEstanVacios(email, pass, pass2, phone) && validarEmail(email) && validarContrasenya(pass, pass2)) {
            // Guardamos los datos en las preferencias
            servidorFake.insertarUsuario(email, pass, Integer.parseInt(phone));
        }
    }

    // ---------------------------------------------------------------------------
    // email: texto -> validarEmail() -> boolean
    // ---------------------------------------------------------------------------
    private boolean validarEmail(String email) {
        TextInputLayout inputEmailLayout = findViewById(R.id.texto_email_registrar_layout);
        if (!email.contains("@") || !email.contains(".") || email.contains(" ")) {
            inputEmailLayout.setError(getString(R.string.errorEmail));
            return false;
        }
        else return true;
    }

    // ---------------------------------------------------------------------------
    // pass1, pass2: texto -> validarContrasenya() -> boolean
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
                return false;
            }
            else return true;
        }
        else {
            // Mostrar error si no coincide
            inputPassLayout.setError(getString(R.string.falloFormatoPass));
            return false;
        }

    }

    // ---------------------------------------------------------------------------
    // email, pass1, pass2, telefono: texto-> validarSiEstanVacios() -> boolean
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
            return false;
        }
        else if (pass1.equals("")) {
            texto_contrasenya_registrar_layout.setError(getString(R.string.completar));
            return false;
        }
        else if (pass2.equals("")) {
            texto_contrasenyaotravez_registrar_layout.setError(getString(R.string.completar));
            return false;
        }
        else if (telefono.equals("")) {
            texto_telefono_registrar_layout.setError(getString(R.string.completar));
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void callbackRegistro(boolean resultadoRegistro) {
        if (resultadoRegistro) {
            TextInputEditText inputEmail = findViewById(R.id.texto_email_registrar);
            String email = inputEmail.getText().toString();

            TextInputEditText inputPass = findViewById(R.id.texto_contrasenya_registrar);
            String pass = inputPass.getText().toString();

            TextInputEditText inputPhone = findViewById(R.id.texto_telefono_registrar);
            String phone = inputPhone.getText().toString();

            loginPrefsEditor.putString("email", email);
            loginPrefsEditor.putString("pass", pass);
            loginPrefsEditor.putString("telefono", phone);
            loginPrefsEditor.commit();

            Intent i = new Intent(this, NavigationDrawerActivity.class);
            Log.d("pruebas", "intent main");
            this.startActivity(i);
            this.finish();
        }
        else {
            TextInputLayout inputEmailLayout = findViewById(R.id.texto_email_registrar_layout);
            inputEmailLayout.setError(getString(R.string.yaExiste));
        }
    }
}
