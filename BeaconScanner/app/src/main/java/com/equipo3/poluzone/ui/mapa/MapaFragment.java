/**
 * Fichero encargado de la creación del mapa.
 *
 * - Matthew Conde Oltra -
 */

package com.equipo3.poluzone.ui.mapa;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.equipo3.poluzone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leinardi.android.speeddial.SpeedDialView;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    //Etiqueta para el debugging
    String TAG = "MAPA";
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Mapa
    //---------------------------------------------------------------------------
    GoogleMap map;
    private MapaViewModel mapaViewModel;
    private SpeedDialView speedDialView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_maps, container, false);
        // acceder speed dial
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.show();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng pp = new LatLng(38.996100, -0.166439);

        try {
            // Para customizar el mapa, añadimos un estilo en un fichero JSON
            // que contiene el estilo del mapa, podemos personalizarlo nosotros
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getContext(), R.raw.style_map_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "No se ha encontrado el estilo. Error: ", e);
        }

        MarkerOptions option = new MarkerOptions();
        option.position(pp).title("UPV").draggable(true).
                snippet("upv gandia").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        //map.setMyLocationEnabled(true);
        map.setMinZoomPreference(6.0f);
        map.setMaxZoomPreference(15.0f);
        map.addMarker(option);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pp, 15.0f));


    }
}