package com.equipo3.poluzone.ui.info;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equipo3.poluzone.R;
import com.leinardi.android.speeddial.SpeedDialView;

public class InfoFragment extends Fragment {

    //---------------------------------------------------------------------------
    //Clase relacionada con el navigation info
    //---------------------------------------------------------------------------

    private InfoViewModel mViewModel;
    private SpeedDialView speedDialView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(InfoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_info, container, false);

        // acceder speed dial
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.hide();

        return root;
    }

}
