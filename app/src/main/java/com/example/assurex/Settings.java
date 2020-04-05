package com.example.assurex;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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

        loadSettings();
    }//end on create

    private void loadSettings() {
        FileInputStream fis = null;

        try {
            fis = openFileInput("assurexSettings.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null)
            {
                if (text.equals("isScreenAlwaysOn=true"))
                {
                    alwaysOnCheckBox.setChecked(true);
                }
                else if (text.equals("isScreenAlwaysOn=false"))
                {
                    alwaysOnCheckBox.setChecked(false);
                }
                else if (text.equals("isDarkmode=true"))
                {
                    settingsBut.setChecked(true);
                }
                else if (text.equals("isDarkmode=false"))
                {
                    settingsBut.setChecked(false);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
            {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
            /*case R.id.profilePic: {
                //Toast.makeText(this, "Insert Picture Selector Here", Toast.LENGTH_SHORT).show();
                break;
            }

             */
            case R.id.profileUser: {
                //Toast.makeText(this, "profileUser selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Package.class));
                break;
            }
            case R.id.home: {
                //Toast.makeText(this, "home selected", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), Speed.class));
                NavUtils.navigateUpFromSameTask(this);
                break;
            }
            case R.id.infoPage: {
                //Toast.makeText(this, "infoPage selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), infoPage.class));
                break;
            }
            case R.id.connect: {
                //Toast.makeText(this, "connect selected", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(this, BluetoothService.class);
                startService(serviceIntent);

                Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
                startService(rawDataIntent);
                break;
            }
            case R.id.settings: {
                //Toast.makeText(this, "settings selected", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            }
            case R.id.signOut: {
                //Toast.makeText(this, "signOut selected", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected
    //end for menu --------

    public void darkMode(View view) {
        /*if (Speed.getDarkMode() == false && !Speed.getIsEngineOn()) {
            //settingsBut.setChecked(true);
            //Toast.makeText(this, "Dark Mode Activated", Toast.LENGTH_SHORT).show();
            Speed.setDarkMode(true);
            setTheme(R.style.DarkTheme);
            Speed.themeChanged = true;
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if (Speed.getDarkMode() == true && !Speed.getIsEngineOn()) {
            //settingsBut.setChecked(false);
            //Toast.makeText(this, "Dark Mode Deactivated", Toast.LENGTH_SHORT).show();
            Speed.setDarkMode(false);
            setTheme(R.style.AppTheme);
            Speed.themeChanged = true;
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
        }
        else {
            settingsBut.setChecked(false);
            //Toast.makeText(this, "Must wait till engine is off", Toast.LENGTH_SHORT).show();
        }

         */
        FileOutputStream fos = null;
        Intent intent = new Intent(this, Settings.class);

        try {
            fos = openFileOutput("assurexSettings.txt", MODE_PRIVATE);

            if (settingsBut.isChecked())
            {
                if (alwaysOnCheckBox.isChecked())
                {
                    fos.write("isScreenAlwaysOn=true\nisDarkmode=true".getBytes());
                }
                else
                {
                    fos.write("isScreenAlwaysOn=false\nisDarkmode=true".getBytes());
                }


                //settingsBut.setChecked(true);
                Speed.setDarkMode(true);
                setTheme(R.style.DarkTheme);
                Speed.themeChanged = true;


            }
            else
            {
                if (alwaysOnCheckBox.isChecked())
                {
                    fos.write("isScreenAlwaysOn=true\nisDarkmode=false".getBytes());
                }
                else
                    fos.write("isScreenAlwaysOn=false\nisDarkmode=false".getBytes());

                //settingsBut.setChecked(false);
                Speed.setDarkMode(false);
                setTheme(R.style.AppTheme);
                Speed.themeChanged = true;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        startActivity(intent);
        finish();

    }

    public void alwaysOnClicked(View view) {

        FileOutputStream fos = null;

        try {
            fos = openFileOutput("assurexSettings.txt", MODE_PRIVATE);

            if (alwaysOnCheckBox.isChecked())
            {
                //Toast.makeText(this, "Screen won't turn off", Toast.LENGTH_SHORT).show();
                if (settingsBut.isChecked())
                {
                    fos.write("isScreenAlwaysOn=true\nisDarkmode=true".getBytes());
                }
                else
                    fos.write("isScreenAlwaysOn=true\nisDarkmode=false".getBytes());

                //Speed.setDarkModeSpeed();
            }
            else
            {
                //Toast.makeText(this, "Screen can now turn off", Toast.LENGTH_SHORT).show();
                if (settingsBut.isChecked())
                {
                    fos.write("isScreenAlwaysOn=false\nisDarkmode=true".getBytes());
                }
                else
                    fos.write("isScreenAlwaysOn=false\nisDarkmode=false".getBytes());

                //Speed.setDarkModeSpeed();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
