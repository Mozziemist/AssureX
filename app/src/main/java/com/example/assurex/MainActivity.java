package com.example.assurex;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assurex.database.AppDatabase;

import org.greenrobot.eventbus.EventBus;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        if (username.getText().toString().equals("guest") &&
                password.getText().toString().equals("guest")) {
            //startActivity(new Intent(getApplicationContext(), Speed.class));
            Intent intent = new Intent(getApplicationContext(), Speed.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    public void SignUpClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }
}
