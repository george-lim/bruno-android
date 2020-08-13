package com.bruno.android.persistence;

import android.content.Context;

import androidx.room.Room;

import com.bruno.android.BuildConfig;

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
