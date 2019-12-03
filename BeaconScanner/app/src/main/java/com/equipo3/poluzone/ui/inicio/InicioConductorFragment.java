package com.equipo3.poluzone.ui.inicio;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.equipo3.poluzone.Callback;
import com.equipo3.poluzone.InfoDialog;
import com.equipo3.poluzone.Medida;
import com.equipo3.poluzone.NavigationDrawerActivity;
import com.equipo3.poluzone.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.leinardi.android.speeddial.SpeedDialView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InicioConductorFragment extends Fragment implements Callback {

    private InicioConductorViewModel mViewModel;
    private SpeedDialView speedDialView;
    private View root;
    private Double umbralMal = 163.0;
    private Double umbralBien = 0.0;


    public static InicioConductorFragment newInstance() {
        return new InicioConductorFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(InicioConductorViewModel.class);
        root = inflater.inflate(R.layout.fragment_inicio_conductor, container, false);

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


        // ----------------------------------- INFO DIALOG ---------------------------------------------

        ImageView iconoInfo = root.findViewById(R.id.iconoinfo);
        iconoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InfoDialog infoDialog = new InfoDialog();
                if (!infoDialog.isAdded()) infoDialog.show(getFragmentManager(),"");
            }
        });


        // ----------------------------------- MEDIA CALIDAD AIRE --------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getCalidadDelAireDeLaJornada();
        }
        NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getParentFragment().getActivity();
        navigationDrawerActivity.servidorFake.callback = this;

        return root;
    }


    // ---------------------------------------------------------------------------
    // calidad: R -> callbackMediaCalidadAire() ->
    // Muestra la media en el layout
    // ---------------------------------------------------------------------------
    @Override
    public void callbackMediaCalidadAire(double media) {
        Log.d("pruebas", "calidad " + media);
        TextView porcentajeText = root.findViewById(R.id.textViewPorcentaje);
        ImageView nubeFoto = root.findViewById(R.id.nubecita);

        // Regla de tres inversa para calcular el porcentaje según la media
        // teniendo en cuenta que el ppm de umbralMal equivale a un 29%
        Double porcentaje = umbralMal * 29 / media;

        // Si sale mayor que 100 lo ponemos a 100%
        if (porcentaje > 100) porcentaje = 100.0;

        // Cambiamos formato para no mostrar todas las decimales
        DecimalFormat df = new DecimalFormat("##.#");
        porcentajeText.setText(df.format(porcentaje) + "%");

        // Cambiamos la imagen de la nube según el porcentaje
        if (porcentaje >= 70) nubeFoto.setImageDrawable(getActivity().getDrawable(R.drawable.happ));
        else if (porcentaje >= 30 && porcentaje < 69) nubeFoto.setImageDrawable(getActivity().getDrawable(R.drawable.meh));
        else if (porcentaje < 30) nubeFoto.setImageDrawable(getActivity().getDrawable(R.drawable.mal));

    }


    // ---------------------------------------------------------------------------
    // -> getCalidadDelAireDeLaJornada() -> calidad: R (callback)
    // Recoge la media de la calidad del aire de la jornada de trabajo
    // ---------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCalidadDelAireDeLaJornada() {
        NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getParentFragment().getActivity();

        // Cogemos la hora actual
        ZonedDateTime now = ZonedDateTime.now();

        // Cogemos la info de hoy a las 7 am
        ZonedDateTime before = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 7, 0, 0, 0, ZoneId.systemDefault());

        // Lo pasamos a milisegundos
        Long beforemili = before.toInstant().toEpochMilli();

        // Llamamos al metodo adecuado de servidorFake
        navigationDrawerActivity.servidorFake.getMediaCalidadDelAireDeLaJornada(beforemili, System.currentTimeMillis(), navigationDrawerActivity.idUser);
    }


    @Override
    public void callbackLogin(boolean resultadoLogin, JSONObject response) {

    }

    @Override
    public void callbackMedidas(boolean resultado, JSONObject medidas) {

    }

    @Override
    public void callbackEstaciones(boolean resultado, JSONObject estaciones) {

    }


}
