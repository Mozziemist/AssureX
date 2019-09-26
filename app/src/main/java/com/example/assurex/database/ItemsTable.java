package com.example.assurex.database;

public class ItemsTable {
    public static final String TABLE_ITEMS = "assuredTable"; // name can be anything you want it to be
    public static final String COLUMN_TRIP_ID = "tripId";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_ACCELRATE = "accelRate";

    //date
    //hazard level
    //avg speed
    //top speed
    //diagnostic messages
    //top acceleration rate
    //avg acceleration rate
    //top deceleration rate
    //avg deceleration rate

    //structure of table created here
    //important to match datatypes of columns to the types
    //of your model fields
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_ITEMS + "(" +
                    COLUMN_TRIP_ID + " TEXT PRIMARY KEY," +
                    COLUMN_DATE + " TEXT," +
                    COLUMN_TIMESTAMP + " TEXT," +
                    COLUMN_SPEED + " REAL," + //Real column matches the double type in java
                    COLUMN_ACCELRATE + " REAL" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_ITEMS;
}
