package com.cs446.group7.bruno.persistence;

import android.content.Context;

import androidx.room.Room;

import com.cs446.group7.bruno.BuildConfig;

public class PersistenceService {
    private AppDatabase database;
    private FitnessRecordDAO recordDAO;

    public PersistenceService(final Context context) {
        if (BuildConfig.DEBUG) {
            recordDAO = new MockFitnessRecordDAO();
        } else {
            database = Room.databaseBuilder(context, AppDatabase.class, FitnessRecordEntry.TABLE_NAME)
                    .allowMainThreadQueries()
                    .build();
            recordDAO = database.getRecordDAO();
        }
    }

    public FitnessRecordDAO getFitnessRecordDAO() {
        return recordDAO;
    }
}
