package com.example.assurex;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    public boolean darkMode = false;
    CheckBox settingsBut;
    LinearLayout settingsPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsBut = findViewById(R.id.settingsBut);
        settingsPage = findViewById(R.id.settingsPage);

        if (darkMode == false) settingsBut.setChecked(false);
        if (darkMode == true) settingsBut.setChecked(true);
    }//end on create

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
                NavUtils.navigateUpFromSameTask(this);
                break;
            }
            case R.id.infoPage: {
                Toast.makeText(this, "infoPage selected", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), infoPage.class));
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
            case R.id.settings: {
                Toast.makeText(this, "settings selected", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), Settings.class));
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

    public void darkMode(View view) {
        if (darkMode == false) {
            //settingsBut.setChecked(true);
            Toast.makeText(this, "Dark Mode Selected", Toast.LENGTH_SHORT).show();
            darkMode = true;
            settingsPage.setBackgroundResource(R.drawable.gradient_background_dark);


            //the actual way to do this involves changing the theme, look into that later


        }
        else if (darkMode == true) {
            //settingsBut.setChecked(false);
            Toast.makeText(this, "Dark Mode Deactivated", Toast.LENGTH_SHORT).show();
            darkMode = false;
            settingsPage.setBackgroundResource(R.drawable.gradient_background);
        }
    }

    public void alwaysOnClicked(View view) {

    }
}
