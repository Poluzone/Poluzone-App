package com.example.beaconscanner.ui.inicio;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beaconscanner.Medida;
import com.example.beaconscanner.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;

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


        // ------------------------ CHART --------------------------------------------------
        LineChart chart = root.findViewById(R.id.chart);

        Medida[] dataObjects = new Medida[10];
        Medida medida = new Medida();
        medida.setMedida(9.8f);
        medida.setTiempo(9891);

        Medida medida1 = new Medida();
        medida1.setMedida(9.4f);
        medida1.setTiempo(9892);

        Medida medida2 = new Medida();
        medida2.setMedida(9.1f);
        medida2.setTiempo(9893);

        dataObjects[0] = medida;
        dataObjects[1] = medida1;
        dataObjects[2] = medida2;


        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            entries.add(new Entry(dataObjects[i].getTiempo(), dataObjects[i].getMedida()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Medidas"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        return root;
    }

}
