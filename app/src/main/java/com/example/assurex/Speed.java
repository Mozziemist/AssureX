package com.example.assurex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assurex.database.AppDatabase;

public class Speed extends AppCompatActivity {
    private static final String TAG = "Speed";
    private TextView speed;
    private TextView acceleration;
<<<<<<< HEAD
=======
    private TextView tripTime, troubleCodes;
>>>>>>> myDev
    CarDataReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        speed = findViewById(R.id.speed);
        acceleration = findViewById(R.id.acceleration);
<<<<<<< HEAD
=======

        tripTime = findViewById(R.id.tripTime);
        troubleCodes = findViewById(R.id.troubleCodes);
>>>>>>> myDev

        receiver = new CarDataReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);

        Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        startService(rawDataIntent);

    }//end oncreate


    @Override
    protected void onStart() {
        super.onStart();
    }//end onstart


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        stopService(serviceIntent);

        Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        stopService(rawDataIntent);
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (("CarDataUpdates").equals(intent.getAction())) {

                Bundle b = intent.getBundleExtra("CarData");
                int spd = b.getInt("speed", 0);
                float accel = b.getFloat("acceleration", 0);

                troubleCodes.setText(b.getString("troubleCodes", "No Codes"));
                tripTime.setText(Double.toString(b.getDouble("tripTime", 0)));
                speed.setText(Integer.toString(spd));
                acceleration.setText(Integer.toString((int)accel));

                Log.d(TAG, "onReceive: text has been set");

            }
        }
    }

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
                //NavUtils.navigateUpFromSameTask(this);
                break;
            }
            case R.id.infoPage: {
                Toast.makeText(this, "infoPage selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), infoPage.class));
                break;
            }
            case R.id.connect: {
                Toast.makeText(this, "connect selected", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(this, BluetoothService.class);
                startService(serviceIntent);

                Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
                startService(rawDataIntent);
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