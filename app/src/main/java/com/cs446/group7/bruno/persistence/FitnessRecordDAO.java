package com.cs446.group7.bruno.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FitnessRecordDAO {
    @Insert
    void insert(FitnessRecordEntry... records);

    @Update
    void update(FitnessRecordEntry... records);

    @Delete
    void delete(FitnessRecordEntry... records);

    @Query("DELETE FROM " + FitnessRecordEntry.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + FitnessRecordEntry.TABLE_NAME)
    List<FitnessRecordEntry> getRecords();

    @Query("SELECT * FROM " + FitnessRecordEntry.TABLE_NAME + " WHERE uid = :uid")
    FitnessRecordEntry getRecordWithUID(int uid);
}
