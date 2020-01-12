// -----------------------------------------------------------------------
// AjustesFragment.java
// Equipo 3
// Autor: Iván Romero Ruíz
// Fecha: 10/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone.ui.ajustes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.equipo3.poluzone.R;
import com.leinardi.android.speeddial.SpeedDialView;

public class AjustesFragment extends Fragment {
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Ajustes
    //---------------------------------------------------------------------------
    private AjustesViewModel ajustesViewModel;
    private SpeedDialView speedDialView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ajustesViewModel =
                ViewModelProviders.of(this).get(AjustesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ajustes, container, false);

        // acceder speed dial para esconderlo
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.hide();

        /*final TextView textView = root.findViewById(R.id.text_ajustes);
        ajustesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/ // Esto era para cambiar un texto que tenia por defecto la navigation Activity que te decia en que fragmento estabas
        return root;
    }
}