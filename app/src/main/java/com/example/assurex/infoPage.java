package com.example.assurex;
//package com.mapbox.mapboxandroiddemo.examples.location;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.database.TripSummaryDao;
import com.example.assurex.model.TripSummary;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//for date
import java.text.SimpleDateFormat;
import java.util.Calendar;

//for map
/*
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
//import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;
*/
//end for map


public class infoPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //average speed variables
    private boolean avSpButTrue = false;
    private Button avSpBut;
    private LinearLayout avSpButWindow;
    private TextView dateSpeed;
    private TextView topSpeed;

    //acceleration variables
    private boolean accButTrue = false;
    private Button accBut;
    private LinearLayout accButWindow;
    private TextView dateAcc;
    private TextView topAcc;

    //engine info variables
    private boolean engInfButTrue = false;
    private Button engInfBut;
    private LinearLayout engInfButWindow;
    private TextView dateEngInf;
    private TextView engStatus;

    //insurance variables
    private boolean insButTrue = false;
    private Button insBut;
    private LinearLayout insButWindow;
    private TextView dateIns;
    private TextView tripSum;

    //calender variables
    private boolean calShowing = false;
    private LinearLayout calenderWindow;
    private TextView calText;
    private CalendarView calender;
    private String date;

    //drop down variables
    private boolean spinShowing = false;
    private LinearLayout spinWindow;
    private Spinner tripSpinner;
    private ArrayAdapter<CharSequence> adapter;

    //location variables
    private boolean locShowing = false;
    private LinearLayout locWindow;
    private TextView locText;
    private String address;
    private LocationManager locationManager;

    //Trip Summary for database integration
    //List<TripSummary> tempTripSummaryList = new ArrayList<TripSummary>();
    List<TripSummary> tempTripSummaryList;
    TripSummary tempTripSummary;
    private AppDatabase db;
    String dbQueryDate;
    String tEngineTroubleCodes;
    double tTopSpeed = 0;
    double tTopAcceleration = 0;
    String displayedEngineTroubleCodes;
    double displayedTopSpeed = 0;
    double displayedTopAcceleration = 0;
    RawDataReceiver rdreceiver;

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
        setContentView(R.layout.activity_info_page);
        db = AppDatabase.getInstance(this);
        rdreceiver = new RawDataReceiver();
        registerReceiver(rdreceiver, new IntentFilter("DataCollectedInfo"));

        //for average speed button
        avSpBut = findViewById(R.id.avSpBut);
        avSpButWindow = findViewById(R.id.avSpWindow);
        dateSpeed = findViewById(R.id.dateSpeed);
        topSpeed = findViewById(R.id.topSpeed);

        //for acceleration button
        accBut = findViewById(R.id.accBut);
        accButWindow = findViewById(R.id.accWindow);
        dateAcc = findViewById(R.id.dateAcc);
        topAcc = findViewById(R.id.topAcc);

        //for engine information
        engInfBut = findViewById(R.id.engInfBut);
        engInfButWindow = findViewById(R.id.engInfWindow);
        dateEngInf = findViewById(R.id.dateEngInf);
        engStatus = findViewById(R.id.engStatus);

        //for insurance button
        insBut = findViewById(R.id.insBut);
        insButWindow = findViewById(R.id.insWindow);
        dateIns = findViewById(R.id.dateIns);
        tripSum = findViewById(R.id.tripSum);

        //for calender drop down
        calenderWindow = findViewById(R.id.calender);
        calText = findViewById(R.id.calText);
        calender = (CalendarView) findViewById(R.id.calenderView);

        //for spinner drop down
        spinWindow = findViewById(R.id.spinnerWindow);
        tripSpinner = findViewById(R.id.tripSpinner);
        //adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //tripSpinner.setAdapter(adapter);
        //tripSpinner.setOnItemClickListener(this::onItemSelected);

        //for loc drop down
        locWindow = findViewById(R.id.locationWindow);
        locText = findViewById(R.id.locText);


        //calender
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM - dd - yyyy ");
        date = mdformat.format(calendar.getTime());
        calText.setText(date);

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                date = (month + 1) + " - " + day + " - " + year;
                dbQueryDate = (month + 1) + "-" + day + "-" + year;
                calText.setText(date);
                addItemsOnSpinner();
            }
        });
        //end calender
        //calls the drop down spinner to be created
        //addItemsOnSpinner();

        //update at start
        updateData();

    }//end onCreate


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
                startActivity(new Intent(getApplicationContext(), Settings.class));
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


    public void avSpButPressed(View view) {
        if (avSpButTrue == false) {
            avSpButWindow.setVisibility(View.VISIBLE);
            avSpBut.setBackgroundResource(R.drawable.drop_down);
            avSpButTrue = true;
        }//end if
        else {
            avSpButWindow.setVisibility(View.GONE);
            avSpBut.setBackgroundResource(R.drawable.drop_down_sideways);
            avSpButTrue = false;
        }//end else
    }//end avSpButPressed

    public void accButPressed(View view) {
        if (accButTrue == false) {
            accButWindow.setVisibility(View.VISIBLE);
            accBut.setBackgroundResource(R.drawable.drop_down);
            accButTrue = true;
        }//end if
        else {
            accButWindow.setVisibility(View.GONE);
            accBut.setBackgroundResource(R.drawable.drop_down_sideways);
            accButTrue = false;
        }//end else
    }//end accButPressed

    public void engInfButPressed(View view) {
        if (engInfButTrue == false) {
            engInfButWindow.setVisibility(View.VISIBLE);
            engInfBut.setBackgroundResource(R.drawable.drop_down);
            engInfButTrue = true;
        }//end if
        else {
            engInfButWindow.setVisibility(View.GONE);
            engInfBut.setBackgroundResource(R.drawable.drop_down_sideways);
            engInfButTrue = false;
        }//end else
    }//end engInfButPressed

    public void insButPressed(View view) {
        if (insButTrue == false) {
            insButWindow.setVisibility(View.VISIBLE);
            insBut.setBackgroundResource(R.drawable.drop_down);
            insButTrue = true;
        }//end if
        else {
            insButWindow.setVisibility(View.GONE);
            insBut.setBackgroundResource(R.drawable.drop_down_sideways);
            insButTrue = false;
        }//end else
    }

    //for calender`----

    public void dateClicked(View view) {
        if (calShowing == false) {
            calenderWindow.setVisibility(View.VISIBLE);
            calText.setVisibility(View.VISIBLE);
            calShowing = true;
            if (spinShowing == true) {
                spinWindow.setVisibility(View.GONE);
                spinShowing = false;
            }//end if
            if (locShowing == true) {
                locWindow.setVisibility(View.GONE);
                locShowing = false;
            }//end if
        }//end if
        else {
            calenderWindow.setVisibility(View.GONE);
            calText.setVisibility(View.GONE);
            calShowing = false;
        }//end else
    }
    //end for calender ------


    //for spinner ---
    public void tripClicked(View view) {
        if (spinShowing == false) {
            spinWindow.setVisibility(View.VISIBLE);
            spinShowing = true;
            if (calShowing == true) {
                calenderWindow.setVisibility(View.GONE);
                calText.setVisibility(View.GONE);
                calShowing = false;
            }//end if
            if (locShowing == true) {
                locWindow.setVisibility(View.GONE);
                locShowing = false;
            }//end if
        }//end if
        else {
            spinWindow.setVisibility(View.GONE);
            spinShowing = false;
        }//end else
    }

    private void addItemsOnSpinner() {
        tripSpinner.setAdapter(null);
        updateData();

        tempTripSummaryList = null;
        DatabaseRequestThread drThread = new DatabaseRequestThread();
        drThread.start();
        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        List<String> list = new ArrayList<String>();
        if(tempTripSummaryList.size() > 0) {
            for (int i = 0; i < tempTripSummaryList.size(); i++) {
                list.add("Trip " + (i + 1));
            }

        }else {
            list.add("No Trips");

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        tripSpinner.setAdapter(dataAdapter);

        tripSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //position is the index of the selection as if it is an array
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), text + " new", Toast.LENGTH_SHORT).show();
                if (tempTripSummaryList.size() > 0) {
                    tempTripSummary = tempTripSummaryList.get(position);
                    displayedTopSpeed = tempTripSummary.getTopSpeed();
                    displayedTopAcceleration = tempTripSummary.getTopAcceleration();
                    displayedEngineTroubleCodes = tempTripSummary.getEngineStatus();
                    updateData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //place holder
            }
        });

    }//end additemsonspinner


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        //required but not used
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //required but not used
    }

    //end for spinner ------

    //for location -------
    public void locationClicked(View view) {
        //location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET
                }, 1);

                //return;
            }
            else{
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                String address = hereLocation(location.getLatitude(), location.getLongitude());
                locText.setText(address);
            }
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String address = hereLocation(location.getLatitude(), location.getLongitude());
            locText.setText(address);
        }

        if(locShowing==false){
            locWindow.setVisibility(View.VISIBLE);
            locShowing = true;
            if(calShowing==true){
                calenderWindow.setVisibility(View.GONE);
                calText.setVisibility(View.GONE);
                calShowing = false;
            }//end if
            if(spinShowing==true){
                spinWindow.setVisibility(View.GONE);
                spinShowing = false;
            }//end if
        }//end if
        else{
            locWindow.setVisibility(View.GONE);
            locShowing = false;
        }//end else
    }

    private String hereLocation(double lat, double lon) {
        String name = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            //name = addresses.get(0).getAddressLine(0);

            if (addresses.size() > 0){
                for (int i  = 0; i<10;i++) {
                    if (addresses.get(i).getAddressLine(0) != null && addresses.get(i).getAddressLine(0).length()>0){
                        name = addresses.get(i).getAddressLine(0);
                        break;
                    }
                }
            }


        } catch (IOException e) {
            Toast.makeText(this, "Location Not Found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return name;
    }//end herelocation

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                        String address = hereLocation(location.getLatitude(), location.getLongitude());
                        locText.setText(address);
                    } else {
                        locText.setText("Location Not Found After Request");
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }//end onpermissionresult

    //end for location ------

    class RawDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (("DataCollectedInfo").equals(intent.getAction())){
                Bundle b = intent.getBundleExtra("DataCollected");
                tEngineTroubleCodes = b.getString("engineTroubleCodes");
                tTopSpeed = b.getDouble("topspeed", -1);
                tTopAcceleration = b.getDouble("topaccel", -1);
            }
        }
    }

    class DatabaseRequestThread extends Thread {
        @Override
        public void run() {
            tempTripSummaryList = db.tripSummaryDao().getAllByDate(dbQueryDate);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(rdreceiver);
        AppDatabase.destroyInstance();
    }

    //update the user view
    private void updateData() {
        //set date
        dateSpeed.setText(date);
        dateAcc.setText(date);
        dateEngInf.setText(date);
        dateIns.setText(date);

        //set Av Speed
        topSpeed.setText("Top Speed: "+displayedTopSpeed+ " mph");

        //set Acceleration
        topAcc.setText("Top Acceleration: "+displayedTopAcceleration);

        //set Eng Inf
        if (displayedEngineTroubleCodes ==  null){
            engStatus.setText("Status: No outstanding problems");
        }
        else engStatus.setText("Status: "+displayedEngineTroubleCodes);

        //tempTripSummary;
        if (tempTripSummary ==  null){
            tripSum.setText("Everything is looking good");
        }
        else tripSum.setText((CharSequence) tempTripSummary);

    }//end update Data
}
