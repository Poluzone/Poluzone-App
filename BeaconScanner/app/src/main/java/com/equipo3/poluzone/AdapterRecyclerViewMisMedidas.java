// -----------------------------------------------------------------------
// AdapterRecyclerViewMisMedidas.java
// Equipo 3
// Autor: Josep Carreres
// Fecha: 11/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdapterRecyclerViewMisMedidas extends RecyclerView.Adapter<AdapterRecyclerViewMisMedidas.ViewHolder> {

    private static final String TAG = "AdapterRecyclerMisMedidas";

    private ArrayList<Medida> ListaValores = new ArrayList<>();
    private ArrayList<Medida> ListaTipoMedidas = new ArrayList<>();
    private ArrayList<Medida> ListaIdMedida = new ArrayList<>();
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView idMedida;
        TextView fechaMedida;
        TextView valorMedida;
        //TextView tipoMedida;
        //RelativeLayout itemLayout;

        public ViewHolder(View itemView){
            super(itemView);


            //itemLayout = itemView.findViewById(R.id.item_layout);
            fechaMedida = itemView.findViewById(R.id.fechamedida);
            valorMedida = itemView.findViewById(R.id.valormedida);
           // tipoMedida = itemView.findViewById(R.id.tipomedida);
            idMedida = itemView.findViewById(R.id.idmedida);

        }

    }

    public AdapterRecyclerViewMisMedidas(ArrayList<Medida> listaV, Context mContext) {
        this.ListaValores = listaV;
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

        DecimalFormat df = new DecimalFormat("#.0");

        if(ex.getMedida()<67){
            holder.valorMedida.setTextColor(Color.parseColor("#CAFECF"));
        }
        if(ex.getMedida()>68 && ex.getMedida()<162){
            holder.valorMedida.setTextColor(Color.parseColor("#FFB200"));
        }
        if(ex.getMedida()>163){
            holder.valorMedida.setTextColor(Color.parseColor("#FF0000"));
        }

         holder.valorMedida.setText(df.format(ex.getMedida()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ex.getTiempo());

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Integer.toString(mYear);
        Integer.toString(mDay);
        Integer.toString(mMonth);

         holder.fechaMedida.setText(mDay +"/"+"01"+"/"+mYear);
         holder.idMedida.setText("ID: "+Integer.toString(ex.getIdMedida()));


    }

    @Override
    public int getItemCount() {
        return ListaValores.size();
    }



}
