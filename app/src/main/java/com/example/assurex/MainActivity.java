package com.example.assurex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.User;

import org.greenrobot.eventbus.EventBus;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_main);

        //initialize
        username = findViewById(R.id.userText);
        password = findViewById(R.id.passText);

        //for location permissions
        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        Toast.makeText(this, "accessing fine location", Toast.LENGTH_SHORT).show();

        if (permissionAccessCoarseLocationApproved) {
            Toast.makeText(this, "accessing background location", Toast.LENGTH_SHORT).show();
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;
            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
                Toast.makeText(this, "can access background location", Toast.LENGTH_SHORT).show();
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                Toast.makeText(this, "cannot access background location", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        1);
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    2);
        }
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void LoginClicked(View view) {
        String name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        Speed.setUsername(name);
        UserRepository userRepository = new UserRepository(getApplicationContext());
        User[] user = new User[1];

        Log.d(TAG, "LoginClicked: User: "+name+"\npass: "+pass);

        new Thread(() -> {
            try {
                try {
                    user[0] = userRepository.getUser(name);
                }catch (Exception e) {
                    Log.d("Invalid", "No such user is registered");
                }

                //=====================DEBUG BYPASS================================
                if(name.equals("debug") && pass.equals("bypass")){
                    Intent intent = new Intent(getApplicationContext(), Speed.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else
                //=====================END DEBUG BYPASS=============================
                if (name.equals(user[0].getUsername()) && pass.equals(user[0].getPassword())) {
                    Intent intent = new Intent(getApplicationContext(), Speed.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("Invalid", "Invalid Username and Password");
                }
            } catch(Exception e) {
                Log.d("Invalid", "No such user is registered");
            }
        }).start();

        /*Intent intent = new Intent(getApplicationContext(), Speed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    public void SignUpClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }
}
