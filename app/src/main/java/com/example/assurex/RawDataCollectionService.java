package com.example.assurex;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.RawDataItem;

import java.util.Calendar;
import java.util.TimeZone;

import static com.example.assurex.App.BT_CHANNEL_ID;
import static com.example.assurex.App.RD_CHANNEL_ID;

public class RawDataCollectionService extends Service {
    private final static String TAG = "RawDataCollectService";
    private AppDatabase db;
    CarDataReceiver receiver;
    double rawSpeed;

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
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Calendar calendar = Calendar.getInstance();
                    String date = calendar.get(Calendar.MONTH) + 1 + "-" +
                            calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                            calendar.get(Calendar.YEAR);
                    String timeStamp = calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                            calendar.get(Calendar.MINUTE) + ":" +
                            calendar.get(Calendar.SECOND);
                    String tripId = date + "@" + timeStamp;
                    RawDataItem tempRawDataItem = new RawDataItem(tripId, date, timeStamp, rawSpeed, 0);
                    db.rawDataItemDao().insert(tempRawDataItem);
                    Log.i(TAG, "raw data inserted into sqlite");
                    Thread.sleep(5000);
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            stopSelf();
        }
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (("CarDataUpdates").equals(intent.getAction())) {
                rawSpeed = (double) intent.getIntExtra("value", 0);
            }
        }
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
