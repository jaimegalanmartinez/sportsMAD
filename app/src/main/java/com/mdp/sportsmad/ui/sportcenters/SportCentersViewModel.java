package com.mdp.sportsmad.ui.sportcenters;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SportCentersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SportCentersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Loading sport centers");
    }

    public LiveData<String> getText() {
        return mText;
    }
}