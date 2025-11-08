package com.bruno.android.models;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.bruno.android.MainActivity;
import com.bruno.android.persistence.FitnessRecord;
import com.bruno.android.persistence.FitnessRecordDAO;
import com.bruno.android.persistence.FitnessRecordEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FitnessModel extends ViewModel {
    private final List<FitnessRecord> fitnessRecords = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();

    // Loads fitness records from DB
    public void loadFitnessRecords() {
        fitnessRecords.clear();

        final FitnessRecordDAO fitnessRecordDAO = MainActivity.getPersistenceService().getFitnessRecordDAO();
        final List<FitnessRecordEntry> entries = fitnessRecordDAO.getRecords();

        try { // Load data from DB
            for (final FitnessRecordEntry entry : entries) {
                fitnessRecords.add(FitnessRecord.deserialize(entry.getRecordDataString()));
            }
        } catch (IOException | ClassNotFoundException e) {
            // Happens when the structure of FitnessRecord changes so we must discard all old data
            fitnessRecords.clear();
            Log.e(TAG, "Failed to load records, deleting old data: " + e);
            fitnessRecordDAO.deleteAll();
        }

        // Sort descending by the date the of the exercise
        // the Date class already implements Comparable so less work for us
        fitnessRecords.sort((record1, record2) -> record2.getStartTime()
                .compareTo(record1.getStartTime()));
    }

    public FitnessRecord getFitnessRecord(int fitnessRecordIndex) {
        return fitnessRecords.get(fitnessRecordIndex);
    }

    public List<FitnessRecord> getFitnessRecords() {
        return fitnessRecords;
    }
}
