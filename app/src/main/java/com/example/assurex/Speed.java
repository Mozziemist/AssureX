package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;
import android.widget.Toast;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Speed extends AppCompatActivity /*implements SensorEventListener*/ {


    private final static String TAG = "Speed";
    private TextView speed;
    private SensorManager snsMngr;
    private Sensor accel;
    CarDataReceiver receiver;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        speed = findViewById(R.id.speed);

        receiver = new CarDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));

/*
        //sensor accelerometer
        snsMngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = snsMngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        snsMngr.registerListener( this, accel, SensorManager.SENSOR_DELAY_NORMAL);
*/



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
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(("CarDataUpdates").equals(intent.getAction()))
            {
                Log.d(TAG, "onReceive: about to setText");
                speed.setText(Integer.toString(intent.getIntExtra("value", 0)));
                Log.d(TAG, "onReceive: text has been set");
            }
        }

    }



/*
            @Override
            public void onSensorChanged(SensorEvent event) {
                //Accelerometer
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float accX = event.values[0];
                    float accY = event.values[1];
                    float accZ = event.values[2];

                    String myText;

                    if ((((int) ((Math.abs(accX) + Math.abs(accY) + Math.abs(accZ)) / 3)) - 3) > 0)
                        myText = Float.toString((int) (((Math.abs(accX) + Math.abs(accY) + Math.abs(accZ)) / 3) - 3));
                    else myText = "0";

                    speed.setText(myText);
                }
            }//end onsensor changed


            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                //place holder

            }//end onAccuracyChanged
*/

}//end class speed
