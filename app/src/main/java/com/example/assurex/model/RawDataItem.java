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

@Entity
public class RawDataItem implements Parcelable {

    //@PrimaryKey(autoGenerate = true)
    @PrimaryKey
    @NonNull
    private String tripDatedTimeStamp;
    @ColumnInfo
    private String tripId;
    @ColumnInfo
    private String date;
    @ColumnInfo
    private String timeStamp;
    @ColumnInfo
    private int speedLimit;
    @ColumnInfo
    private int speed;
    @ColumnInfo
    private double accelerationRate;
    @ColumnInfo
    private double latitude;
    @ColumnInfo
    private double longitude;
    @ColumnInfo
    private String nearestAddress;

    public RawDataItem() {
    }

    //public RawDataItem(String tripId, String date, String timeStamp, double topSpeed) {
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

    @Override
    public String toString() {
        return "RawDataItem{" +
                "tripId='" + tripId + '\'' +
                ", date='" + date + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", Speed='" + speed + '\'' +
                ", accelRate='" + accelerationRate +
                '}';
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tripDatedTimeStamp);
        dest.writeString(this.tripId);
        dest.writeString(this.date);
        dest.writeString(this.timeStamp);
        dest.writeInt(this.speedLimit);
        dest.writeInt(this.speed);
        dest.writeDouble(this.accelerationRate);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.nearestAddress);
    }

    protected RawDataItem(Parcel in) {
        this.tripDatedTimeStamp = in.readString();
        this.tripId = in.readString();
        this.date = in.readString();
        this.timeStamp = in.readString();
        this.speedLimit = in.readInt();
        this.speed = in.readInt();
        this.accelerationRate = in.readDouble();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.nearestAddress = in.readString();
    }

    public static final Parcelable.Creator<RawDataItem> CREATOR = new Parcelable.Creator<RawDataItem>() {
        @Override
        public RawDataItem createFromParcel(Parcel source) {
            return new RawDataItem(source);
        }

        @Override
        public RawDataItem[] newArray(int size) {
            return new RawDataItem[size];
        }
    };
}
