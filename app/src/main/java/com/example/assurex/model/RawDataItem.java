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
    private String tripId;
    @ColumnInfo
    private String date;
    @ColumnInfo
    private String timeStamp;
    @ColumnInfo
    private double speed;
    @ColumnInfo
    private double accelerationRate;

    public RawDataItem() {
    }

    //public RawDataItem(String tripId, String date, String timeStamp, double topSpeed) {
    @Ignore
    public RawDataItem(String tripId, String date, String timeStamp, double speed, double accelerationRate) {

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
        this.timeStamp = timeStamp;
        this.speed = speed;
        this.accelerationRate = accelerationRate;
    }

    public String getTripId() { return tripId; }

    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTimeStamp() { return timeStamp; }

    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public double getSpeed() { return speed; }

    public void setSpeed(double speed) { this.speed = speed; }

    public double getAccelerationRate() { return accelerationRate; }

    public void setAccelerationRate(double accelerationRate) { this.accelerationRate = accelerationRate; }

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
        dest.writeString(this.tripId);
        dest.writeString(this.date);
        dest.writeString(this.timeStamp);
        dest.writeDouble(this.speed);
        dest.writeDouble(this.accelerationRate);
    }

    protected RawDataItem(Parcel in) {
        this.tripId = in.readString();
        this.date = in.readString();
        this.timeStamp = in.readString();
        this.speed = in.readDouble();
        this.accelerationRate = in.readDouble();
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
