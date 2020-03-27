package com.example.assurex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        }
        else if (Speed.getDarkMode() == true) {
            //settingsBut.setChecked(false);
            //Toast.makeText(this, "Dark Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.DarkTheme);
        }
        //end for dark mode
        setContentView(R.layout.activity_main);

        //initialize
        email = findViewById(R.id.userText);
        password = findViewById(R.id.passText);
        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Speed.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
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
                    Intent signInIntent = new Intent(getApplicationContext(), Speed.class);
                    signInIntent.putExtra("isRegistering", false);
                    startActivity(signInIntent);
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