package com.equipo3.poluzone;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainMisMedidas extends AppCompatActivity implements CallbackMisMedidas {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ServidorFake servidorFake;
    ArrayList<Medida> mValores;
    ArrayList<Medida> mTipoMedida;
    ArrayList<Medida> mIdMedida;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recyclerview_listamedidas);

        mValores = new ArrayList<>();
        mTipoMedida = new ArrayList<>();
        mIdMedida = new ArrayList<>();

        servidorFake = new ServidorFake(this);
        long hasta = 1575741203368L;
        servidorFake.getMedidasPorUsuario(0,hasta,15);

        mRecyclerView = findViewById(R.id.recyclerview_medidas);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

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
                mValores.add(medida);

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Funciono?",e.toString());
        }


        mAdapter = new AdapterRecyclerViewMisMedidas(mValores ,this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }
}
