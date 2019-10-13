package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;
import android.widget.Toast;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.RawDataItem;

import java.io.IOException;
import java.util.Set;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

public class Speed extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "Speed";
    private TextView speed;
    private SensorManager snsMngr;
    private Sensor accel;
    CarDataReceiver receiver;
    private static AppDatabase db;

    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    static double rawSpeed;
    static double rawAcceleration; //based on linear acceleration from device sensors


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        speed = findViewById(R.id.speed);

        receiver = new CarDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));


        db = AppDatabase.getInstance(this);

        executorService.scheduleWithFixedDelay(Speed::rawDataCollection, 0, 10, TimeUnit.SECONDS);

        //sensor accelerometer
        snsMngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = snsMngr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        snsMngr.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);


    }//end oncreate


    @Override
    protected void onStart() {
        super.onStart();


    }//end onstart


    // connects to service
    public void connectBtnClick(View view) {
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);
    }

    public void toPackage(View view) {
        startActivity(new Intent(getApplicationContext(), Package.class));
    }

    public void toInfoPage(View view) {
        startActivity(new Intent(getApplicationContext(), infoPage.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        stopService(serviceIntent);

        AppDatabase.destroyInstance();
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (("CarDataUpdates").equals(intent.getAction())) {
                Log.d(TAG, "onReceive: about to setText");
                speed.setText(Integer.toString(intent.getIntExtra("value", 0)));
                Log.d(TAG, "onReceive: text has been set");

                rawSpeed = (double) intent.getIntExtra("value", 0);
            }
        }
    }

    private static void rawDataCollection() {
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.MONTH) + 1 + "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                calendar.get(Calendar.YEAR);
        String timeStamp = calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE) + ":" +
                calendar.get(Calendar.SECOND);
        String tripId = date + "@" + timeStamp;
        RawDataItem tempRawDataItem = new RawDataItem(tripId, date, timeStamp, rawSpeed, rawAcceleration);
        db.rawDataItemDao().insert(tempRawDataItem);
        Log.i(TAG, "raw data inserted into sqlite");

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //Accelerometer
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double accX = (double) event.values[0];
            double accY = (double) event.values[1];
            double accZ = (double) event.values[2];

            String myText;

            //if ((((int) ((Math.abs(accX) + Math.abs(accY) + Math.abs(accZ)) / 3)) - 3) > 0)
            if (((((Math.abs(accX) + Math.abs(accY) + Math.abs(accZ)) / 3)) - 3) > 0)
                myText = Double.toString((((Math.abs(accX) + Math.abs(accY) + Math.abs(accZ)) / 3) - 3));
            else myText = "0";
            Log.i(TAG, "current acceleration is " + myText);
            rawAcceleration = Double.parseDouble(myText);
        }
    }//end onsensor changed


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //place holder

    }//end onAccuracyChanged

}//end class speed