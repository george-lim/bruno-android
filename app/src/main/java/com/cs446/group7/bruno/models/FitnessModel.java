package com.cs446.group7.bruno.models;

import android.util.Log;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.persistence.FitnessRecordDAO;
import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.persistence.FitnessRecordEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class FitnessModel extends ViewModel {

    private List<FitnessRecordData> fitnessRecordDataList = new ArrayList<>();
    private int selectedIndex; // Indicates which record is selected

    private final String TAG = getClass().getSimpleName();

    // Loads past fitness data from DB
    public void loadFitnessRecords() {
        fitnessRecordDataList.clear();

        final FitnessRecordDAO fitnessRecordDAO = MainActivity.getPersistenceService().getFitnessRecordDAO();
        final List<FitnessRecordEntry> entries = fitnessRecordDAO.getRecords();

        for (final FitnessRecordEntry entry : entries) {
            try {
                fitnessRecordDataList.add(FitnessRecordData.deserialize(entry.getRecordDataString()));
            } catch (IOException | ClassNotFoundException e) {
                // Happens when the structure of FitnessRecordData changes so we must discard all old data
                Log.e(TAG, "Failed to load record: " + e.toString());
                fitnessRecordDAO.delete(entry);
            }
        }

        // Sort descending by the date the of the exercise
        // the Date class already implements Comparable so less work for us
        Collections.sort(fitnessRecordDataList, (record1, record2) -> record2.getStartTime()
                .compareTo(record1.getStartTime()));
    }

    public void setSelectedIndex(final int index) {
        this.selectedIndex = index;
    }

    public FitnessRecordData getCurrentFitnessRecord() {
        return fitnessRecordDataList.get(selectedIndex);
    }

    public List<FitnessRecordData> getFitnessRecords() {
        return fitnessRecordDataList;
    }
}
