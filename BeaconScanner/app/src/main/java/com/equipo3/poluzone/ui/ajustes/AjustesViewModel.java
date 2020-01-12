// -----------------------------------------------------------------------
// AjustesViewModel.java
// Equipo 3
// Autor: Iván Romero Ruíz
// Fecha: 10/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone.ui.ajustes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AjustesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AjustesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}