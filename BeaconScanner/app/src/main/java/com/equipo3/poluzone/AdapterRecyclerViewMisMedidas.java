package com.equipo3.poluzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRecyclerViewMisMedidas extends RecyclerView.Adapter<AdapterRecyclerViewMisMedidas.ViewHolder> {

    private static final String TAG = "AdapterRecyclerMisMedidas";

    private ArrayList<Medida> ListaValores = new ArrayList<>();
   // private ArrayList<String> ListaTipoMedidas = new ArrayList<>();
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView idMedida;
        TextView fechaMedida;
        TextView valorMedida;
        TextView tipoMedida;
        RelativeLayout itemLayout;

        public ViewHolder(View itemView){
            super(itemView);
            itemLayout = itemView.findViewById(R.id.item_layout);
            fechaMedida = itemView.findViewById(R.id.fechamedida);
            valorMedida = itemView.findViewById(R.id.valormedida);
            tipoMedida = itemView.findViewById(R.id.tipomedida);
            idMedida = itemView.findViewById(R.id.idmedida);


        }

    }

    public AdapterRecyclerViewMisMedidas(ArrayList<Medida> listaV, Context mContext) {
        this.ListaValores = listaV;
        //this.ListaTipoMedidas = listaTM;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_medidasitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          Medida ex = ListaValores.get(position);

         holder.valorMedida.setText(Double.toString(ex.getMedida()));
        //holder.tipoMedida.setText(ListaTipoMedidas.get(position));


    }

    @Override
    public int getItemCount() {
        return ListaValores.size();
    }



}
