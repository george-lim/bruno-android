package com.cs446.group7.bruno.persistence;

import android.content.Context;

import androidx.room.Room;

import com.cs446.group7.bruno.BuildConfig;

public class PersistenceService {
    private FitnessRecordDAO fitnessRecordDAO;

    public PersistenceService(final Context context) {
        AppDatabase database = Room
                .databaseBuilder(context, AppDatabase.class, FitnessRecordEntry.TABLE_NAME)
                .allowMainThreadQueries()
                .build();

        fitnessRecordDAO = BuildConfig.DEBUG
                ? new DynamicFitnessRecordDAO(
                        database.getRecordDAO(),
                        new MockFitnessRecordDAO()
                )
                : database.getRecordDAO();
    }

    public FitnessRecordDAO getFitnessRecordDAO() {
        return fitnessRecordDAO;
    }
}
