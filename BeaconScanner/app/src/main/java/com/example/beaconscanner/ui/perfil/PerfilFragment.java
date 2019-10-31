package com.example.beaconscanner.ui.perfil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.beaconscanner.R;
import com.example.beaconscanner.ui.foto.FotoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import static android.content.Context.MODE_PRIVATE;

public class PerfilFragment extends Fragment {
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Polufoto
    //---------------------------------------------------------------------------
    private PerfilViewModel perfilViewModel;
    // Para recordar que se ha logeado
    private SharedPreferences loginPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        perfilViewModel = ViewModelProviders.of(this).get(PerfilViewModel.class);
        View root = inflater.inflate(R.layout.fragment_perfil, container, false);

        loginPreferences = getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);

        String email = loginPreferences.getString("email","");
        String tlf = loginPreferences.getString("telefono","");

        TextInputEditText emailinput = root.findViewById(R.id.textoemail);
        TextInputEditText tlfinput = root.findViewById(R.id.textotelefono);
        FloatingActionButton fab = getParentFragment().getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.edit_account);

        emailinput.setEnabled(false);
        tlfinput.setEnabled(false);

        emailinput.setText(email);
        tlfinput.setText(tlf);


        /*final TextView textView = root.findViewById(R.id.text_foto);
        fotoViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/ // Esto era para cambiar un texto que tenia por defecto la navigation Activity que te decia en que fragmento estabas
        return root;
    }
}

