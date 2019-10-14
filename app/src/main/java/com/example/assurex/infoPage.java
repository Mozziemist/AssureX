package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

public class infoPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private boolean avSpButTrue = false;
    private Button avSpBut;
    private LinearLayout avSpButWindow;
    private boolean accButTrue = false;
    private Button accBut;
    private LinearLayout accButWindow;
    private boolean engInfButTrue = false;
    private Button engInfBut;
    private LinearLayout engInfButWindow;
    private boolean insButTrue = false;
    private Button insBut;
    private LinearLayout insButWindow;
    private boolean calShowing = false;
    private LinearLayout calenderWindow;
    private boolean spinShowing = false;
    private LinearLayout spinWindow;
    private Spinner tripSpinner;
    private ArrayAdapter<CharSequence> adapter;
    private boolean locShowing = false;
    private LinearLayout locWindow;

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

        //for spinner drop down
        spinWindow = findViewById(R.id.spinnerWindow);
        tripSpinner = findViewById(R.id.tripSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tripSpinner.setAdapter(adapter);
        //tripSpinner.setOnItemClickListener(this::onItemSelected);

        //for calender drop down
        locWindow = findViewById(R.id.locationWindow);



    }//end onCreate

    public void toMain(View view) {
        startActivity(new Intent(getApplicationContext(), Speed.class));
    }

    public void avSpButPressed(View view) {
        if(avSpButTrue==false){
            avSpButWindow.setVisibility(View.VISIBLE);
            avSpBut.setBackgroundResource(R.drawable.drop_down);
            avSpButTrue = true;
        }//end if
        else{
            avSpButWindow.setVisibility(View.GONE);
            avSpBut.setBackgroundResource(R.drawable.drop_down_sideways);
            avSpButTrue = false;
        }//end else
    }//end avSpButPressed

    public void accButPressed(View view) {
        if(accButTrue==false){
            accButWindow.setVisibility(View.VISIBLE);
            accBut.setBackgroundResource(R.drawable.drop_down);
            accButTrue = true;
        }//end if
        else{
            accButWindow.setVisibility(View.GONE);
            accBut.setBackgroundResource(R.drawable.drop_down_sideways);
            accButTrue = false;
        }//end else
    }//end accButPressed

    public void engInfButPressed(View view) {
        if(engInfButTrue==false){
            engInfButWindow.setVisibility(View.VISIBLE);
            engInfBut.setBackgroundResource(R.drawable.drop_down);
            engInfButTrue = true;
        }//end if
        else{
            engInfButWindow.setVisibility(View.GONE);
            engInfBut.setBackgroundResource(R.drawable.drop_down_sideways);
            engInfButTrue = false;
        }//end else
    }//end engInfButPressed

    public void insButPressed(View view) {
        if(insButTrue==false){
            insButWindow.setVisibility(View.VISIBLE);
            insBut.setBackgroundResource(R.drawable.drop_down);
            insButTrue = true;
        }//end if
        else{
            insButWindow.setVisibility(View.GONE);
            insBut.setBackgroundResource(R.drawable.drop_down_sideways);
            insButTrue = false;
        }//end else
    }

    public void dateClicked(View view) {
        if(calShowing==false){
            calenderWindow.setVisibility(View.VISIBLE);
            calShowing = true;
            if(spinShowing==true){
                spinWindow.setVisibility(View.GONE);
                spinShowing = false;
            }//end if
            if(locShowing==true){
                locWindow.setVisibility(View.GONE);
                locShowing = false;
            }//end ifb
        }//end if
        else{
            calenderWindow.setVisibility(View.GONE);
            calShowing = false;
        }//end else
    }

    public void tripClicked(View view) {
        if(spinShowing==false){
            spinWindow.setVisibility(View.VISIBLE);
            spinShowing = true;
            if(calShowing==true){
                calenderWindow.setVisibility(View.GONE);
                calShowing = false;
            }//end if
            if(locShowing==true){
                locWindow.setVisibility(View.GONE);
                locShowing = false;
            }//end if
        }//end if
        else{
            spinWindow.setVisibility(View.GONE);
            spinShowing = false;
        }//end else
    }

    //for spinner -----
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //end for spinner ------

    public void locationClicked(View view) {
        if(locShowing==false){
            locWindow.setVisibility(View.VISIBLE);
            if(calShowing==true){
                calenderWindow.setVisibility(View.GONE);
                calShowing = false;
            }//end if
            if(spinShowing==true){
                spinWindow.setVisibility(View.GONE);
                spinShowing = false;
            }//end if
            locShowing = true;
        }//end if
        else{
            locWindow.setVisibility(View.GONE);
            locShowing = false;
        }//end else
    }

}
