package com.example.moupass10.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<List<DataItem>> items;

    public DashboardViewModel() {
        items = new MutableLiveData<>();
    }

    public void setItems(List<DataItem> items) {
        this.items.setValue(items);
    }

    public LiveData<List<DataItem>> getItems() {
        return items;
    }
}

