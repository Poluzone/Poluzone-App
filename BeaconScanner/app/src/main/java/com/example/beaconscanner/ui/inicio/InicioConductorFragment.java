package com.example.beaconscanner.ui.inicio;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beaconscanner.R;
import com.leinardi.android.speeddial.SpeedDialView;

public class InicioConductorFragment extends Fragment {

    private InicioConductorViewModel mViewModel;
    private SpeedDialView speedDialView;

    public static InicioConductorFragment newInstance() {
        return new InicioConductorFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(InicioConductorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_inicio_conductor, container, false);

        // acceder speed dial para esconderlo
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.hide();

        return root;
    }

}
