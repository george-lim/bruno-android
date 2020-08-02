package com.cs446.group7.bruno.models;

import android.util.Log;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.persistence.FitnessRecordDAO;
import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.persistence.FitnessRecordEntry;
import com.cs446.group7.bruno.persistence.MockFitnessRecordDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class FitnessModel extends ViewModel {

    private List<FitnessRecordData> fitnessRecordDataList = new ArrayList<>();
    private int selectedIndex;

    private final String TAG = getClass().getSimpleName();

    public void loadFitnessRecords() {
        fitnessRecordDataList.clear();

        final FitnessRecordDAO fitnessRecordDAO = BuildConfig.DEBUG
                ? new MockFitnessRecordDAO()
                : MainActivity.getPersistenceService().getFitnessRecordDAO();

        final List<FitnessRecordEntry> entries = fitnessRecordDAO.getRecords();

        for (final FitnessRecordEntry entry : entries) {
            try {
                fitnessRecordDataList.add(FitnessRecordData.deserialize(entry.getRecordDataString()));
            } catch (IOException | ClassNotFoundException e) {
                Log.e(TAG, "Failed to load record: " + e.toString());
            }
        }
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public FitnessRecordData getCurrentFitnessRecord() {
        return fitnessRecordDataList.get(selectedIndex);
    }

    public List<FitnessRecordData> getFitnessRecords() {
        return fitnessRecordDataList;
    }
}
