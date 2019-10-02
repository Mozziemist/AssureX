package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Speed extends AppCompatActivity implements SensorEventListener {


    private TextView speed;
    private SensorManager snsMngr;
    private Sensor accel;
    BluetoothAdapter myBtAdapter;
    BluetoothDevice myDevice = null;
    Set<BluetoothDevice> pairedDevices;
    Boolean connected = false;
    BluetoothThread myBtThread;
    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        //sensor accelerometer
        snsMngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = snsMngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        snsMngr.registerListener( this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        speed = findViewById(R.id.speed);


        myBtAdapter = BluetoothAdapter.getDefaultAdapter();

    }//end oncreate




            @Override
            protected void onStart() {
                super.onStart();

                if (myDevice == null) {
                    pairedDevices = myBtAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getName().equals("OBDII")) {
                                myDevice = device;
                                Toast.makeText(getApplicationContext(), "Target device found", Toast.LENGTH_LONG).show();
                                // todo: save device address somewhere for next app use
                            }
                        }
                        if (myDevice == null) {
                            Toast.makeText(getApplicationContext(), "Target device not found", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No devices paired", Toast.LENGTH_LONG).show();
                    }
                }//end if mydevice

                if (myDevice != null && !connected) {
                    // try to connect
                    myBtThread = new BluetoothThread();
                    myBtThread.start();

                }

            }//end onstart




            public void toTesting(View view) {
                startActivity(new Intent(getApplicationContext(), Testing.class));
            }

            public void toPackage(View view) {
                startActivity(new Intent(getApplicationContext(), Package.class));
            }

            public void toInfoPage(View view) {
                startActivity(new Intent(getApplicationContext(), infoPage.class));
            }


    private class BluetoothThread extends Thread {
                BluetoothSocket mySocket;

                //constructor
                BluetoothThread() {
                    try {
                        mySocket = myDevice.createRfcommSocketToServiceRecord(myUUID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public void run ()
                {
                    try {
                        mySocket.connect();
                        connected = true;
                        Speed.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Speed.this.getApplicationContext(), "Connection successful", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Speed.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Speed.this.getApplicationContext(), "Connection Unsuccessful", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }//end run

            }//end Bluetooth thread class





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


}//end class speed
