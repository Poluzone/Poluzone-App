package com.equipo3.poluzone;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustomInfoWindowAdapter";
    private LayoutInflater inflater;

    public TextView nombre;
    public TextView SO2;
    public TextView CO;
    public TextView NO;
    public TextView NO2;
    public TextView NOX;
    public TextView O3;


    public String n;
    public String s;
    public String c;
    public String no;
    public String no2;
    public String nox;
    public String o;


    public CustomInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }

    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = inflater.inflate(R.layout.info_marker, null);
        //String[] info = m.getTitle().split("&");
        //String url = m.getSnippet();

        nombre = v.findViewById(R.id.info_nombre);
        SO2 = v.findViewById(R.id.info_SO2);
        CO = v.findViewById(R.id.info_CO);
        NO = v.findViewById(R.id.info_NO);
        NO2 = v.findViewById(R.id.info_NO2);
        NOX = v.findViewById(R.id.info_NOX);
        O3 = v.findViewById(R.id.info_O3);
        nombre.setText(n);
        SO2.setText(s);
        CO.setText(c);
        NO.setText(no);
        NO2.setText(no2);
        NOX.setText(nox);
        O3.setText(o);
        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

}
