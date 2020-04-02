package com.example.assurex.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.UUID;


public class RawDataItem {

    private String tripDatedTimeStamp;
    private String tripId;
    private String date;
    private String timeStamp;
    private int speedLimit;
    private int speed;
    private double accelerationRate;
    private double latitude;
    private double longitude;
    private String nearestAddress;

    public RawDataItem() {
    }

    @Ignore
    public RawDataItem(String tripDatedTimeStamp, String tripId, String date, String timeStamp,
                       int speedLimit, int speed, double accelerationRate,
                       double latitude, double longitude, String nearestAddress) {

        if (tripId == null) {
            tripId = UUID.randomUUID().toString();
            /*
            * todo set tripID to a value that is the same for every entry for the same trip
            *  but changes to a new value for another trip. Perhaps set to change tripId if
            *   obd2 connection is terminated and assume vehicle has turned off
            * */
        }
        this.tripDatedTimeStamp = tripDatedTimeStamp;
        this.tripId = tripId;
        this.date = date;
        this.timeStamp = timeStamp;
        this.speedLimit = speedLimit;
        this.speed = speed;
        this.accelerationRate = accelerationRate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nearestAddress = nearestAddress;
    }

    public String getTripDatedTimeStamp() { return tripDatedTimeStamp; }

    public void setTripDatedTimeStamp(String tripDatedTimeStamp) { this.tripDatedTimeStamp = tripDatedTimeStamp; }

    public String getTripId() { return tripId; }

    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTimeStamp() { return timeStamp; }

    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public int getSpeedLimit() { return speedLimit; }

    public void setSpeedLimit(int speedLimit) { this.speedLimit = speedLimit; }

    public int getSpeed() { return speed; }

    public void setSpeed(int speed) { this.speed = speed; }

    public double getAccelerationRate() { return accelerationRate; }

    public void setAccelerationRate(double accelerationRate) { this.accelerationRate = accelerationRate; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getNearestAddress() { return nearestAddress; }

    public void setNearestAddress(String nearestAddress) { this.nearestAddress = nearestAddress; }
}
