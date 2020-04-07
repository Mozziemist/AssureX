package com.example.assurex;
//package com.mapbox.mapboxandroiddemo.examples.location;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.assurex.model.TripSummary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
//import com.mapbox.mapboxandroiddemo.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

//for date
//for map
//import com.mapbox.mapboxandroiddemo.R;
//end for map


public class infoPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, MapboxMap.OnMapClickListener {

    //average speed variables
    private boolean avSpButTrue = false;
    private final static String TAG = "infoPage";
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
    List<Object> tempTripSummaryList = new ArrayList<>();
    List<Object> tempRawDataItemList = new ArrayList<>();
    TripSummary tempTripSummary;
    boolean FirebaseTripSummaryQueryIsCompleted = false;
    boolean FirebaseRawDataItemQueryIsCompleted = false;
    FirebaseFirestore db;
    FirebaseUser user;
    String uid;
    String dbQueryDate;
    String tEngineTroubleCodes;
    double tTopSpeed = 0;
    double tTopAcceleration = 0;
    String displayedEngineTroubleCodes;
    double displayedTopSpeed = 0;
    double displayedTopAcceleration = 0;
    RawDataReceiver rdreceiver;
    Object[] tempRawDataItemArray;


    //for map
    private MapView mapView;
    private MapboxMap mapboxMap;
    private LinearLayout infoMap;
    private List<Point> routeCoordinates;
    private boolean isFirstLocation = true;
    private double[] firstLocationArray = new double[2];
    private static LatLng locationOne = new LatLng();
    private static LatLng locationTwo = new LatLng();

