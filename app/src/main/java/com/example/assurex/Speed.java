//Copyright [2019] [Assurex]
//SPDX-License-Identifier: Apache-2.0

package com.example.assurex;
//for map
//package com.mapbox.mapboxandroiddemo.examples.location;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

//for map
//import com.mapbox.mapboxandroiddemo.R;
//for profile pic


public class Speed extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private static final String TAG = "Speed";
    private static String username;
    private TextView speed;
    private TextView acceleration;
    private TextView tripTime, troubleCodes;
    private TextView speedLimitView;
    private TextView totalDistance;
    private TextView lastScore;
    private int speedLimitInt;
    private int oldSpeedLimitInt;
    CarDataReceiver receiver;
    SettingsReceiver settingsReceiver;
    SpeedLimitReceiver slReceiver;
    ScoreReceiver scoreReceiver;
    SpeedLimitThread speedLimitThread;
    private static Boolean isEngineOn;
    //for map
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    //for theme
    private static boolean darkMode = false;
    public static boolean themeChanged = false;

    private boolean warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiY2xpZW50aW5ncyIsImEiOiJjazE5dzI1cWUwYjVkM2NwY2c5Z21neHJ6In0.UvUvFuBQpl-DdyK9DAmYVw");

        loadSettings();

       /* //for dark mode
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
        //end for dark mode */

        setContentView(R.layout.activity_speed);

        //for display
        speed = findViewById(R.id.speed);
        acceleration = findViewById(R.id.acceleration);
        speedLimitView = findViewById(R.id.speedLimitView);
        totalDistance = findViewById(R.id.Distance);
        lastScore = findViewById(R.id.tripScore);
        isEngineOn = false;
