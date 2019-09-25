package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class Testing extends AppCompatActivity {

    private Button back;
    BluetoothAdapter myBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        myBtAdapter = BluetoothAdapter.getDefaultAdapter();

        back = findViewById(R.id.backToScreen1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Speed.class));
            }
        });

    }

    public void BtSettingsClicked(View view) {
        Intent openBtSettings = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(openBtSettings);
    }
}
