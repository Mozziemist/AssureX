package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class infoPage extends AppCompatActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        avSpBut = findViewById(R.id.avSpBut);
        avSpButWindow = findViewById(R.id.avSpWindow);
        accBut = findViewById(R.id.accBut);
        accButWindow = findViewById(R.id.accWindow);
        engInfBut = findViewById(R.id.engInfBut);
        engInfButWindow = findViewById(R.id.engInfWindow);
        insBut = findViewById(R.id.insBut);
        insButWindow = findViewById(R.id.insWindow);


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
}
