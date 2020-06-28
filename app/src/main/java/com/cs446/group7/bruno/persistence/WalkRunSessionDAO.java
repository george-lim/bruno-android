package com.cs446.group7.bruno.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WalkRunSessionDAO {
    @Insert
    public void insert(WalkRunSession... sessions);

    @Update
    public void update(WalkRunSession... sessions);

    @Delete
    public void delete(WalkRunSession... sessions);

    @Query("SELECT * FROM " + WalkRunSession.TABLE_NAME)
    public List<WalkRunSession> getSessions();

    @Query("SELECT * FROM " + WalkRunSession.TABLE_NAME + " WHERE uid = :uid")
    public WalkRunSession getSessionWithUID(int uid);
}
