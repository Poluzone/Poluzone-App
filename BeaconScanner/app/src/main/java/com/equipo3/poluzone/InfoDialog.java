package com.equipo3.poluzone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

// -----------------------------------------------------------------------
// InfoDialog.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// CopyRight:
// Popup que se muestra cuando se pulsa el botón info de la calidad del aire
// -----------------------------------------------------------------------
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
            dialogFragment.dismiss();
            dialogFragment.dismissAllowingStateLoss();
            getFragmentManager().executePendingTransactions();
        }
    };

}
