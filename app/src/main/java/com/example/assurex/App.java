package com.example.assurex;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String BT_CHANNEL_ID = "BluetoothServiceChannel";
    public static final String RD_CHANNEL_ID = "RawDataServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createBTNotificationChannel();
        createRDNotificationChannel();
    }

    private void createBTNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(
                    BT_CHANNEL_ID,
                    "Bluetooth Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void createRDNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(
                    RD_CHANNEL_ID,
                    "Raw Data Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

