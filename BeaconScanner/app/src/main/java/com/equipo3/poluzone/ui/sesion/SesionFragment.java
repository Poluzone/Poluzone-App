package com.equipo3.poluzone.ui.sesion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.equipo3.poluzone.LoginActivity;
import com.equipo3.poluzone.R;

import static android.content.Context.MODE_PRIVATE;

public class SesionFragment extends Fragment {

    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Cerrar sesion
    //---------------------------------------------------------------------------

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        // Borramos to do de sharedpreferences
        SharedPreferences loginPreferences = getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = loginPreferences.edit();
        editor.remove("email");
        editor.remove("passEncriptado");
        editor.remove("passSinEncriptar");
        editor.remove("telefono");
        editor.commit();

        // Empezar el loginactivity y terminar el mainactivity
        Intent i = new Intent(getActivity(), LoginActivity.class);
        Log.d("pruebas", "intent main");
        this.startActivity(i);
        this.getActivity().finish();
        return root;
    }
}