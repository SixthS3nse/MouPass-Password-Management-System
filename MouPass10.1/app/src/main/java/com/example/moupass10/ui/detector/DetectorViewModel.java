package com.example.moupass10.ui.detector;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetectorViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public DetectorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Detector");
    }

    public LiveData<String> getText() {
        return mText;
    }
}