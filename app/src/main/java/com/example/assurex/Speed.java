package com.example.assurex;
//for map
//package com.mapbox.mapboxandroiddemo.examples.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assurex.database.AppDatabase;

//for map
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

//for profile pic
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


public class Speed extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private static final String TAG = "Speed";
    private TextView speed;
    private TextView acceleration;
    private TextView tripTime, troubleCodes;
    CarDataReceiver receiver;
    SettingsReceiver settingsReceiver;
    //for map
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    //for theme
    private static boolean darkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiY2xpZW50aW5ncyIsImEiOiJjazE5dzI1cWUwYjVkM2NwY2c5Z21neHJ6In0.UvUvFuBQpl-DdyK9DAmYVw");
        //for dark mode
        if (Speed.getDarkMode() == false) {
            //settingsBut.setChecked(true);
            Toast.makeText(this, "Light Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.AppTheme);
        }
        else if (Speed.getDarkMode() == true) {
            //settingsBut.setChecked(false);
            Toast.makeText(this, "Dark Mode Picked", Toast.LENGTH_SHORT).show();
            setTheme(R.style.DarkTheme);
        }
        //end for dark mode
        setContentView(R.layout.activity_speed);

        speed = findViewById(R.id.speed);
        acceleration = findViewById(R.id.acceleration);

        tripTime = findViewById(R.id.tripTime);
        troubleCodes = findViewById(R.id.troubleCodes);

        receiver = new CarDataReceiver();
        settingsReceiver = new SettingsReceiver();
        registerReceiver(receiver, new IntentFilter("CarDataUpdates"));
        registerReceiver(settingsReceiver, new IntentFilter("SettingsUpdate"));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);

        Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        startService(rawDataIntent);


        //for map
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //end for map

        setDarkModeSpeed();

    }//end oncreate


    class CarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (("CarDataUpdates").equals(intent.getAction())) {

                Bundle b = intent.getBundleExtra("CarData");
                int spd = b.getInt("speed", 0);
                float accel = b.getFloat("acceleration", 0);

                troubleCodes.setText(b.getString("troubleCodes", "No Codes"));
                tripTime.setText(Double.toString(b.getDouble("tripTime", 0)));
                speed.setText(Integer.toString(spd));
                acceleration.setText(Integer.toString((int)accel));

                Log.d(TAG, "onReceive: text has been set");

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
                //NavUtils.navigateUpFromSameTask(this);
                break;
            }
            case R.id.infoPage: {
                Toast.makeText(this, "infoPage selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), infoPage.class));
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
                Intent intent = new Intent(getApplicationContext(), Settings.class);

                if (isAlwaysOnSet())
                    intent.putExtra("isAlwaysOnSet", true);
                else
                    intent.putExtra("isAlwaysOnSet", false);

                startActivity(intent);
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

    //for profile pic -----------

    //end for profile pic -----------

    //for map -----
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
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
    }

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
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        stopService(serviceIntent);

        Intent rawDataIntent = new Intent(this, RawDataCollectionService.class);
        stopService(rawDataIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    //end for map ------

    //for theme
    public static boolean getDarkMode (){
        return darkMode;
    }

    public static void setDarkMode (boolean input){
        darkMode = input;
    }

    public void setDarkModeSpeed (){
        //for dark mode
            if (Speed.getDarkMode() == false) {
                //settingsBut.setChecked(true);
                Toast.makeText(this, "Light Mode Picked", Toast.LENGTH_SHORT).show();
                setTheme(R.style.AppTheme);
            } else if (Speed.getDarkMode() == true) {
                //settingsBut.setChecked(false);
                Toast.makeText(this, "Dark Mode Picked", Toast.LENGTH_SHORT).show();
                setTheme(R.style.DarkTheme);
            }
            //end for dark mode
    }
}//end class speed