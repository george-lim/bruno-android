package com.cs446.group7.bruno.models;

import androidx.lifecycle.ViewModel;

import com.cs446.group7.bruno.persistence.FitnessRecordData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FitnessModel extends ViewModel {

    public enum Winner { YOU, BRUNO, TIE }

    private List<FitnessRecordData> fitnessRecordDataList = new ArrayList<>();
    private int selectedIndex; // Indicates which record is selected

    private final String TAG = getClass().getSimpleName();

    // Loads past fitness data from DB
    public void loadFitnessRecords() {
        fitnessRecordDataList.clear();

        // MOCK: - Dummy data

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
