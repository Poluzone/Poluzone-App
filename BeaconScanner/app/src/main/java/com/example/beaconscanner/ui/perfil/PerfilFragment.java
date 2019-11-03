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
import com.leinardi.android.speeddial.SpeedDialView;

import static android.content.Context.MODE_PRIVATE;

public class PerfilFragment extends Fragment {
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Polufoto
    //---------------------------------------------------------------------------
    private PerfilViewModel perfilViewModel;
    // Para recordar que se ha logeado
    private SharedPreferences loginPreferences;
    SpeedDialView speedDialView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        perfilViewModel = ViewModelProviders.of(this).get(PerfilViewModel.class);
        View root = inflater.inflate(R.layout.fragment_perfil, container, false);

        loginPreferences = getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);

        String email = loginPreferences.getString("email","");
        String tlf = loginPreferences.getString("telefono","");

        TextInputEditText emailinput = root.findViewById(R.id.textoemail);
        TextInputEditText tlfinput = root.findViewById(R.id.textotelefono);

        // acceder speed dial
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.hide();

        emailinput.setEnabled(false);
        tlfinput.setEnabled(false);

        emailinput.setText(email);
        tlfinput.setText(tlf);

        return root;
    }
}

