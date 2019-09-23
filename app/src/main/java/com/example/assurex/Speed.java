package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class Speed extends AppCompatActivity {

    private Button testing;
    BluetoothAdapter myBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        myBtAdapter = BluetoothAdapter.getDefaultAdapter();
        testing = findViewById(R.id.testing);
        testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Testing.class));
            }
        });

    }

    public void BtSettingsClicked(View view) {
        Intent openBtSettings = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(openBtSettings);
    }
}
