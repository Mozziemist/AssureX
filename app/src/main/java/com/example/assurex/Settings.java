package com.example.assurex;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

public class Settings extends AppCompatActivity {

    CheckBox settingsBut;
    CheckBox alwaysOnCheckBox;
    LinearLayout settingsPage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for dark mode
        if (Speed.getDarkMode() == false) {
            //settingsBut.setChecked(true);
            //Toast.makeText(this, "Light Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.AppTheme);
        }
        else if (Speed.getDarkMode() == true) {
            //settingsBut.setChecked(false);
            //Toast.makeText(this, "Dark Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.DarkTheme);
        }
        //end for dark mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsBut = findViewById(R.id.settingsBut);
        settingsPage = findViewById(R.id.settingsPage);
        alwaysOnCheckBox = findViewById(R.id.alwaysOnBut);

        if (getIntent().getBooleanExtra("isAlwaysOnSet", true))
        {
            alwaysOnCheckBox.setChecked(true);
        }
        else
            alwaysOnCheckBox.setChecked(false);

        if (Speed.getDarkMode() == false) settingsBut.setChecked(false);
        if (Speed.getDarkMode() == true) settingsBut.setChecked(true);
    }//end on create

    //for menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        menu.findItem(R.id.profileUser).setTitle(Speed.getUsername());
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
        if (Speed.getDarkMode() == false && !Speed.getIsEngineOn()) {
            //settingsBut.setChecked(true);
            Toast.makeText(this, "Dark Mode Activated", Toast.LENGTH_SHORT).show();
            Speed.setDarkMode(true);
            setTheme(R.style.DarkTheme);
            Speed.themeChanged = true;
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if (Speed.getDarkMode() == true && !Speed.getIsEngineOn()) {
            //settingsBut.setChecked(false);
            Toast.makeText(this, "Dark Mode Deactivated", Toast.LENGTH_SHORT).show();
            Speed.setDarkMode(false);
            setTheme(R.style.AppTheme);
            Speed.themeChanged = true;
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
        }
        else {
            settingsBut.setChecked(false);
            Toast.makeText(this, "Must wait till engine is off", Toast.LENGTH_SHORT).show();
        }

    }

    public void alwaysOnClicked(View view) {

        Intent sendSettings = new Intent("SettingsUpdate");

        if (alwaysOnCheckBox.isChecked())
        {
            Toast.makeText(this, "Screen won't turn off", Toast.LENGTH_SHORT).show();
            sendSettings.putExtra("alwaysOn", true);
            sendBroadcast(sendSettings);
            //Speed.setDarkModeSpeed();
        }
        else
        {
            Toast.makeText(this, "Screen can now turn off", Toast.LENGTH_SHORT).show();
            sendSettings.putExtra("alwaysOn", false);
            sendBroadcast(sendSettings);
            //Speed.setDarkModeSpeed();
        }
    }


}
