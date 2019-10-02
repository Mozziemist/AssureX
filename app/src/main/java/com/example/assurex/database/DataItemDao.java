package com.example.assurex.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.assurex.model.DataItem;

import java.util.List;

@Dao
public interface DataItemDao {

    @Insert
    void insertAll(List<DataItem> items);

    @Insert
    void insertAll(DataItem... items);

    @Query("SELECT COUNT(*) from dataitem")
    int countItems();

    @Query("SELECT * FROM dataitem ORDER BY tripId")
    List<DataItem> getAll();

    @Query("SELECT * FROM dataitem WHERE tripId = :tripId")
    DataItem findById(String tripId);

}
