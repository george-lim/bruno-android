package com.cs446.group7.bruno.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = WalkRunSession.TABLE_NAME)
public class WalkRunSession {
    static final String TABLE_NAME = "sessions";

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "session_data")
    private String sessionDataString;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @NonNull
    public String getSessionDataString() {
        return sessionDataString;
    }

    public void setSessionDataString(@NonNull String sessionDataString) {
        this.sessionDataString = sessionDataString;
    }
}
