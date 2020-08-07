package com.cs446.group7.bruno.models;

import androidx.lifecycle.ViewModel;

import com.cs446.group7.bruno.persistence.FitnessRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FitnessModel extends ViewModel {
    private List<FitnessRecord> fitnessRecords = new ArrayList<>();

    // Loads fitness records from DB
    public void loadFitnessRecords() {
        fitnessRecords.clear();

        // TODO: Add mock data here

        // Sort descending by the date the of the exercise
        // the Date class already implements Comparable so less work for us
        Collections.sort(fitnessRecords, (record1, record2) -> record2.getStartTime()
                .compareTo(record1.getStartTime()));
    }

    public FitnessRecord getFitnessRecord(int fitnessRecordIndex) {
        return fitnessRecords.get(fitnessRecordIndex);
    }

    public List<FitnessRecord> getFitnessRecords() {
        return fitnessRecords;
    }
}
