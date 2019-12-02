package com.equipo3.poluzone.ui.mapa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.equipo3.poluzone.R;
import com.leinardi.android.speeddial.SpeedDialView;

public class MapaFragment extends Fragment {

    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Mapa
    //---------------------------------------------------------------------------

    private MapaViewModel mapaViewModel;
    private SpeedDialView speedDialView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapaViewModel =
                ViewModelProviders.of(this).get(MapaViewModel.class);
        View root = inflater.inflate(R.layout.activity_maps, container, false);
        // acceder speed dial
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.show();

        return root;
    }
}