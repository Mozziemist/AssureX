package com.example.assurex.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

public class TripSummary {


    private String tripId;
    private String date;
    private int tripNumber;
    private String notableTripEvents;
    private String engineStatus;
    private double currentTripScore;
    private double totalTripScore;
    private double averageSpeed;
    private double topSpeed;
    private double averageAcceleration;
    private double averageDeceleration;
    private double topAcceleration;
    private double topDeceleration;
    private String originLocation;
    private String destinationLocation;




    public TripSummary(){
    }

    //actual constructor
    @Ignore
    public TripSummary(String tripId, String date, int tripNumber, String notableTripEvents,
                       String engineStatus, double currentTripScore, double totalTripScore,
                       double averageSpeed, double topSpeed, double averageAcceleration,
                       double averageDeceleration, double topAcceleration, double topDeceleration,
                       String originLocation, String destinationLocation){

        if (tripId == null) {
            tripId = UUID.randomUUID().toString();
            /*
             * todo set tripID to a value that is the same for every entry for the same trip
             *  but changes to a new value for another trip. Perhaps set to change tripId if
             *   obd2 connection is terminated and assume vehicle has turned off
             * */
        }

        this.tripId = tripId;
        this.date = date;
        this.tripNumber = tripNumber;
        this.notableTripEvents = notableTripEvents;
        this.engineStatus = engineStatus;
        this.currentTripScore = currentTripScore;
        this.totalTripScore = totalTripScore;
        this.averageSpeed = averageSpeed;
        this.topSpeed = topSpeed;
        this.averageAcceleration = averageAcceleration;
        this.averageDeceleration = averageDeceleration;
        this.topAcceleration = topAcceleration;
        this.topDeceleration = topDeceleration;
        this.originLocation = originLocation;
        this.destinationLocation = destinationLocation;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(int tripNumber) {
        this.tripNumber = tripNumber;
    }

    public String getNotableTripEvents() {
        return notableTripEvents;
    }

    public void setNotableTripEvents(String notableTripEvents) { this.notableTripEvents = notableTripEvents; }

    public String getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(String engineStatus) {
        this.engineStatus = engineStatus;
    }

    public double getCurrentTripScore() { return currentTripScore; }

    public void setCurrentTripScore(double currentTripScore) { this.currentTripScore = currentTripScore; }

    public double getTotalTripScore() { return totalTripScore; }

    public void setTotalTripScore(double totalTripScore) { this.totalTripScore = totalTripScore; }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(double topSpeed) {
        this.topSpeed = topSpeed;
    }

    public double getAverageAcceleration() {
        return averageAcceleration;
    }

    public void setAverageAcceleration(double averageAcceleration) { this.averageAcceleration = averageAcceleration; }

    public double getAverageDeceleration() { return averageDeceleration; }

    public void setAverageDeceleration(double averageDeceleration) { this.averageDeceleration = averageDeceleration; }

    public double getTopAcceleration() {
        return topAcceleration;
    }

    public double getTopDeceleration() { return topDeceleration; }

    public void setTopDeceleration(double topDeceleration) { this.topDeceleration = topDeceleration; }

    public void setTopAcceleration(double topAcceleration) { this.topAcceleration = topAcceleration; }

    public String getOriginLocation() { return originLocation; }

    public void setOriginLocation(String originLocation) { this.originLocation = originLocation; }

    public String getDestinationLocation() { return destinationLocation; }

    public void setDestinationLocation(String destinationLocation) { this.destinationLocation = destinationLocation; }

}
