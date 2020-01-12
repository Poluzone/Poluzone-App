// -----------------------------------------------------------------------
// PerfilViewModel.java
// Equipo 3
// Autor: Iván Romero Ruíz
// Fecha: 10/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PerfilViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PerfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is perfil fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}