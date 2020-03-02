package com.example.assurex.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity
public class TripSummary implements Parcelable {

    @PrimaryKey
    @NonNull
    private String tripId;
    @ColumnInfo
    private String date;
    @ColumnInfo
    private int tripNumber;
    @ColumnInfo
    private String notableTripEvents;
    @ColumnInfo
    private String engineStatus;
    @ColumnInfo
    private double currentTripScore;
    @ColumnInfo
    private double totalTripScore;
    @ColumnInfo
    private double averageSpeed;
    @ColumnInfo
    private double topSpeed;
    @ColumnInfo
    private double averageAcceleration;
    @ColumnInfo
    private double averageDeceleration;
    @ColumnInfo
    private double topAcceleration;
    @ColumnInfo
    private double topDeceleration;
    @ColumnInfo
    private String originLocation;
    @ColumnInfo
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

    protected TripSummary(Parcel in) {
        this.tripId = in.readString();
        this.date = in.readString();
        this.notableTripEvents = in.readString();
        this.tripNumber = in.readInt();
        this.engineStatus = in.readString();
        this.currentTripScore = in.readDouble();
        this.totalTripScore = in.readDouble();
        this.averageSpeed = in.readDouble();
        this.topSpeed = in.readDouble();
        this.averageAcceleration = in.readDouble();
        this.averageDeceleration = in.readDouble();
        this.topAcceleration = in.readDouble();
        this.topDeceleration = in.readDouble();
        this.originLocation = in.readString();
        this.destinationLocation = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tripId);
        dest.writeString(this.date);
        dest.writeString(this.notableTripEvents);
        dest.writeInt(this.tripNumber);
        dest.writeString(this.engineStatus);
        dest.writeDouble(this.currentTripScore);
        dest.writeDouble(this.totalTripScore);
        dest.writeDouble(this.averageSpeed);
        dest.writeDouble(this.topSpeed);
        dest.writeDouble(this.averageAcceleration);
        dest.writeDouble(this.averageDeceleration);
        dest.writeDouble(this.topAcceleration);
        dest.writeDouble(this.topDeceleration);
        dest.writeString(this.originLocation);
        dest.writeString(this.destinationLocation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripSummary> CREATOR = new Creator<TripSummary>() {
        @Override
        public TripSummary createFromParcel(Parcel in) {
            return new TripSummary(in);
        }

        @Override
        public TripSummary[] newArray(int size) {
            return new TripSummary[size];
        }
    };
}
