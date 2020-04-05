package com.example.assurex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Package extends AppCompatActivity {

    private final static String TAG = "Package";

    double totalTripScore;
    private TextView conditionText;


    //for bar
    private ProgressBar bar;
    int counter = 0;
    private Button tripScore;

    //for user data
    private TextView email;
    private TextView startDate;
    private TextView company;

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
        setContentView(R.layout.activity_package);

        //totalTripScore = getTotalTripScore();

        //assign to bar and button
        bar = (ProgressBar)findViewById(R.id.progressBar);
        tripScore = findViewById(R.id.tripScore);
        conditionText = findViewById(R.id.conditionText);

        //assign data to screen
        email = findViewById(R.id.email);
        company = findViewById(R.id.company);
        startDate = findViewById(R.id.startDate);

        //MARCIAL WORK HERE-------------------------------------
        //also, if you cuold please look at taking data for the total trip score as well
        //you can find that on line 196 in getScoreFromFireBase
        //it is returning 0 right now because of a try catch i put so it doesnt crash

        //email.setText("");
        //company.setText("");
        //startDate.setText("");

        //for database
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseUser user;
        String uid;
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

        final Object[] userInfoObject = new Object[1];

        db.collection("users")
                .document(uid)
                .collection("userinfo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    userInfoObject[0] = document.getData();
                                    HashMap userInfoHashMap = (HashMap) userInfoObject[0];
                                    company.setText((String) userInfoHashMap.get("new_insur"));
                                    totalTripScore = (double) userInfoHashMap.get("totalTripScore");
                                }catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

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
                //startActivity(new Intent(getApplicationContext(), Package.class));
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
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            }
            case R.id.signOut: {
                //Toast.makeText(this, "signOut selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected
    //end for menu --------


    public void prog() {


        conditionText.setVisibility(View.VISIBLE);

        if (totalTripScore<10 && totalTripScore>-1) {
            conditionText.setTextColor(Color.parseColor("#FF0000"));
            conditionText.setText("Bad");
        }
        else if (totalTripScore>9 && totalTripScore<25) {
            conditionText.setTextColor(Color.parseColor("#FFEB3B"));
            conditionText.setText("Poor");
        }
        else if (totalTripScore>24 && totalTripScore<80) {
            conditionText.setTextColor(Color.parseColor("#23B100"));
            conditionText.setText("Good");
        }
        else if (totalTripScore>79) {
            conditionText.setTextColor(Color.parseColor("#23B100"));
            conditionText.setText("Good");
        }
        else {
            conditionText.setTextColor(Color.parseColor("#FF0000"));
            conditionText.setText("Not Available");
        }

        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (counter == totalTripScore)
                    t.cancel();
                counter++;
                bar.setProgress(counter);
            }
        };

        t.schedule(tt,0,100);

    }//and prog


    public void getTotalTripScore(View view) {

        //make button invisible
        tripScore.setVisibility(view.GONE);

        //make bar visible
        bar.setVisibility(view.VISIBLE);

        prog();
    }
}//end class
