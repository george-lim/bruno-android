package com.cs446.group7.bruno.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = WalkRunSession.TABLE_NAME)
public class WalkRunSession {
    static final String TABLE_NAME = "sessions";

    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "my_data")
    @NonNull
    private String myData;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @NonNull
    public String getMyData() {
        return myData;
    }

    public void setMyData(@NonNull String myData) {
        this.myData = myData;
    }
}
