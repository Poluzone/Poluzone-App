// -----------------------------------------------------------------------
// MainMisMedidas.java
// Equipo 3
// Autor: Josep Carreres
// Fecha: 11/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainMisMedidas extends AppCompatActivity implements CallbackMisMedidas {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ServidorFake servidorFake;
    ArrayList<Medida> mValores;
    ArrayList<Medida> calidadAlta;
    ArrayList<Medida> calidadMedia;
    ArrayList<Medida> calidadBaja;
    CheckBox calidad_buena;
    CheckBox calidad_media;
    CheckBox calidad_mala;
    SwipeRefreshLayout refreshLayout;
    private SharedPreferences loginPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recyclerview_listamedidas);

        mValores = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recyclerview_medidas);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AdapterRecyclerViewMisMedidas(mValores, this);
        mRecyclerView.setAdapter(mAdapter);

        calidadAlta = new ArrayList<>();
        calidadMedia = new ArrayList<>();
        calidadBaja = new ArrayList<>();

        calidad_buena = findViewById(R.id.filtrar_buenaire);
        calidad_media = findViewById(R.id.filtrar_medioaire);
        calidad_mala = findViewById(R.id.filtrar_malaire);

        calidad_buena.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(calidad_buena.isChecked()){
                calidad_mala.setClickable(false);
                calidad_media.setClickable(false);
                filtrarPorCalidadBuena(mValores);
            }else {
                calidad_mala.setClickable(true);
                calidad_media.setClickable(true);
            }


        }
        });
        calidad_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(calidad_media.isChecked()){
                    calidad_mala.setClickable(false);
                    calidad_buena.setClickable(false);
                    filtrarPorCalidadMedia(mValores);
                }else
                {
                    calidad_mala.setClickable(true);
                    calidad_buena.setClickable(true);
                }


            }
        });
        calidad_mala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(calidad_mala.isChecked()){
                    calidad_buena.setClickable(false);
                    calidad_media.setClickable(false);
                    filtrarPorCalidadBaja(mValores);
                }else
                {
                    calidad_buena.setClickable(true);
                    calidad_media.setClickable(true);
                }


            }
        });
        refreshLayout = findViewById(R.id.refresh_mismedidas);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
            }
        });

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int idUser = loginPreferences.getInt("idUsuario", 15);

        servidorFake = new ServidorFake(this);
        servidorFake.getMedidasPorUsuario(20, idUser );
    }


    @Override
    public void callbackMisMedidas(JSONObject response) {

        try {
            for (int i = 0;i<response.getJSONArray("medidas").length();i++){

                JSONObject medidas = response.getJSONArray("medidas").getJSONObject(i);
                Medida medida = new Medida();
                medida.setMedida(Float.parseFloat(medidas.getString("Valor")));
                medida.setTipoMedida(Integer.parseInt(medidas.getString("IdTipoMedida")));
                medida.setIdMedida(Integer.parseInt(medidas.getString("IdMedida")));
                medida.setTiempo(Long.parseLong(medidas.getString("Tiempo")));
                mValores.add(medida);

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Funciono?",e.toString());
        }

        mAdapter = new AdapterRecyclerViewMisMedidas(mValores ,this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

    }

    private void filtrarPorCalidadBuena(ArrayList<Medida> Valores){

        for (int i = 0;i<Valores.size();i++){

            if(Valores.get(i).getMedida() <= 67){

                calidadAlta.add(Valores.get(i));

            }
        }

        mAdapter = new AdapterRecyclerViewMisMedidas(calidadAlta ,this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    private void filtrarPorCalidadMedia(ArrayList<Medida> Valores){
        for (int i = 0;i<Valores.size();i++){
            if(Valores.get(i).getMedida() >= 68 && Valores.get(i).getMedida() <= 162){

                calidadMedia.add(Valores.get(i));
            }
        }
        mAdapter = new AdapterRecyclerViewMisMedidas(calidadMedia ,this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }

    private void filtrarPorCalidadBaja(ArrayList<Medida> Valores){
        for (int i = 0;i<Valores.size();i++){
            if(Valores.get(i).getMedida() > 163){
                calidadBaja.add(Valores.get(i));
            }
        }

        mAdapter = new AdapterRecyclerViewMisMedidas(calidadBaja ,this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
