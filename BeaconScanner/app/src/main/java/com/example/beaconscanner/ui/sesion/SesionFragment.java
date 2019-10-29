package com.example.beaconscanner.ui.sesion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.beaconscanner.R;

public class SesionFragment extends Fragment {

    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Cerrar sesion
    //---------------------------------------------------------------------------

    private SesionViewModel sesionViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sesionViewModel =
                ViewModelProviders.of(this).get(SesionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sesion, container, false);//CAMBIAR A LOGIN CUANDO ESTÉ
        /*final TextView textView = root.findViewById(R.id.text_sesion);
        sesionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}