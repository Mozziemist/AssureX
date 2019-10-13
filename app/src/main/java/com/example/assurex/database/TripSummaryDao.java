package com.example.assurex.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.assurex.model.TripSummary;

import java.util.List;

@Dao
public interface TripSummaryDao {

    @Insert
    void insertAll(List<TripSummary> items);

    @Insert
    void insertAll(TripSummary... items);

    @Insert
    void insert(TripSummary item);

    @Query("SELECT COUNT(*) from tripsummary")
    int countItems();

    @Query("SELECT * FROM tripsummary ORDER BY tripId")
    List<TripSummary> getAll();

    @Query("SELECT * FROM tripsummary WHERE tripId = :tripId")
    TripSummary findById(String tripId);
}
