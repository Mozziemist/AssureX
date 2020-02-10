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
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.RawDataItem;
import com.example.assurex.model.TripSummary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.assurex.App.RD_CHANNEL_ID;

public class RawDataCollectionService extends Service {
    //this boolean makes it so data collection runs without the car first having moved
    boolean isDebugging = true;
    private final static String TAG = "RawDataCollectService";
    //private AppDatabase db;
    FirebaseFirestore db;
    CarDataReceiver receiver;
    SpeedDataReceiver spdreceiver;
    boolean isEngineOn = false;
    boolean shouldEndService = false;


    //for the RawDateItem
    int rawSpeed;
    double rawAcceleration;
    double tripTime;
    int speedLimit = -1;

    //for the tripSummary
    String engineTroubleCodes;
    int accelOverEight = 0;
    int decelOverEight = 0;
    int secondsSpentOverTenMPH = 0;
    double tAverageSpeed = 0;
    double tTopSpeed = 0;
    double tAverageAcceleration = 0;
    double tAverageDeceleration = 0;
    double tTopAcceleration = 0;
    double tTopDeceleration = 0;
    boolean tripSummaryShouldBeSaved = false;

    //for location related calculations
    double myLatitude;
    double myLongitude;
    String myAddress = "";
    String myOriginAddress = "";
    String myDestinationAddress = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        receiver = new CarDataReceiver();
        spdreceiver = new SpeedDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));
        registerReceiver(spdreceiver, new IntentFilter("MiscDataFromSpeedjava"));

        Log.d(TAG, "receiver registered");
        //getLocation();

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
        //db = AppDatabase.getInstance(this);
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "AppDB instance gotten");

        RawDataCollectionThread mRDThread = new RawDataCollectionThread();
        mRDThread.start();

        return START_STICKY;
    }

    class RawDataCollectionThread extends Thread {

        @Override
        public void run() {
            while (!shouldEndService) {
                //while engine is not on but bluetooth service is running
                while(!isEngineOn && isServiceRunning(BluetoothService.class)){
                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                //while engine is on, bt service is running and the speed is still 0 indicating vehicle
                //has yet to move
                while(isEngineOn && !isDebugging && isServiceRunning(BluetoothService.class) && speedLimit <= 0 && rawSpeed == 0){
                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                }

                myOriginAddress = hereLocation(myLatitude, myLongitude);

                if(myOriginAddress.equals("")){
                    myOriginAddress = "Unable to determine origin address";
                }

                while(isEngineOn && !isDebugging &&  isServiceRunning(BluetoothService.class) && rawSpeed == 0){
                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                }

                //now start the collection of data if the bt service is on and the engine is on
                if(isServiceRunning(BluetoothService.class) && isEngineOn) {

                    //====GET TRIP NUMBER====
                    int tripNumber;
                    Calendar calendar = Calendar.getInstance();
                    String tsDate = calendar.get(Calendar.MONTH) + 1 + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);
                    List<Object> tempTripSummaryList = new ArrayList<>();
                    RawDataItem tempRawDataItem;

                    //===CONTACTING FIREBASE FOR TRIP SUMMARIES===
                    db.collection("users")
                    .document("debug_user")
                    .collection("tripsummaries")
                    .whereEqualTo("date", tsDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    try {
                                        //tempTripSummaryList.add((TripSummary) document.getData());
                                        //tempTripSummaryList.add(document.getData());
                                    }catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                    //===END OF CONTACTING FIREBASE===

                    TripSummary tempTripSummary;

                    if(!tempTripSummaryList.isEmpty() && tempTripSummaryList != null){
                        tempTripSummary = (TripSummary) tempTripSummaryList.get(tempTripSummaryList.size()-1);
                        tripNumber = tempTripSummary.getTripNumber() + 1;
                    }else{
                        tripNumber = 1;
                    }
                    //====END OF GET TRIP NUMBER====

                    String tripId = tsDate + "#" + tripNumber;

                    //by reaching this point, the do loop will complete at least once thus making it
                    //necessary to save the trip summary
                    tripSummaryShouldBeSaved = true;
                    tempTripSummaryList.clear();

                    do {
                        //===GENERATE THE TIMESTAMP===
                        calendar = Calendar.getInstance();
                        String date = calendar.get(Calendar.MONTH) + 1 + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);
                        String timeStamp = "";
                        int hourTemp = calendar.get(Calendar.HOUR_OF_DAY);
                        int minuteTemp = calendar.get(Calendar.MINUTE);
                        int secondTemp = calendar.get(Calendar.SECOND);
                        int milliSecondTemp = calendar.get(Calendar.MILLISECOND);

                        if (hourTemp < 10) {
                            timeStamp = timeStamp + "0" + hourTemp + ":";
                        } else {
                            timeStamp = timeStamp + hourTemp + ":";
                        }

                        if (minuteTemp < 10) {
                            timeStamp = timeStamp + "0" + minuteTemp + ":";
                        } else {
                            timeStamp = timeStamp + minuteTemp + ":";
                        }

                        if (secondTemp < 10) {
                            //timeStamp = timeStamp + "0" + secondTemp + ":";
                            timeStamp = timeStamp + "0" + secondTemp;
                        } else {
                            //timeStamp = timeStamp + secondTemp + ":";
                            timeStamp = timeStamp + secondTemp;
                        }

                        Log.i(TAG, timeStamp + ":" + milliSecondTemp);
                        //====END OF GENERATE TIME STAMP====

                        String tripDatedTimeStamp = tripId + "@" + timeStamp;

                        //CALCULATE RAW ACCELERATION WITHIN 3 DIGITS
                        rawAcceleration = Math.floor(rawAcceleration * 1000) / 1000.0;

                        //DETERMINE NEAREST ADDRESS
                        myAddress = hereLocation(myLatitude, myLongitude);
                        if (myAddress.equals("")) {
                            myAddress = "Unable to determine address";
                        }

                        //GENERATE THE RAW DATA ITEM FOR SAVING INTO DATABASE RAW DATA ENTRIES
                        tempRawDataItem = new RawDataItem(tripDatedTimeStamp, tripId, date, timeStamp, speedLimit, rawSpeed, rawAcceleration, myLatitude, myLongitude, myAddress);

                        //CALCULATES AVG SPEED AND ACCELERATOIN
                        averagesCalculation();

                        //KEEPS TRACK OF WATCHED FOR EVENTS
                        eventsTracker();

                        //SAVES RAW DATE ITEM INTO FIREBASE
                        db.collection("users").document("debug_user").collection("rawdataitems")
                                .document(tripDatedTimeStamp).set(tempRawDataItem);

                        Log.i(TAG, "raw data inserted into firebase");

                        //send received data out to be received by a broadcast in other windows
                        Bundle b = new Bundle();
                        b.putDouble("topspeed", tTopSpeed);
                        b.putDouble("topaccel", tTopAcceleration);
                        b.putString("engineTroubleCodes", engineTroubleCodes);
                        sendMessageToActivity(b);

                        //saving raw data entries every second
                        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                    }while (isEngineOn && isServiceRunning(BluetoothService.class));

                    //===SETS DESTINATION ADDRESS AS LAST NEAREST ADDRESS CHECKED===
                    myDestinationAddress = myAddress;
                    if(myDestinationAddress.equals("")){
                        myDestinationAddress = "Unable to determine destination address";
                    }

                    //===THIS CREATES A TRIP SUMMARY ENTRY IN THE DATABASE===
                    //it relies on a boolean value that should be set to true
                    //given conditions set above
                    if(tripSummaryShouldBeSaved){
                        tripSummaryEntryCreation(tripId, tsDate, tripNumber);
                    }
                }

                //if bt service is no longer running, my service has no purpose in running
                if(!isServiceRunning(BluetoothService.class)){
                    shouldEndService = true;
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

    class SpeedDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (("MiscDataFromSpeedjava").equals(intent.getAction())){
                Bundle b = intent.getBundleExtra("miscData");
                speedLimit = b.getInt("SpeedLimit", 0);
                myLatitude = b.getDouble("Latitude", 0.0);
                myLongitude = b.getDouble("Longitude", 0.0);
                Log.d(TAG, "onReceive: Speed limit set");
            }
        }
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

    private void averagesCalculation(){
        tAverageSpeed = (tAverageSpeed + rawSpeed) / 2;
        tAverageSpeed = Math.floor(tAverageSpeed * 1000) / 1000.0;
        if (rawSpeed > tTopSpeed) {
            tTopSpeed = rawSpeed;
        }

        if(rawAcceleration >= 0){
            tAverageAcceleration = (tAverageAcceleration + rawAcceleration) / 2;
            tAverageAcceleration = Math.floor(tAverageAcceleration * 1000) / 1000.0;
            if (rawAcceleration > tTopAcceleration) {
                tTopAcceleration = rawAcceleration;
            }
        }else {
            tAverageDeceleration = (tAverageDeceleration + rawAcceleration) / 2;
            tAverageDeceleration = Math.ceil(tAverageDeceleration * 1000) / 1000.0;
            if (rawAcceleration < tTopDeceleration){
                tTopDeceleration = rawAcceleration;
            }
        }
    }

    private void eventsTracker(){
        if (Math.abs(rawAcceleration) > 8) {
            if(rawAcceleration > 8){
                accelOverEight++;
            }
            else{
                decelOverEight++;
            }
        }

        if (speedLimit != -1) {
            if (rawSpeed > speedLimit + 10) {
                secondsSpentOverTenMPH++;
            }
        } else {
            Log.d(TAG, "Speed Limit Data Not Available. Unable to check if current speed exceeds speed limit");
        }
    }

    private void tripSummaryEntryCreation(String tripId, String tsDate, int tripNumber){
        String notableTripEvents = "Times Acceleration Exceeded 8 MPH/S: " + accelOverEight + "\n" +
                                   "Times Deceleration Exceeded -8 MPH/S: " + decelOverEight + "\n" +
                                   "Cumulative Time Spent 10 MPH Over Speed Limit: " + secondsSpentOverTenMPH;

        if(engineTroubleCodes.equals("Pending Search")){
            engineTroubleCodes = "No Trouble Codes Reported";
        }

        TripSummary tempTripSummary = new TripSummary(tripId, tsDate, tripNumber, notableTripEvents,
                engineTroubleCodes, tAverageSpeed, tTopSpeed, tAverageAcceleration,tAverageDeceleration,
                tTopAcceleration, tTopDeceleration, myOriginAddress, myDestinationAddress);
        db.collection("users").document("debug_user").collection("tripsummaries")
                .document(tripId).set(tempTripSummary);

        //clear variables that will still contain old info if the service is still running after the end of
        //one trip and before the start of another
        tTopSpeed = 0;
        tTopAcceleration = 0;
        tTopDeceleration = 0;
        tAverageAcceleration = 0;
        tAverageDeceleration = 0;
        accelOverEight = 0;
        decelOverEight = 0;
        secondsSpentOverTenMPH = 0;
        tripSummaryShouldBeSaved = false;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(spdreceiver);
        //locationManager.removeUpdates(this);
        shouldEndService = true;
        //AppDatabase.destroyInstance();
    }

    //must be implemented
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
