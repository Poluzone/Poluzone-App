package com.example.beaconscanner.ui.foto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.beaconscanner.R;

public class FotoFragment extends Fragment {
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Polufoto
    //---------------------------------------------------------------------------
    private FotoViewModel fotoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fotoViewModel =
                ViewModelProviders.of(this).get(FotoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_foto, container, false);
        final TextView textView = root.findViewById(R.id.text_foto);
        fotoViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}