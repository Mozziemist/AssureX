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
    private String currentStanding;
    @ColumnInfo
    private String engineStatus;
    @ColumnInfo
    private double averageSpeed;
    @ColumnInfo
    private double topSpeed;
    @ColumnInfo
    private double averageAcceleration;
    @ColumnInfo
    private double topAcceleration;





    public TripSummary(){
    }

    //actual constructor
    @Ignore
    public TripSummary(String tripId, String date, int tripNumber, String currentStanding,
                       String engineStatus, double averageSpeed, double topSpeed,
                       double averageAcceleration, double topAcceleration){

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
        this.currentStanding = currentStanding;
        this.engineStatus = engineStatus;
        this.averageSpeed = averageSpeed;
        this.topSpeed = topSpeed;
        this.averageAcceleration = averageAcceleration;
        this.topAcceleration = topAcceleration;
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

    public String getCurrentStanding() {
        return currentStanding;
    }

    public void setCurrentStanding(String currentStanding) {
        this.currentStanding = currentStanding;
    }

    public String getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(String engineStatus) {
        this.engineStatus = engineStatus;
    }

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

    public void setAverageAcceleration(double averageAcceleration) {
        this.averageAcceleration = averageAcceleration;
    }

    public double getTopAcceleration() {
        return topAcceleration;
    }

    public void setTopAcceleration(double topAcceleration) {
        this.topAcceleration = topAcceleration;
    }

    protected TripSummary(Parcel in) {
        this.tripId = in.readString();
        this.date = in.readString();
        this.currentStanding = in.readString();
        this.tripNumber = in.readInt();
        this.engineStatus = in.readString();
        this.averageSpeed = in.readDouble();
        this.topSpeed = in.readDouble();
        this.averageAcceleration = in.readDouble();
        this.topAcceleration = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tripId);
        dest.writeString(this.date);
        dest.writeString(this.currentStanding);
        dest.writeInt(this.tripNumber);
        dest.writeString(this.engineStatus);
        dest.writeDouble(this.averageSpeed);
        dest.writeDouble(this.topSpeed);
        dest.writeDouble(this.averageAcceleration);
        dest.writeDouble(this.topAcceleration);
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
