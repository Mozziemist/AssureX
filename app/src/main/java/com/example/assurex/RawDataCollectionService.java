package com.example.assurex;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.RawDataItem;
import com.example.assurex.model.TripSummary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.example.assurex.App.BT_CHANNEL_ID;
import static com.example.assurex.App.RD_CHANNEL_ID;

public class RawDataCollectionService extends Service {
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
    int accelOverFour = 0;
    double tAverageSpeed = 0;
    double tTopSpeed = 0;
    double tAverageAcceleration = 0;
    double tTopAcceleration = 0;
    boolean tripSummaryShouldBeSaved = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        receiver = new CarDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));
        Log.d(TAG, "receiver registered");

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
                        tempRawDataItemList.add(new RawDataItem(tripDatedTimeStamp, tripId, date, timeStamp, rawSpeed, rawAcceleration));
                        tAverageSpeed = (tAverageSpeed + rawSpeed) / 2;
                        if(rawSpeed > tTopSpeed){
                            tTopSpeed = rawSpeed;
                        }

                        tAverageAcceleration = (tAverageAcceleration + Math.abs(rawAcceleration)) / 2;
                        if(Math.abs(rawAcceleration) > tTopAcceleration){
                            tTopAcceleration = Math.abs(rawAcceleration);
                        }

                        if(Math.abs(rawAcceleration) > 4){
                            accelOverFour++;
                        }
                        tripSummaryShouldBeSaved = true;

                        Bundle b = new Bundle();
                        b.putDouble("averagespeed", tAverageSpeed);
                        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                    if(!tempRawDataItemList.isEmpty()) {
                        db.rawDataItemDao().insertAll(tempRawDataItemList);
                        Log.i(TAG, "raw data inserted into sqlite");
                        tempRawDataItemList.clear();
                    }
                }while (!Thread.currentThread().isInterrupted() && isEngineOn && isServiceRunning(BluetoothService.class));

                if(tripSummaryShouldBeSaved){
                    notableTripEvents = "Times Abs Accel Exceeded 4mph/s: " + accelOverFour;
                    tempTripSummary = new TripSummary(tripId, tsDate, tripNumber, notableTripEvents,
                            engineTroubleCodes, tAverageSpeed, tTopSpeed,
                            tAverageAcceleration, tTopAcceleration);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        AppDatabase.destroyInstance();
    }

    //must be implemented
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
