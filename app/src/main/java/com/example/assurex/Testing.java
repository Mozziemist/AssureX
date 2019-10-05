package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class Testing extends AppCompatActivity {

    private Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);



        back = findViewById(R.id.backToScreen1);


    }



    public void BtSettingsClicked(View view) {
        Intent openBtSettings = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(openBtSettings);

    }

    public void backButton(View view) {
        startActivity(new Intent(getApplicationContext(), Speed.class));
    }
}
