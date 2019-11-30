package com.example.assurex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
        UserRepository userRepository = new UserRepository(getApplicationContext());
        User[] user = new User[1];

        new Thread(() -> {
            try {
                user[0] = userRepository.getUser(name);
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
