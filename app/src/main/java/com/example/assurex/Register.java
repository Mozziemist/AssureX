package com.example.assurex;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.assurex.database.AppDatabase;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Set;

public class Register extends AppCompatActivity{
    EditText userInput;
    EditText passInput;
    EditText secPass;
    EditText emailInput;
    Button regDevice;
    String newUser;
    String newEmail;
    String newPass;
    String rePass;

    String deviceAddress = "";

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
        regDevice = findViewById(R.id.deviceReg);
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
        boolean devicePass = deviceCheck();


        if(userPass && emailPass && passwordPass && devicePass) {
            UserRepository register = new UserRepository(getApplicationContext());
            // todo: insert deviceAddress along with other info
            register.insertUser(newUser, newPass, newEmail);

            startActivity(new Intent(getApplicationContext(), Speed.class));
            //finish();
        }
    }

    public boolean deviceCheck()
    {
        if (deviceAddress == "")
        {
            regDevice.setError("");
            return false;
        }
        else
        {
            return true;
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

    public void devRegClicked(View view) {
        BluetoothAdapter myBtAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice myBtDevice = null;
        Set<BluetoothDevice> pairedDevices;
        deviceAddress = "";

        // look in phone's bonded devices
        for(int i = 0; i < 2; i++) {
            pairedDevices = myBtAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("OBDII")) {
                        Toast.makeText(this, "found device", Toast.LENGTH_SHORT).show();
                        myBtDevice = device;
                        deviceAddress = myBtDevice.getAddress();
                        regDevice.setError(null);
                        regDevice.setText("Device Found");
                        regDevice.setBackgroundColor(Color.parseColor("green"));
                    }
                }
                if (myBtDevice == null) {
                    Intent openBtSettings = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    openBtSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(openBtSettings);
                }
            } else {
                Intent openBtSettings = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                openBtSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openBtSettings);
            }
            if (myBtDevice != null)
            {
                break;
            }
        }




    }
}
