package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;

public class Speed extends AppCompatActivity implements SensorEventListener {

    private Button butTesting;
    private Button butMyPlan;
    private TextView speed;
    private SensorManager snsMngr;
    private Sensor accel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        //sensor accelerometer
        snsMngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = snsMngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        snsMngr.registerListener( this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        speed = findViewById(R.id.speed);


        butTesting = findViewById(R.id.testing);
        butTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Testing.class));
            }
        });

        butMyPlan = findViewById(R.id.myPlan);
        butMyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Package.class));
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Accelerometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float accX = event.values[0];
            float accY = event.values[1];
            float accZ = event.values[2];

            String myText;

            if((((int)((Math.abs(accX)+Math.abs(accY)+Math.abs(accZ))/3))-3)>0) myText = Float.toString((int)(((Math.abs(accX)+Math.abs(accY)+Math.abs(accZ))/3)-3));
            else  myText = "0";

            speed.setText(myText);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //place holder
    }
}
