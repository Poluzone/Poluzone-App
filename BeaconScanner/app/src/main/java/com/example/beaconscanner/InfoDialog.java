package com.example.beaconscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InfoDialog extends DialogFragment {

    DialogFragment dialogFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogFragment = this;
        View view = inflater.inflate(R.layout.popup_info_calidadaire,container,false);
        getDialog().setTitle(R.string.calidad);
        Button doneBtn = (Button) view.findViewById(R.id.botonCerrarDialogInfos);
        doneBtn.setOnClickListener(doneAction);
        return view;
    }

    View.OnClickListener doneAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),"Test",Toast.LENGTH_LONG).show();
            dialogFragment.dismiss();
        }
    };

}