//        tripTime = findViewById(R.id.tripTime);
//        troubleCodes = findViewById(R.id.troubleCodes);

        receiver = new CarDataReceiver();
        settingsReceiver = new SettingsReceiver();
        slReceiver = new SpeedLimitReceiver();
        scoreReceiver = new ScoreReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));
        registerReceiver(settingsReceiver, new IntentFilter("SettingsUpdate"));
        registerReceiver(slReceiver, new IntentFilter("SpeedLimitUpdates"));
        registerReceiver(scoreReceiver, new IntentFilter("ScoreUpdates"));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);

        //for map
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //end for map

        speedLimitThread = new SpeedLimitThread();
        speedLimitThread.start();

        Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        rawDataIntent.putExtra("isRegistering", getIntent().getBooleanExtra("isRegistering", false));
        if (getIntent().getBooleanExtra("isRegistering", false))
        {
            rawDataIntent.putExtra("device_id", getIntent().getExtras().getString("device_id"));
            rawDataIntent.putExtra("new_insur", getIntent().getExtras().getString("new_insur"));
            rawDataIntent.putExtra("new_user",  getIntent().getExtras().getString("new_user"));
        }
        startService(rawDataIntent);

        warning = false;

    }//end oncreate

    public void loadSettings() {
        FileInputStream fis = null;

        try {
            fis = openFileInput("assurexSettings.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null)
            {
                if (text.equals("isScreenAlwaysOn=true"))
                {
                    // flag on
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                else if (text.equals("isScreenAlwaysOn=false"))
                {
                    // flag off
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                else if (text.equals("isDarkmode=true"))
                {
                    // set darkmode true
                    setTheme(R.style.DarkTheme);
                    setDarkMode(true);
                    // this.recreate();
                }
                else if (text.equals("isDarkmode=false"))
                {
                    // set darkmode false
                    setTheme(R.style.AppTheme);
                    setDarkMode(false);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
            {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    class SpeedLimitReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (("SpeedLimitUpdates").equals(intent.getAction())){
                speedLimitView.setText(intent.getStringExtra("limit"));
                Log.d(TAG, "onReceive: Speed limit set");
            }
        }
    }
    class ScoreReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (("ScoreUpdates").equals(intent.getAction())){
                int temp = (int)intent.getDoubleExtra("score", 100);
                lastScore.append(Integer.toString(temp));
                lastScore.setVisibility(View.VISIBLE);
            }
        }
    }

    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (("CarDataUpdates").equals(intent.getAction())) {

                Bundle b = intent.getBundleExtra("CarData");
                int spd = b.getInt("speed", 0);
                float accel = b.getFloat("acceleration", 0);
                int dist = b.getInt("distance", 0);


                //troubleCodes.setText(b.getString("troubleCodes", "Trouble Codes"));
                //tripTime.setText(Double.toString(b.getDouble("tripTime", 0)));
                speed.setText(Integer.toString(spd));
                acceleration.setText(Integer.toString((int)accel));
                if (dist >= 5280/4)
                {
                    float distf = (float)dist / 5280;
                    totalDistance.setText(String.format("%.2f", distf) + " mi");
                }
                else
                {
                    totalDistance.setText(Integer.toString(dist) + " ft");
                }

                isEngineOn = b.getBoolean("isEngineOn", false);

                if (isEngineOn)
                {
                    lastScore.setVisibility(View.INVISIBLE);
                    lastScore.setText("Score: ");
                }

                Log.d(TAG, "onReceive: text has been set");
            }
        }
    }

    class SpeedLimitThread extends Thread {
        @Override
        public void run() {

            // Object to make requests for speed limit from HERE API
            SpeedLimitRequester speedLimitRequester = new SpeedLimitRequester();

            while (!Thread.currentThread().isInterrupted())
            {

                if (isEngineOn)
                {
                    Bundle b = new Bundle();
                    if (mapboxMap != null && mapboxMap.getLocationComponent().isLocationComponentActivated())
                    {
                        double latitude = 0;
                        if (mapboxMap.getLocationComponent().getLastKnownLocation() != null) {
                            latitude = mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude();
                        }
                        double longitude = 0;
                        if (mapboxMap.getLocationComponent().getLastKnownLocation() != null) {
                            longitude = mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude();
                        }
                        b.putDouble("Latitude", latitude);
                        b.putDouble("Longitude", longitude);

                        String wpnt = Double.toString(latitude);
                        wpnt += ',' + Double.toString(longitude);
                        speedLimitRequester.sendRequest(wpnt);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (speedLimitRequester.getSpeedLimit() > 0)
                        {
                            Intent spdlmtIntent = new Intent("SpeedLimitUpdates");
                            Log.d(TAG, "run: sending > 0: " + speedLimitRequester.getSpeedLimit() + wpnt);
                            spdlmtIntent.putExtra("limit", Integer.toString(speedLimitRequester.getSpeedLimit()));
                            sendBroadcast(spdlmtIntent);
                            b.putInt("SpeedLimit", speedLimitRequester.getSpeedLimit());

                            SpeedWarning(speedLimitRequester.getSpeedLimit());
                        }
                        else
                        {
                            Intent spdlmtIntent = new Intent("SpeedLimitUpdates");
                            spdlmtIntent.putExtra("limit", "NA");
                            sendBroadcast(spdlmtIntent);
                            //sendSpeedLimitToRDCollectionSvc(-1);
                            b.putInt("SpeedLimit", -1);
                            Log.d(TAG, "run: sent NA = 0");
                        }

                    }
                    else
                    {
                        Intent spdlmtIntent = new Intent("SpeedLimitUpdates");
                        spdlmtIntent.putExtra("limit", "NA");
                        sendBroadcast(spdlmtIntent);
                        //sendSpeedLimitToRDCollectionSvc(-1);
                        b.putInt("SpeedLimit", -1);
                        Log.d(TAG, "run: sent NA: mapbox not active");
                    }
                    sendSpeedDataToRDCollectionSvc(b);
                }//end if engineOn = true
                else
                {
                    Intent spdlmtIntent = new Intent("SpeedLimitUpdates");
                    spdlmtIntent.putExtra("limit", "NA");
                    sendBroadcast(spdlmtIntent);
                    //sendSpeedLimitToRDCollectionSvc(-1);
                    Bundle b = new Bundle();
                    b.putDouble("Latitude", 0.0);
                    b.putDouble("Longitude", 0.0);
                    b.putInt("SpeedLimit", -1);
                    sendSpeedDataToRDCollectionSvc(b);
                    Log.d(TAG, "run: sent NA: Engine off");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    class SettingsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (("SettingsUpdate").equals(intent.getAction())) {

                if (intent.getBooleanExtra("alwaysOn", true))
                {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                else
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }

        }
    }

    private void sendSpeedDataToRDCollectionSvc(Bundle b){
        Intent sendSpeedLimitInfo = new Intent("MiscDataFromSpeedjava");
        sendSpeedLimitInfo.putExtra("miscData", b);
        sendBroadcast(sendSpeedLimitInfo);
    }

    public boolean isAlwaysOnSet() {

        int flg = getWindow().getAttributes().flags;
        boolean flag = false;
        if ((flg & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) == WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) {
            flag = true;
        }
        return flag;
    }

    //for menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        menu.findItem(R.id.profileUser).setTitle(Speed.getUsername());
        //Toast.makeText(this, "username set + " + username, Toast.LENGTH_SHORT).show();
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
                //NavUtils.navigateUpFromSameTask(this);
                break;
            }
            case R.id.infoPage: {
                //Toast.makeText(this, "infoPage selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), infoPage.class));
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
                Intent intent = new Intent(getApplicationContext(), Settings.class);

                if (isAlwaysOnSet())
                    intent.putExtra("isAlwaysOnSet", true);
                else
                    intent.putExtra("isAlwaysOnSet", false);

                startActivity(intent);
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

    //for profile pic -----------

    //end for profile pic -----------

    //for map -----
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }//end onExplanationNeeded

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            //Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }//end onPermissionResult

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        Speed.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });


    }//endOnMapReady

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }//end enableLocationComponent

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }//end onRequestPermissionResult

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        loadSettings();
/*
        if (Speed.getDarkMode() == false) {
            //settingsBut.setChecked(true);
            //Toast.makeText(this, "Light Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.AppTheme);
            //reCreate();
        } else if (Speed.getDarkMode() == true) {
            //settingsBut.setChecked(false);
            //Toast.makeText(this, "Dark Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.DarkTheme);
            //reCreate();
        }

 */
        if (themeChanged){
            themeChanged = false;
            this.recreate();
        }//end if
    }//end onResume

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(settingsReceiver);
        unregisterReceiver(slReceiver);
        unregisterReceiver(scoreReceiver);
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        stopService(serviceIntent);

        Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        stopService(rawDataIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
        //if you want to save stuff before dark mode changes it
        //savedInstanceState.putBoolean("MyBoolean", true);
        //savedInstanceState.putDouble("myDouble", 1.9);
        //savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("Username", username);
    }
    //end for map ------

    //for theme
    public static boolean getDarkMode (){ return darkMode; }

    public static void setDarkMode (boolean input){
        darkMode = input;
    }


    //currently trying to figure out how to use
    /*
    public void setDarkModeSpeed (){
        //for dark mode
            if (Speed.getDarkMode() == false) {
                //settingsBut.setChecked(true);
                Toast.makeText(this, "Light Mode Picked", Toast.LENGTH_SHORT).show();
                ThemeUtils.setTheme("light");
                ThemeUtils.applyTheme(this);
                //reCreate();
            } else if (Speed.getDarkMode() == true) {
                //settingsBut.setChecked(false);
                Toast.makeText(this, "Dark Mode Picked", Toast.LENGTH_SHORT).show();
                ThemeUtils.setTheme("dark");
                ThemeUtils.applyTheme(this);
                //reCreate();
            }
            //end for dark mode
    }//end setDarmModeSpeed

     */

    public class SpeedLimitRequester {
        private double speedLimit;
        private String startURL, endURL;

        public SpeedLimitRequester(){
            speedLimit = 0;

            startURL = "https://route.api.here.com/routing/7.2/calculateroute.json?";
            endURL = "&legattributes=li&mode=fastest;car&app_id=c5QU4Uke4T5WqcxB3Z8O&app_code=s26PhxE3UnFLBkFkrSDVtw";
        }

        public int getSpeedLimit()
        {
            return ((int)Math.round(speedLimit * 2.23694));
        }

        public void sendRequest(String waypoint)
        {
            RequestQueue mQueue;
            mQueue = Volley.newRequestQueue(getApplicationContext());
            String url = startURL + "waypoint0=" + waypoint + "&waypoint1=" + waypoint + endURL;

            Log.d(TAG, "sendRequest: JSON Prep...");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: JSON Response success: ");
                            try {
                                JSONObject jsonObject = response.getJSONObject("response");
                                JSONArray jsonArray = jsonObject.getJSONArray("route");

                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                JSONArray jsonArray1 = jsonObject1.getJSONArray("leg");

                                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                                JSONArray jsonArray2 = jsonObject2.getJSONArray("link");

                                JSONObject jsonObject3 = jsonArray2.getJSONObject(0);
                                speedLimit = jsonObject3.getDouble("speedLimit");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d(TAG, "onErrorResponse: NO RESPONSE");
                }
            });

            mQueue.add(request);
        }
    }

    public void SpeedWarning(int spdl) {
        Intent popup = new Intent(getApplicationContext(), PopupWarning.class);
        int spd = Integer.parseInt(speed.getText().toString());

        if(warning == false && spdl + 5 <= spd) {
            warning = true;
            startActivity(popup);
        }

        if(warning == true && spdl + 5 > spd) {
            warning = false;
        }
    }

    //for username
    public static void setUsername(String username1){
        username=username1;
    }
    public static String getUsername(){
        return username;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        //boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
        //double myDouble = savedInstanceState.getDouble("myDouble");
        //int myInt = savedInstanceState.getInt("MyInt");
        username = savedInstanceState.getString("Username");
        //Toast.makeText(this, "restored user name: " + username, Toast.LENGTH_SHORT).show();

    }

    //end for username

    public static boolean getIsEngineOn(){
        return isEngineOn;
    }

}//end class speed