package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Speed extends AppCompatActivity {

    private Button testing;
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


        testing = findViewById(R.id.testing);
        myBtAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (myDevice == null)
        {
            pairedDevices = myBtAdapter.getBondedDevices();
            if (pairedDevices.size() > 0)
            {
                for (BluetoothDevice device : pairedDevices)
                {
                    if (device.getName().equals("OBDII"))
                    {
                        myDevice = device;
                        Toast.makeText(getApplicationContext(), "Target device found", Toast.LENGTH_LONG).show();
                        // todo: save device address somewhere for next app use
                    }
                }
                if (myDevice == null)
                {
                    Toast.makeText(getApplicationContext(), "Target device not found", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No devices paired", Toast.LENGTH_LONG).show();
            }
        }

        if (myDevice != null && !connected)
        {
            // try to connect
            myBtThread = new BluetoothThread();
            myBtThread.start();

        }



    }


    public void nextPage(View view) {
        startActivity(new Intent(getApplicationContext(), Testing.class));
    }


    private class BluetoothThread extends Thread
    {
        BluetoothSocket mySocket;

        BluetoothThread()
        {
            try {
                mySocket = myDevice.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                mySocket.connect();
                connected = true;
                Speed.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(Speed.this.getApplicationContext(), "Connection successful", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Speed.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(Speed.this.getApplicationContext(), "Connection Unsuccessful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }



}
