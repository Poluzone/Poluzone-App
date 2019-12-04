package com.equipo3.poluzone;

import android.os.Bundle;
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

public class MainMisMedidas extends Fragment {
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview_listamedidas, container, false);

        initRecyclerView(rootView);
        return rootView;
    }

    private void initRecyclerView(View view){



        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_medidas);
        AdapterRecyclerViewMisMedidas adapter = new AdapterRecyclerViewMisMedidas(this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));


    }

}
