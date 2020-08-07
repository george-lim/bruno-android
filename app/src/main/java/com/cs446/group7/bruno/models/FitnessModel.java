package com.cs446.group7.bruno.models;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.persistence.FitnessRecord;
import com.cs446.group7.bruno.persistence.FitnessRecordDAO;
import com.cs446.group7.bruno.persistence.FitnessRecordEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FitnessModel extends ViewModel {
    private List<FitnessRecord> fitnessRecords = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();

    // Loads fitness records from DB
    public void loadFitnessRecords() {
        fitnessRecords.clear();

        final FitnessRecordDAO fitnessRecordDAO = MainActivity.getPersistenceService().getFitnessRecordDAO();
        final List<FitnessRecordEntry> entries = fitnessRecordDAO.getRecords();

        for (final FitnessRecordEntry entry : entries) {
            try {
                fitnessRecords.add(FitnessRecord.deserialize(entry.getRecordDataString()));
            } catch (IOException | ClassNotFoundException e) {
                // Happens when the structure of FitnessRecordData changes so we must discard all old data
                Log.e(TAG, "Failed to load record: " + e.toString());
                fitnessRecordDAO.delete(entry);
            }
        }

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
