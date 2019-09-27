package com.example.assurex.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.assurex.database.ItemsTable;

import java.util.Calendar;
import java.util.UUID;

public class DataItem implements Parcelable {
    
    //tripID
    //date
    //timeStamp
    //avg speed
    //top speed
    //top acceleration rate
    //avg acceleration rate
    //top deceleration rate
    //avg deceleration rate

    private String tripId;
    private String date;
    private String timeStamp;
    private double speed;
    private double accelRate;

    public DataItem() {
    }

    //public DataItem(String tripId, String date, String timeStamp, double topSpeed) {
    public DataItem(String tripId, String date, String timeStamp, double speed, double accelRate) {


        if (tripId == null) {
            tripId = UUID.randomUUID().toString();
            /*
            * todo set tripID to a value that is the same for every entry for the same trip
            *  but changes to a new value for another trip. Perhaps set to change tripId if
            *   obd2 connection is termimnated and assume vehicle has turned off
            * */
        }

        Calendar calendar = Calendar.getInstance();

        this.tripId = tripId;
        this.date = date;
        this.timeStamp = timeStamp;
        this.speed = speed;
        this.accelRate = accelRate;
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

    //public void setDate(String Date) {
    //    this.date = Date;
    //}

    public String getTimeStamp() {
        return timeStamp;
    }

    //public void setTimeStamp(String timeStamp) {
    //    this.timeStamp = timeStamp;
    //}


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAccelRate() {
        return accelRate;
    }

    public void setAccelRate(double accelRate) {
        this.accelRate = accelRate;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(5); //7 columns, so passed value 7

        values.put(ItemsTable.COLUMN_TRIP_ID, tripId);
        values.put(ItemsTable.COLUMN_DATE, date);
        values.put(ItemsTable.COLUMN_TIMESTAMP, timeStamp);
        values.put(ItemsTable.COLUMN_SPEED, speed);
        values.put(ItemsTable.COLUMN_ACCELRATE, accelRate);
        return values;

    }

    @Override
    public String toString() {
        return "DataItem{" +
                "tripId='" + tripId + '\'' +
                ", date='" + date + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", Speed='" + speed + '\'' +
                ", accelRate='" + accelRate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tripId);
        dest.writeString(this.date);
        dest.writeString(this.timeStamp);
        dest.writeDouble(this.speed);
        dest.writeDouble(this.accelRate);
    }

    protected DataItem(Parcel in) {
        this.tripId = in.readString();
        this.date = in.readString();
        this.timeStamp = in.readString();
        this.speed = in.readDouble();
        this.accelRate = in.readDouble();
    }

    public static final Parcelable.Creator<DataItem> CREATOR = new Parcelable.Creator<DataItem>() {
        @Override
        public DataItem createFromParcel(Parcel source) {
            return new DataItem(source);
        }

        @Override
        public DataItem[] newArray(int size) {
            return new DataItem[size];
        }
    };
}
