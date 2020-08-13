package com.bruno.android.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = FitnessRecordEntry.TABLE_NAME)
public class FitnessRecordEntry {
    static final String TABLE_NAME = "fitness_records";

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "record_data")
    private String recordDataString;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @NonNull
    public String getRecordDataString() {
        return recordDataString;
    }

    public void setRecordDataString(@NonNull String recordDataString) {
        this.recordDataString = recordDataString;
    }
}
