package com.example.cinem.ui.cinemas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CinemasViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CinemasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Cinemas fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}