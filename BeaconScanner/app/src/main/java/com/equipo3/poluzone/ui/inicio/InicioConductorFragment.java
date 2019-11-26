package com.equipo3.poluzone.ui.inicio;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.equipo3.poluzone.Medida;
import com.equipo3.poluzone.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;

public class InicioConductorFragment extends Fragment {

    private InicioConductorViewModel mViewModel;
    private SpeedDialView speedDialView;

    public static InicioConductorFragment newInstance() {
        return new InicioConductorFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        // Anyadir las medidas a la gráfica
        Medida[] dataObjects = new Medida[10];
        Medida medida = new Medida();
        medida.setMedida(9);
        medida.setTiempo(9891);

        Medida medida1 = new Medida();
        medida1.setMedida(10);
        medida1.setTiempo(9892);

        Medida medida2 = new Medida();
        medida2.setMedida(11);
        medida2.setTiempo(9893);

        Medida medida3 = new Medida();
        medida3.setMedida(10);
        medida3.setTiempo(9894);

        dataObjects[0] = medida;
        dataObjects[1] = medida1;
        dataObjects[2] = medida2;
        dataObjects[3] = medida3;

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            entries.add(new Entry(dataObjects[i].getTiempo(), dataObjects[i].getMedida()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Medidas"); // add entries to dataset

        // Cambiar el estilo
        dataSet.setColor(R.color.colorAccent); // color
        chart.setNoDataText(getString(R.string.nomedidas)); // texto que se muestra cuando no hay datos

        // Curvado
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(2);

        // Tamanyo texto
        dataSet.setValueTextSize(15);

        // Quitar lineas on click a dato
        dataSet.setDrawHighlightIndicators(false);
        // Apagar zoom
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);

        // Quitar el grid
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);

        // Quitar las líneas
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);

        // Quitar textos
        chart.setDescription(null);
        chart.setDrawMarkers(false);
        chart.setDrawBorders(false);
        chart.getLegend().setEnabled(false);

        chart.setDragEnabled(true);
        chart.setHighlightPerDragEnabled(true);

        // Quitar los circulitos
        dataSet.setDrawCircles(false);

        // Hacer el relleno gradient
        dataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient_naranja);
            dataSet.setFillDrawable(drawable);
        }
        else {
            dataSet.setFillColor(getContext().getColor(R.color.colorAccent));
        }

        // Color de la línea
        dataSet.setColor(getContext().getColor(R.color.colorAccent));


        // Asignar to do a la gráfica
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        return root;
    }

}
