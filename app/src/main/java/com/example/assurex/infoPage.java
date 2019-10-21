package com.example.assurex;
//package com.mapbox.mapboxandroiddemo.examples.location;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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

import java.io.IOException;
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

    //acceleration variables
    private boolean accButTrue = false;
    private Button accBut;
    private LinearLayout accButWindow;

    //engine info variables
    private boolean engInfButTrue = false;
    private Button engInfBut;
    private LinearLayout engInfButWindow;

    //insurance variables
    private boolean insButTrue = false;
    private Button insBut;
    private LinearLayout insButWindow;

    //calender variables
    private boolean calShowing = false;
    private LinearLayout calenderWindow;
    private TextView calText;
    private CalendarView calender;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        //for average speed button
        avSpBut = findViewById(R.id.avSpBut);
        avSpButWindow = findViewById(R.id.avSpWindow);

        //for acceleration button
        accBut = findViewById(R.id.accBut);
        accButWindow = findViewById(R.id.accWindow);

        //for engine information
        engInfBut = findViewById(R.id.engInfBut);
        engInfButWindow = findViewById(R.id.engInfWindow);

        //for insurance button
        insBut = findViewById(R.id.insBut);
        insButWindow = findViewById(R.id.insWindow);

        //for calender drop down
        calenderWindow = findViewById(R.id.calender);
        calText = findViewById(R.id.calText);
        calender = (CalendarView) findViewById(R.id.calenderView);

        //for spinner drop down
        spinWindow = findViewById(R.id.spinnerWindow);
        tripSpinner = findViewById(R.id.tripSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tripSpinner.setAdapter(adapter);
        //tripSpinner.setOnItemClickListener(this::onItemSelected);

        //for loc drop down
        locWindow = findViewById(R.id.locationWindow);
        locText = findViewById(R.id.locText);


        //calender
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM - dd - yyyy ");
        String strDate = mdformat.format(calendar.getTime());
        calText.setText(strDate);

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String date = (month + 1) + " - " + day + " - " + year;
                calText.setText(date);
            }
        });
        //end calender



    }//end onCreate

    public void toMain(View view) {
        startActivity(new Intent(getApplicationContext(), Speed.class));
    }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            addresses = geocoder.getFromLocation(lat, lon, 1);
            name = addresses.get(0).getAddressLine(0);
            /*
            if (addresses.size() > 0){
                for (Address adr: addresses) {
                    if (adr.getAddressLine(0) != null && adr.getAddressLine(0).length()>0){
                        name = adr.getAddressLine(0);
                        break;
                    }
                }
            }

             */
        } catch (IOException e) {
            Toast.makeText(this, "No Location Found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return name;
    }//end herelocation

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String address = hereLocation(location.getLatitude(), location.getLongitude());
                    locText.setText(address);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }//end onpermissionresult

    //end for location ------


}
