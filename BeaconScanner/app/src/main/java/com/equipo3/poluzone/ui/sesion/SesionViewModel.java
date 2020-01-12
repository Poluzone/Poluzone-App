// -----------------------------------------------------------------------
// SesionViewModel.java
// Equipo 3
// Autor: Emilia Rosa van der Heide
// Fecha: 11/2019
// CopyRight:
// -----------------------------------------------------------------------
package com.equipo3.poluzone.ui.sesion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SesionViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SesionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is send fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}