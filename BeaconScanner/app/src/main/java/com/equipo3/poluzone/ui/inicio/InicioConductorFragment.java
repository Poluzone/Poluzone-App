package com.equipo3.poluzone.ui.inicio;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.equipo3.poluzone.Callback;
import com.equipo3.poluzone.CallbackMisMedidas;
import com.equipo3.poluzone.InfoDialog;

import com.equipo3.poluzone.MainMisMedidas;
import com.equipo3.poluzone.Medida;
import com.equipo3.poluzone.NavigationDrawerActivity;
import com.equipo3.poluzone.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.leinardi.android.speeddial.SpeedDialView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InicioConductorFragment extends Fragment implements Callback, CallbackMisMedidas {

    private InicioConductorViewModel mViewModel;
    private SpeedDialView speedDialView;
    private View root;
    private Double umbralMal = 163.0;
    private LineChart chart;


    public static InicioConductorFragment newInstance() {
        return new InicioConductorFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(InicioConductorViewModel.class);
        root = inflater.inflate(R.layout.fragment_inicio_conductor, container, false);

        NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getParentFragment().getActivity();

        // acceder speed dial para esconderlo
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.hide();


        // ------------------------------------ CHART --------------------------------------------------
        // Cogemos la hora actual
        ZonedDateTime now = ZonedDateTime.now();

        // Cogemos la info de hoy a las 7 am
        ZonedDateTime before = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 7, 0, 0, 0, ZoneId.systemDefault());

        // Lo pasamos a milisegundos
        Long beforemili = before.toInstant().toEpochMilli();

        chart = root.findViewById(R.id.chart);
        navigationDrawerActivity.servidorFake.getMedidasPorUsuario(beforemili, System.currentTimeMillis(), navigationDrawerActivity.idUser);


        // ----------------------------------- INFO DIALOG ---------------------------------------------

        ImageView iconoInfo = root.findViewById(R.id.iconoinfo);
        iconoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InfoDialog infoDialog = new InfoDialog();
                if (!infoDialog.isAdded()) infoDialog.show(getFragmentManager(),"");
            }
        });

        // ----------------------------------- MIS MEDIDAS ---------------------------------------------

        Button botonMisMedidas = root.findViewById(R.id.botonVerMedidas);
        botonMisMedidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(),MainMisMedidas.class);
                startActivity(in);
            }
        });


        // ----------------------------------- MEDIA CALIDAD AIRE --------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getCalidadDelAireDeLaJornada();
        }
        navigationDrawerActivity.servidorFake.callback = this;
        navigationDrawerActivity.servidorFake.callbackMisMedidas = this;

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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void callbackMisMedidas(JSONObject response) {

        // Anyadir las medidas a la gráfica
        Medida[] dataObjects = new Medida[10];
        int j = 0;
        try {
            for (int i = response.getJSONArray("medidas").length()-1;i>response.getJSONArray("medidas").length()-11;i--) {
                JSONObject medidas = response.getJSONArray("medidas").getJSONObject(i);
                Medida medida = new Medida();
                medida.setMedida(Float.parseFloat(medidas.getString("Valor")));
                medida.setTiempo(Long.parseLong(medidas.getString("Tiempo")));
                dataObjects[j] = medida;
                j++;
            }

            DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < 10; i++) {

                // Creating date from milliseconds
                // using Date() constructor
               // Date result = new Date(dataObjects[i].getTiempo());
                entries.add(new Entry(i, dataObjects[i].getMedida()));
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
            dataSet.setCircleHoleColor(R.color.colorAccent);
            dataSet.setCircleColor(R.color.colorAccent);
            //dataSet.setDrawCircles(false);

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

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Pruebas?",e.toString());
        }



    }
}
