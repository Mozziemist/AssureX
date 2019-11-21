package com.example.assurex;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.RawDataItem;
import com.example.assurex.model.TripSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.assurex.App.BT_CHANNEL_ID;
import static com.example.assurex.App.RD_CHANNEL_ID;

public class RawDataCollectionService extends Service implements LocationListener {
    private final static String TAG = "RawDataCollectService";
    private AppDatabase db;
    CarDataReceiver receiver;
    boolean isEngineOn = false;


    //for the RawDateItem
    int rawSpeed;
    double rawAcceleration;
    double tripTime;

    //for the tripSummary
    String engineTroubleCodes;
    String notableTripEvents;
    int accelOverSeven = 0;
    double tAverageSpeed = 0;
    double tTopSpeed = 0;
    double tAverageAcceleration = 0;
    double tTopAcceleration = 0;
    boolean tripSummaryShouldBeSaved = false;

    LocationManager locationManager;
    //LocationListener locationListener;
    double myLatitude;
    double myLongitude;
    String myAddress;
    String myOriginAddress;
    String myDestinationAddress;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        receiver = new CarDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));
        Log.d(TAG, "receiver registered");
        getLocation();

        Intent notificationIntent = new Intent(this, Speed.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, RD_CHANNEL_ID)
                .setContentTitle("Raw Data Collection Service")
                .setContentText("running...")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(2, notification);
        Log.d(TAG, "Started Foreground notification");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        db = AppDatabase.getInstance(this);
        Log.d(TAG, "AppDB instance gotten");
        RawDataCollectionThread mRDThread = new RawDataCollectionThread();
        mRDThread.start();

        return START_STICKY;
    }

    class RawDataCollectionThread extends Thread {

        @Override
        public void run() {
            //while engine is not on but bluetooth service is running
            while(!isEngineOn && isServiceRunning(BluetoothService.class)){
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
            //while engine is on, bt service is running and the speed is still 0 indicating vehicle
            //has yet to move
            myOriginAddress = myAddress;
            while(isEngineOn && isServiceRunning(BluetoothService.class) && rawSpeed == 0){
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            }

            List<RawDataItem> tempRawDataItemList = new ArrayList<RawDataItem>();

            //now start the collection of data if the bt service is on and the engine is on
            if(isServiceRunning(BluetoothService.class) && isEngineOn) {
                int tripNumber;
                Calendar calendar = Calendar.getInstance();
                String tsDate = calendar.get(Calendar.MONTH) + 1 + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);
                List<TripSummary> tempTripSummaryList = db.tripSummaryDao().getAllByDate(tsDate);
                TripSummary tempTripSummary;
                if(tempTripSummaryList != null && !tempTripSummaryList.isEmpty()){
                    tempTripSummary = tempTripSummaryList.get(tempTripSummaryList.size()-1);
                    tripNumber = tempTripSummary.getTripNumber() + 1;
                }else{
                    tripNumber = 1;
                }
                String tripId = tsDate + "#" + tripNumber;

                //while (!Thread.currentThread().isInterrupted() && isEngineOn && isServiceRunning(BluetoothService.class)) {
                do{
                    for(int i = 0; i < 5; i++){
                        if(!isServiceRunning(BluetoothService.class) || !isEngineOn){
                            i = 5;
                        }
                        calendar = Calendar.getInstance();
                        String date = calendar.get(Calendar.MONTH) + 1 + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);
                        String timeStamp = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
                        String tripDatedTimeStamp = date + "@" + timeStamp;
                        rawAcceleration = Math.floor(rawAcceleration * 1000) / 1000.0;
                        tempRawDataItemList.add(new RawDataItem(tripDatedTimeStamp, tripId, date, timeStamp, rawSpeed, rawAcceleration, myLatitude, myLongitude, myAddress));
                        tAverageSpeed = (tAverageSpeed + rawSpeed) / 2;
                        tAverageSpeed = Math.floor(tAverageSpeed * 1000) / 1000.0;
                        if(rawSpeed > tTopSpeed){
                            tTopSpeed = rawSpeed;
                        }

                        tAverageAcceleration = (tAverageAcceleration + Math.abs(rawAcceleration)) / 2;
                        tAverageAcceleration = Math.floor(tAverageAcceleration * 1000) / 1000.0;
                        if(Math.abs(rawAcceleration) > tTopAcceleration){
                            tTopAcceleration = Math.abs(rawAcceleration);
                        }

                        if(Math.abs(rawAcceleration) > 7){
                            accelOverSeven++;
                        }
                        tripSummaryShouldBeSaved = true;

                        Bundle b = new Bundle();
                        //b.putDouble("averagespeed", tAverageSpeed);
                        b.putDouble("topspeed", tTopSpeed);
                        //b.putDouble("averageaccel", tAverageAcceleration);
                        b.putDouble("topaccel", tTopAcceleration);
                        b.putString("engineTroubleCodes",engineTroubleCodes);
                        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                    if(!tempRawDataItemList.isEmpty()) {
                        db.rawDataItemDao().insertAll(tempRawDataItemList);
                        Log.i(TAG, "raw data inserted into sqlite");
                        tempRawDataItemList.clear();
                    }
                }while (!Thread.currentThread().isInterrupted() && isEngineOn && isServiceRunning(BluetoothService.class));

                myDestinationAddress = myAddress;

                if(tripSummaryShouldBeSaved){
                    notableTripEvents = "Times Abs Accel Exceeded 7mph/s: " + accelOverSeven;
                    tempTripSummary = new TripSummary(tripId, tsDate, tripNumber, notableTripEvents,
                            engineTroubleCodes, tAverageSpeed, tTopSpeed,
                            tAverageAcceleration, tTopAcceleration, myOriginAddress, myDestinationAddress);
                    db.tripSummaryDao().insert(tempTripSummary);
                }
            }
            stopSelf();
        }
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (("CarDataUpdates").equals(intent.getAction())) {
                Bundle b = intent.getBundleExtra("CarData");
                rawSpeed = b.getInt("speed", 0);
                rawAcceleration = (double) b.getFloat("acceleration", 0);
                tripTime = b.getDouble("tripTime", 0);
                isEngineOn = b.getBoolean("isEngineOn", false);
                engineTroubleCodes = b.getString("troubleCodes", "No Data Available");
            }
        }
    }

    private void sendMessageToActivity(Bundle b){
        Intent sendDataCollectedInfo = new Intent("DataCollectedInfo");
        sendDataCollectedInfo.putExtra("DataCollected", b);
        sendBroadcast(sendDataCollectedInfo);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        myAddress = hereLocation(myLatitude, myLongitude);

    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    private String hereLocation(double lat, double lon) {
        String name = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            //name = addresses.get(0).getAddressLine(0);

            if (addresses.size() > 0){
                for (int i  = 0; i<10;i++) {
                    if (addresses.get(i).getAddressLine(0) != null && addresses.get(i).getAddressLine(0).length()>0){
                        name = addresses.get(i).getAddressLine(0);
                        break;
                    }
                }
            }


        } catch (IOException e) {
            //Toast.makeText(this, "Location Not Found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            name = "Location Not Found";
            Log.i(TAG, "Location Not Found");
        }

        return name;
    }//end herelocation

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        locationManager.removeUpdates(this);
        AppDatabase.destroyInstance();
    }

    //must be implemented
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