    //route camera centering
    private double routeCenterLat = 0.0d;
    private double routeCenterLng = 0.0d;
    private int myCount = 0;


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
        //access token for map
        Mapbox.getInstance(this, "pk.eyJ1IjoiY2xpZW50aW5ncyIsImEiOiJjazE5dzI1cWUwYjVkM2NwY2c5Z21neHJ6In0.UvUvFuBQpl-DdyK9DAmYVw");
        //----
        setContentView(R.layout.activity_info_page);
        //db = AppDatabase.getInstance(this);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is signed in
            //uid = user.getUid();
            uid = user.getEmail();
        } else {
            // No user is signed in
            uid = "debug_user";
            Log.d(TAG, "Error. No User appears to be signed in");
        }

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
        //date = mdformat.format(calendar.getTime());
        date = "Select Date First";
        calText.setText(date);
        dbQueryDate = date.replaceAll("\\s+", "");

        addItemsOnSpinner();

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

        //for map
        initRouteCoordinates();
        infoMap = findViewById(R.id.infoMap);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Toast.makeText(this, "Called getMapAsync", Toast.LENGTH_SHORT).show();

        //update at start
        //updateData();

    }//end onCreate


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
                //startActivity(new Intent(getApplicationContext(), infoPage.class));
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
                startActivity(new Intent(getApplicationContext(), Settings.class));
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
        //updateData();

        tempTripSummaryList.clear();

        db.collection("users")
                .document(uid)
                .collection("tripsummaries")
                .whereEqualTo("date", dbQueryDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    tempTripSummaryList.add(document.getData());
                                }catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        addItemsOnSpinnerHelper();
                    }
                });



    }//end additemsonspinner

    public void addItemsOnSpinnerHelper(){
        Object[] tempTripSummaryArray = tempTripSummaryList.toArray();

        List<String> list = new ArrayList<>();

        if(tempTripSummaryArray.length > 0){
            for (int i = 0; i < tempTripSummaryArray.length; i++) {
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
                isFirstLocation = true;
                //position is the index of the selection as if it is an array
                //if (tempTripSummaryList.size() > 0) {
                if(tempTripSummaryArray.length > 0) {
                    infoMap.setVisibility(View.VISIBLE);
                    HashMap mapSelectedTripSummary;
                    mapSelectedTripSummary = (HashMap) tempTripSummaryArray[position];
                    displayedTopSpeed = (double) mapSelectedTripSummary.get("topSpeed");
                    displayedTopAcceleration = (double) mapSelectedTripSummary.get("topAcceleration");
                    displayedEngineTroubleCodes = (String) mapSelectedTripSummary.get("engineStatus");

                    tempRawDataItemList.clear();

                    db.collection("users")
                            .document(uid)
                            .collection("rawdataitems")
                            .document(dbQueryDate)
                            .collection("Trip Number " + mapSelectedTripSummary.get("tripNumber"))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            try {
                                                tempRawDataItemList.add(document.getData());
                                            }catch (NullPointerException e) {
                                                e.printStackTrace();
                                            }
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }

                                    try {
                                        Thread.sleep(2500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    if(tempRawDataItemList != null){
                                        if(!tempRawDataItemList.isEmpty()){
                                            tempRawDataItemArray = tempRawDataItemList.toArray();
                                        }
                                    }

                                    if(tempRawDataItemArray.length > 0){
                                        routeCoordinates = new ArrayList<>();
                                        for(int i = 0; i < tempRawDataItemArray.length; i++){
                                            HashMap mapTempRawDataItem = (HashMap) tempRawDataItemArray[i];
                                            //if neither latitude nor longitude are 0, indicating it contains actual coordinates
                                            if(( (double)mapTempRawDataItem.get("longitude") != (double) 0) && (double)mapTempRawDataItem.get("latitude") != (double) 0){
                                                routeCoordinates.add(Point.fromLngLat((double) mapTempRawDataItem.get("longitude"),(double) mapTempRawDataItem.get("latitude") ));

                                                routeCenterLat += (double) mapTempRawDataItem.get("latitude");
                                                routeCenterLng += (double) mapTempRawDataItem.get("longitude");
                                                myCount++;

                                                if (isFirstLocation == true) {
                                                    firstLocationArray[0] = (double)mapTempRawDataItem.get("latitude");
                                                    firstLocationArray[1] = (double) mapTempRawDataItem.get("longitude");
                                                    locationOne = new LatLng((double)mapTempRawDataItem.get("latitude"), (double) mapTempRawDataItem.get("longitude"));
                                                    isFirstLocation = false;
                                                }//find where camera should look
                                                if (i==tempRawDataItemArray.length-1) {
                                                    locationTwo = new LatLng((double)mapTempRawDataItem.get("latitude"), (double) mapTempRawDataItem.get("longitude"));
                                                }//find where camera should look


                                            }
                                        }
                                    }

                                    //at this point, tempRawDataItemArray *should* have all the raw data items in it
                                    //to work with this data, you need to do something like
                                    //for(...){
                                    //      Map<String, Object> mapTempRawDataItem = new HashMap<>();
                                    //      mapTempRawDataItem = (HashMap) tempRawDataItemArray[i];
                                    //      latitude = mapTempRawDataItem.get("latitude");
                                    //      longitude = mapTempRawDataItem.get("longitude");
                                    //      ...
                                    //      do whatever you need to do with the longitudes and latitudes, either store them somewhere else
                                    //      or work with them as they are received
                                    //      ...
                                    //

                                    updateData();
                                }
                            });



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //place holder
            }
        });
    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        //required but not used
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //required but not used
    }

    //end for spinner ------
    /*
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
    */

    public void currentClicked(View view){

        if (tTopSpeed > 0 || tTopAcceleration > 0) {
            topSpeed.setText("Top Speed: "+tTopSpeed+ " MPH");
            topAcc.setText("Top Acceleration: "+tTopAcceleration + " MPH/S");
            if (tEngineTroubleCodes ==  null){
                engStatus.setText("Status: No outstanding problems");
            }
            else engStatus.setText("Status: "+tEngineTroubleCodes);
        } else {
            Toast.makeText(this, "Not Currently On Trip", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        infoPage.this.mapboxMap = mapboxMap;
        Toast.makeText(infoPage.this, "called onMapReady", Toast.LENGTH_SHORT).show();

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.addOnMapClickListener(infoPage.this);
                Toast.makeText(infoPage.this, "Added onmapclicklistener", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(locationOne) // Northeast
                .include(locationTwo) // Southwest
                .build();
        Toast.makeText(
                infoPage.this,
                "got points and gonna easeCamera",
                Toast.LENGTH_LONG
        ).show();

        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
        return true;
    }

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

//    class DatabaseRequestThread extends Thread {
//        @Override
//        public void run() {
//            tempTripSummaryList = db.tripSummaryDao().getAllByDate(dbQueryDate);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(rdreceiver);
        mapView.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        //AppDatabase.destroyInstance();
    }

    //update the user view
    private void updateData() {
        //set date
        dateSpeed.setText(date);
        dateAcc.setText(date);
        dateEngInf.setText(date);
        dateIns.setText(date);

        //set Av Speed
        topSpeed.setText("Top Speed: "+displayedTopSpeed+ " MPH");

        //set Acceleration
        topAcc.setText("Top Acceleration: "+displayedTopAcceleration + " MPH/S");

        //set Eng Inf
        if ((displayedEngineTroubleCodes ==  null) || displayedEngineTroubleCodes.equals("")){
        //if (displayedEngineTroubleCodes.equals(""){
            engStatus.setText("Status: No outstanding problems");
        }
        else engStatus.setText("Status: "+displayedEngineTroubleCodes);

        //tempTripSummary;
        if (tempTripSummary ==  null){
            tripSum.setText("Everything is looking good");
        }
        else tripSum.setText("No Trip Summary Selected");

        //for map
        //for map
        mapView = findViewById(R.id.mapView);

        //mapView.onCreate(savedInstanceState);

        //if the route coordinates array isn't a null pointer
        if (routeCoordinates != null) {
            //if the route coordinates array isn't empty
            if (!routeCoordinates.isEmpty()) {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {

                                // Create the LineString from the list of coordinates and then make a GeoJSON
                                // FeatureCollection so we can add the line to our map as a layer.
                                style.addSource(new GeoJsonSource("line-source",
                                        FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
                                                LineString.fromLngLats(routeCoordinates)
                                        )})));

                                // The layer properties for our line. This is where we make the line dotted, set the
                                // color, etc.
                                style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                                        PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                        PropertyFactory.lineWidth(5f),
                                        PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                                ));

                                firstLocationArray[0] = routeCenterLat / myCount;
                                firstLocationArray[1] = routeCenterLng / myCount;
                                Log.d(TAG, "onStyleLoaded: Lat: " + (routeCenterLat/myCount) + " lng: " + (routeCenterLng/myCount));
                                Log.d(TAG, "onStyleLoaded: Default: lat: " + firstLocationArray[0] + " lng: " + firstLocationArray[1]);
                                CameraPosition position = new CameraPosition.Builder()
                                        .target(new LatLng(firstLocationArray[0], firstLocationArray[1])) // Sets the new camera position
                                        .zoom(13) // Sets the zoom
                                        .bearing(0) // Rotate the camera/
                                        .tilt(0) // Set the camera tilt
                                        .build(); // Creates a CameraPosition from the builder

                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);

                            }

                        });
                    }
                });
            }
        }

        //mapboxMap.setCameraPosition(firstLocationArray[0], firstLocationArray[1]);
        /*
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(locationOne) // Northeast
                .include(locationTwo) // Southwest
                .build();

        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);

         */

    }//end update Data

    private void initRouteCoordinates() {
        // Create a list to store our line coordinates.
        routeCoordinates = new ArrayList<>();
        routeCoordinates.add(Point.fromLngLat(-118.39439114221236, 33.397676454651766));
        routeCoordinates.add(Point.fromLngLat(-118.39421054012902, 33.39769799454838));
        routeCoordinates.add(Point.fromLngLat(-118.39408583869053, 33.39761901490136));
        routeCoordinates.add(Point.fromLngLat(-118.39388373635917, 33.397328225582285));
        routeCoordinates.add(Point.fromLngLat(-118.39372033447427, 33.39728514560042));
        routeCoordinates.add(Point.fromLngLat(-118.3930882271826, 33.39756875508861));
        routeCoordinates.add(Point.fromLngLat(-118.3928216241072, 33.39759029501192));
        routeCoordinates.add(Point.fromLngLat(-118.39227981785722, 33.397234885594564));
        routeCoordinates.add(Point.fromLngLat(-118.392021814881, 33.397005125197666));
        routeCoordinates.add(Point.fromLngLat(-118.39090810203379, 33.396814854409186));
        routeCoordinates.add(Point.fromLngLat(-118.39040499623022, 33.39696563506828));
        routeCoordinates.add(Point.fromLngLat(-118.39005669221234, 33.39703025527067));
        routeCoordinates.add(Point.fromLngLat(-118.38953208616074, 33.39691896489222));
        routeCoordinates.add(Point.fromLngLat(-118.38906338075398, 33.39695127501678));
        routeCoordinates.add(Point.fromLngLat(-118.38891287901787, 33.39686511465794));
        routeCoordinates.add(Point.fromLngLat(-118.38898167981154, 33.39671074380141));
        routeCoordinates.add(Point.fromLngLat(-118.38984598978178, 33.396064537239404));
        routeCoordinates.add(Point.fromLngLat(-118.38983738968255, 33.39582400356976));
        routeCoordinates.add(Point.fromLngLat(-118.38955358640874, 33.3955978295119));
        routeCoordinates.add(Point.fromLngLat(-118.389041880506, 33.39578092284221));
        routeCoordinates.add(Point.fromLngLat(-118.38872797688494, 33.3957916930261));
        routeCoordinates.add(Point.fromLngLat(-118.38817327048618, 33.39561218978703));
        routeCoordinates.add(Point.fromLngLat(-118.3872530598711, 33.3956265500598));
        routeCoordinates.add(Point.fromLngLat(-118.38653065153775, 33.39592811523983));
        routeCoordinates.add(Point.fromLngLat(-118.38638444985126, 33.39590657490452));
        routeCoordinates.add(Point.fromLngLat(-118.38638874990086, 33.395737842093304));
        routeCoordinates.add(Point.fromLngLat(-118.38723155962309, 33.395027006653244));
        routeCoordinates.add(Point.fromLngLat(-118.38734766096238, 33.394441819579285));
        routeCoordinates.add(Point.fromLngLat(-118.38785936686516, 33.39403972556368));
        routeCoordinates.add(Point.fromLngLat(-118.3880743693453, 33.393616088784825));
        routeCoordinates.add(Point.fromLngLat(-118.38791956755958, 33.39331092541894));
        routeCoordinates.add(Point.fromLngLat(-118.3874852625497, 33.39333964672257));
        routeCoordinates.add(Point.fromLngLat(-118.38686605540683, 33.39387816940854));
        routeCoordinates.add(Point.fromLngLat(-118.38607484627983, 33.39396792286514));
        routeCoordinates.add(Point.fromLngLat(-118.38519763616081, 33.39346171215717));
        routeCoordinates.add(Point.fromLngLat(-118.38523203655761, 33.393196040109466));
        routeCoordinates.add(Point.fromLngLat(-118.3849955338295, 33.393023711860515));
        routeCoordinates.add(Point.fromLngLat(-118.38355931726203, 33.39339708930139));
        routeCoordinates.add(Point.fromLngLat(-118.38323251349217, 33.39305243325907));
        routeCoordinates.add(Point.fromLngLat(-118.3832583137898, 33.39244928189641));
        routeCoordinates.add(Point.fromLngLat(-118.3848751324406, 33.39108499551671));
        routeCoordinates.add(Point.fromLngLat(-118.38522773650804, 33.38926830725471));
        routeCoordinates.add(Point.fromLngLat(-118.38508153482152, 33.38916777794189));
        routeCoordinates.add(Point.fromLngLat(-118.38390332123025, 33.39012280171983));
        routeCoordinates.add(Point.fromLngLat(-118.38318091289693, 33.38941192035707));
        routeCoordinates.add(Point.fromLngLat(-118.38271650753981, 33.3896129783018));
        routeCoordinates.add(Point.fromLngLat(-118.38275090793661, 33.38902416443619));
        routeCoordinates.add(Point.fromLngLat(-118.38226930238106, 33.3889451769069));
        routeCoordinates.add(Point.fromLngLat(-118.38258750605169, 33.388420985121336));
        routeCoordinates.add(Point.fromLngLat(-118.38177049662707, 33.388083490107284));
        routeCoordinates.add(Point.fromLngLat(-118.38080728551597, 33.38836353925403));
        routeCoordinates.add(Point.fromLngLat(-118.37928506795642, 33.38717870977523));
        routeCoordinates.add(Point.fromLngLat(-118.37898406448423, 33.3873079646849));
        routeCoordinates.add(Point.fromLngLat(-118.37935386875012, 33.38816247841951));
        routeCoordinates.add(Point.fromLngLat(-118.37794345248027, 33.387810620840135));
        routeCoordinates.add(Point.fromLngLat(-118.37546662390886, 33.38847843095069));
        routeCoordinates.add(Point.fromLngLat(-118.37091717142867, 33.39114243958559));

        firstLocationArray[0] = 33.397676454651766;
        firstLocationArray[1] = -118.39439114221236;
        LatLng locationOne = new LatLng(-89, 33.397676454651766);
        LatLng locationTwo = new LatLng(-70, 33.39114243958559);
    }//end coordinates

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
