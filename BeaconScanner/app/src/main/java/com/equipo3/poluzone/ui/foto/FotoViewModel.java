// -----------------------------------------------------------------------
// FotoFragment.java
// Equipo 3
// Autor: Iván Romero Ruíz
// Fecha: 10/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone.ui.foto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FotoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FotoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}