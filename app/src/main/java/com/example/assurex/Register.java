package com.example.assurex;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assurex.database.AppDatabase;

import org.greenrobot.eventbus.EventBus;
public class Register extends AppCompatActivity{
    EditText userInput;
    EditText passInput;
    EditText secPass;
    EditText emailInput;
    String newUser;
    String newEmail;
    String newPass;
    String rePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Spinner spinner = findViewById(R.id.insurSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.insurArray, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        emailInput = findViewById(R.id.newEmailText);
        passInput = findViewById(R.id.newPassText);
        secPass = findViewById(R.id.rePassText);
        userInput = findViewById(R.id.newUserText);
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void RegisterClicked(View view) {
        newEmail = emailInput.getText().toString();
        newPass = passInput.getText().toString();
        rePass = secPass.getText().toString();
        newUser = userInput.getText().toString();

        boolean userPass = userCheck(newUser);
        boolean emailPass = emailCheck(newEmail);
        boolean passwordPass = passCheck(newPass, rePass);

        if(userPass && emailPass && passwordPass) {
            UserRepository register = new UserRepository(getApplicationContext());
            register.insertUser(newUser, newPass, newEmail);

            startActivity(new Intent(getApplicationContext(), Speed.class));
            //finish();
        }
    }

    public boolean emailCheck(String email) {
        if(email.isEmpty()) {
            emailInput.setError("Please enter an Email address");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid Email address");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean userCheck(String user) {
        if(user.isEmpty()) {
            userInput.setError("Please enter a username");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean passCheck(String pass, String sec) {
        if(pass.isEmpty()) {
            passInput.setError("Please enter a password");
            return false;
        }
        else if(!pass.equals(sec)) {
            secPass.setError("Passwords do not match");
            return false;
        }
        else {
            return true;
        }
    }
}
