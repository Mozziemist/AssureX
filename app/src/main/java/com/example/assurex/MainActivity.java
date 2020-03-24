package com.example.assurex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.assurex.database.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText email;
    private EditText password;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for dark mode
        if (Speed.getDarkMode() == false) {
            //settingsBut.setChecked(true);
            //Toast.makeText(this, "Light Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.AppTheme);
        } else if (Speed.getDarkMode() == true) {
            //settingsBut.setChecked(false);
            //Toast.makeText(this, "Dark Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.DarkTheme);
        }
        //end for dark mode
        setContentView(R.layout.activity_main);

        //initialize
        email = findViewById(R.id.userText);
        password = findViewById(R.id.passText);

        //for location permissions
        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessCoarseLocationApproved) {
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this, "background location was approved?: " + backgroundLocationPermissionApproved, Toast.LENGTH_SHORT).show();
            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
                Toast.makeText(this, "can access background location", Toast.LENGTH_SHORT).show();
                fAuth = FirebaseAuth.getInstance();

                if (fAuth.getCurrentUser() != null) {
                    Speed.setUsername(fAuth.getCurrentUser().getEmail());
                    startActivity(new Intent(getApplicationContext(), Speed.class));
                    finish();
                }
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                Toast.makeText(this, "cannot access background location", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        1);
                fAuth = FirebaseAuth.getInstance();

                if (fAuth.getCurrentUser() != null) {
                    Speed.setUsername(fAuth.getCurrentUser().getEmail());
                    startActivity(new Intent(getApplicationContext(), Speed.class));
                    finish();
                }
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    2);
            fAuth = FirebaseAuth.getInstance();

            if (fAuth.getCurrentUser() != null) {
                Speed.setUsername(fAuth.getCurrentUser().getEmail());
                startActivity(new Intent(getApplicationContext(), Speed.class));
                finish();
            }
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
        String name = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        Speed.setUsername(name);

        Log.d(TAG, "LoginClicked: User: "+name+"\npass: "+pass);

        //=====================DEBUG BYPASS================================
        if (name.equals("debug") && pass.equals("bypass")) {
            Intent intent = new Intent(getApplicationContext(), Speed.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        boolean emailPass = emailCheck(name);
        boolean passwordPass = passCheck(pass);

        if(emailPass && passwordPass) {
            fAuth.signInWithEmailAndPassword(name, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), Speed.class));
                    finish();
                }
                else {
                    Toast.makeText(MainActivity.this, "Error! " +
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean emailCheck(String em) {
        if(em.isEmpty()) {
            email.setError("Please enter an Email address");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            email.setError("Invalid Email address");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean passCheck(String pw) {
        if(pw.isEmpty()) {
            password.setError("Please enter a password");
            return false;
        }
        else if(pw.length() < 6) {
            password.setError("Password must be at least 6 characters long");
            return false;
        }
        else {
            return true;
        }
    }

    public void SignUpClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }
}