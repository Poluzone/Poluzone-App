package com.example.beaconscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Servidor
    ServidorFake servidorFake;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        servidorFake = new ServidorFake(this);
        TextView registrateaqui = findViewById(R.id.registrateaqui);

        // TODO: PRIMERO COMPROBAR SI YA SE HABÍA LOGEADO ANTERIORMENTE

        registrateaqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegistrarUsuarioActivity.class);
                startActivity(i);
            }
        });
    }


    public void logearse(String email, String pass) {
        
    }

}
