package com.cs446.group7.bruno.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = { FitnessRecordEntry.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FitnessRecordDAO getRecordDAO();
}
