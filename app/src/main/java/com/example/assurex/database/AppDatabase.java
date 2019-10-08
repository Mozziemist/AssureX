package com.example.assurex.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.assurex.model.RawDataItem;
import com.example.assurex.model.TripSummary;

@Database(entities = {RawDataItem.class, TripSummary.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract RawDataItemDao rawDataItemDao();
    public abstract TripSummaryDao tripSummaryDao();

    public static AppDatabase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "assurex-db")
                    //recommended to not use allowMainThreadQueries()
                    //.allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static void destroyInstance(){
        instance = null;
    }
}
