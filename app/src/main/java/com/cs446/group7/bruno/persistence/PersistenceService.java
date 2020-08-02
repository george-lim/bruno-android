package com.cs446.group7.bruno.persistence;

import android.content.Context;

import androidx.room.Room;

public class PersistenceService {
    private AppDatabase database;
    private WalkRunSessionDAO sessionDAO;

    public PersistenceService(final Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, WalkRunSession.TABLE_NAME)
                .allowMainThreadQueries()
                .build();
        sessionDAO = database.getSessionDAO();
    }

    public WalkRunSessionDAO getWalkRunSessionDAO() {
        return sessionDAO;
    }
}
