package com.example.assurex;

import androidx.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class Speed extends AppCompatActivity {
    private static final String TAG = "Speed";
    private TextView speed;
    private TextView acceleration;
    private Button connectBtn;
    CarDataReceiver receiver;
    BtnStateReceiver BtnReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        speed = findViewById(R.id.speed);
        acceleration = findViewById(R.id.acceleration);
        connectBtn = findViewById(R.id.connectButton);

        receiver = new CarDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));
        BtnReceiver = new BtnStateReceiver();
        registerReceiver(BtnReceiver, new IntentFilter("BtnStateUpdate"));

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);

        //Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        //startService(rawDataIntent);

    }//end oncreate


    @Override
    protected void onStart() {
        super.onStart();
    }//end onstart


    // connects to service
    public void connectBtnClick(View view) {
        if (connectBtn.getText() == "Connect")
        {
            Intent serviceIntent = new Intent(this, BluetoothService.class);
            startService(serviceIntent);

            //Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
            //startService(rawDataIntent);
        }
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
        unregisterReceiver(BtnReceiver);
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        stopService(serviceIntent);

        //Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        //stopService(rawDataIntent);
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (("CarDataUpdates").equals(intent.getAction())) {

                Bundle b = intent.getBundleExtra("CarData");
                int spd = b.getInt("speed", 0);
                float accel = b.getFloat("acceleration", 0);

                speed.setText(Integer.toString(spd));
                acceleration.setText(Float.toString(accel));

                Log.d(TAG, "onReceive: text has been set");

            }
        }
    }

    class BtnStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (("BtnStateUpdate").equals(intent.getAction())) {
                if (intent.getBooleanExtra("value", false)) {
                    connectBtn.setTextSize(20);
                    connectBtn.setText("Connected");
                }
                else
                {
                    connectBtn.setTextSize(21);
                    connectBtn.setText("Connect");
                }
            }
        }
    }//end btnstatereciever

    //for menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profilePic: {
                Toast.makeText(this, "Insert Picture Selector Here", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.profileUser: {
                Toast.makeText(this, "profileUser selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Package.class));
                break;
            }
            case R.id.home: {
                Toast.makeText(this, "home selected", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), Speed.class));
                break;
            }
            case R.id.infoPage: {
                Toast.makeText(this, "infoPage selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), infoPage.class));
                break;
            }
            case R.id.signOut: {
                Toast.makeText(this, "signOut selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected
    //end for menu --------
}//end class speed