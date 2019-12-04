package com.equipo3.poluzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRecyclerViewMisMedidas extends RecyclerView.Adapter<AdapterRecyclerViewMisMedidas.ViewHolder> {

    private static final String TAG = "AdapterRecyclerMisMedidas";
    private Context mContext;

    public AdapterRecyclerViewMisMedidas(Context context){

        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recyclerview_listamedidas,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(View itemView){
            super(itemView);
        }

    }
}
