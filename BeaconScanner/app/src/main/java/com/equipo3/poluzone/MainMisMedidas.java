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

import org.json.JSONObject;

import java.util.ArrayList;

public class MainMisMedidas extends AppCompatActivity implements CallbackMisMedidas {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ServidorFake servidorFake;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recyclerview_listamedidas);

        ArrayList<Medida> mValores = new ArrayList<>();
        mValores.add(new Medida(654));
        mValores.add(new Medida(452));
        mValores.add(new Medida(655434));
        mValores.add(new Medida(654524));
        servidorFake = new ServidorFake(this);
        long hasta = 1575741203368L;
        servidorFake.getMedidasPorUsuario(0,hasta,15);

        mRecyclerView = findViewById(R.id.recyclerview_medidas);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new AdapterRecyclerViewMisMedidas(mValores , this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void callbackMisMedidas(JSONObject response) {

        Log.d("Funciono?",response.toString());

    }
}