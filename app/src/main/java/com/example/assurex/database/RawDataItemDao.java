package com.example.assurex.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.assurex.model.RawDataItem;

import java.util.List;

@Dao
public interface RawDataItemDao {

    @Insert
    void insertAll(List<RawDataItem> items);

    @Insert
    void insertAll(RawDataItem... items);

    @Insert
    void insert(RawDataItem item);

    @Query("SELECT COUNT(*) from rawdataitem")
    int countItems();

    @Query("SELECT * FROM rawdataitem ORDER BY tripId")
    List<RawDataItem> getAll();

    @Query("SELECT * FROM rawdataitem WHERE tripId = :tripId")
    RawDataItem findById(String tripId);

}
