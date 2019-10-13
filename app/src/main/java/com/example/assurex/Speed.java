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
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;
import android.widget.Toast;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.RawDataItem;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

public class Speed extends AppCompatActivity /*implements SensorEventListener*/ {
    private static final String TAG = "Speed";
    private TextView speed;
    private SensorManager snsMngr;
    private Sensor accel;
    BluetoothAdapter myBtAdapter;
    BluetoothDevice myDevice = null;
    Set<BluetoothDevice> pairedDevices;
    Boolean connected = false;
    BluetoothThread myBtThread;
    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Handler myHandler;
    private static AppDatabase db;

    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    static double rawSpeed;
    static double rawAcceleration; //based on linear acceleration from device sensors


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        speed = findViewById(R.id.speed);
        myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                // modify UI with speed
                speed.setText(Integer.toString(msg.arg1));
                rawSpeed = (double) msg.arg1;
            }
        };

        db = AppDatabase.getInstance(this);

        executorService.scheduleWithFixedDelay(Speed::rawDataCollection, 0, 10, TimeUnit.SECONDS);
        /*
        //sensor accelerometer
        snsMngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = snsMngr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        snsMngr.registerListener( this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        */

        // get phone's bluetooth adapter
        myBtAdapter = BluetoothAdapter.getDefaultAdapter();

    }//end oncreate


    @Override
    protected void onStart() {
        super.onStart();

        // if bluetooth device hasnt been set yet
        if (myDevice == null) {
            // look in phone's bonded devices
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

        // if bluetooth device is found and its NOT connected
        if (myDevice != null && !connected) {
            // on a new thread, try to connect and do work
            myBtThread = new BluetoothThread();
            myBtThread.start();

        }

    }//end onstart

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();

        super.onDestroy();
    }

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
                        // set the key to obd device to allow connection
                        mySocket = myDevice.createRfcommSocketToServiceRecord(myUUID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public void run ()
                {
                    try {
                        // try to connect and do desired work
                        mySocket.connect();
                        connected = true;
                        Speed.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Speed.this.getApplicationContext(), "Connection successful", Toast.LENGTH_LONG).show();
                            }
                        });

                        // initialize car with obd initialization commands
                        try {
                            new EchoOffCommand().run(mySocket.getInputStream(), mySocket.getOutputStream());

                            new LineFeedOffCommand().run(mySocket.getInputStream(), mySocket.getOutputStream());

                            new TimeoutCommand(100).run(mySocket.getInputStream(), mySocket.getOutputStream());

                            new SelectProtocolCommand(ObdProtocols.AUTO).run(mySocket.getInputStream(), mySocket.getOutputStream());

                            SpeedCommand speedCommand = new SpeedCommand();

                            // loop thread for a constant stream of refreshed data, 3 sec interval
                            while (!Thread.currentThread().isInterrupted())
                            {

                                try {
                                    Message speedMessage = Message.obtain();

                                    speedCommand.run(mySocket.getInputStream(), mySocket.getOutputStream());
                                    speedMessage.arg1 = (int)speedCommand.getImperialUnit();
                                    myHandler.sendMessage(speedMessage);

                                    Speed.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Speed.this.getApplicationContext(), "Speed refreshed ", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Thread.sleep(3000);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                            Speed.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Speed.this.getApplicationContext(), "Exiting While Loop", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



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

    private static void rawDataCollection(){
        Calendar calendar = Calendar.getInstance();
            String date =   calendar.get(Calendar.MONTH)+1 + "-" +
                            calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                            calendar.get(Calendar.YEAR);
            String timeStamp =  calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                                calendar.get(Calendar.MINUTE) + ":" +
                                calendar.get(Calendar.SECOND);
            String tripId = date + "@" + timeStamp;
            RawDataItem tempRawDataItem = new RawDataItem(tripId, date, timeStamp, rawSpeed, rawAcceleration);
            db.rawDataItemDao().insert(tempRawDataItem);
            Log.i(TAG, "raw data inserted into sqlite");
    }


    /*
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Accelerometer
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double accX = (double)event.values[0];
            double accY = (double)event.values[1];
            double accZ = (double)event.values[2];

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
    */

}//end class speed
