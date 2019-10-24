package com.example.assurex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assurex.database.AppDatabase;

public class Speed extends AppCompatActivity {
    private static final String TAG = "Speed";
    private TextView speed;
    Intent rawDataCollectionIntent;
    private Button connectBtn;
    private SensorManager snsMngr;
    private Sensor accel;
    CarDataReceiver receiver;
    BtnStateReceiver BtnReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        speed = findViewById(R.id.speed);
        connectBtn = findViewById(R.id.connectButton);

        receiver = new CarDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));
        BtnReceiver = new BtnStateReceiver();
        registerReceiver(BtnReceiver, new IntentFilter("BtnStateUpdate"));

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);

        rawDataCollectionIntent = new Intent(this, RawDataCollectionService.class);
        startService(rawDataCollectionIntent);

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
        stopService(rawDataCollectionIntent);
        AppDatabase.destroyInstance();
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (("CarDataUpdates").equals(intent.getAction())) {

                Bundle b = intent.getBundleExtra("CarData");
                int spd = b.getInt("speed", 0);
                float accel = b.getFloat("acceleration", 0);

                speed.setText(Integer.toString(spd));
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
    }
}//end class speed