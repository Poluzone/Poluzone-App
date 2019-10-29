package com.example.beaconscanner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrarUsuarioActivity extends Activity {

    ServidorFake servidorFake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        // TODO: COMPROBAR QUE NO EXISTA EL EMAIL YA

        if (validarSiEstanVacios(email, pass, pass2, phone) && validarEmail(email) && validarContrasenya(pass, pass2)) {
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


        if (!pass1.equals(pass2)) {
            inputPassAgainLayout.setError(getString(R.string.errorContrasenyaNoCoincide));
            return false;
        }
        else return true;
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
}
