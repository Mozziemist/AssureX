package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.assurex.database.DBHelper;
import com.example.assurex.database.DataSource;
import com.example.assurex.model.DataItem;
import com.example.assurex.sample.SampleDataProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<DataItem> dataItemSampleList = SampleDataProvider.dataItemList;

    DataSource mDataSource;

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataSource = new DataSource(this);
        mDataSource.open();
        //Toast.makeText(this, "Database acquired!", Toast.LENGTH_SHORT).show();
        mDataSource.seedDatabase(dataItemSampleList);
        /*
        long numItems = mDataSource.getDataItemsCount();
        if (numItems == 0){
            //do inserting
            Toast.makeText(this, "Data inserted!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Data already inserted!", Toast.LENGTH_SHORT).show();
        }
         */
    }

    //these are helpful to ensure database connection is active when interacting with app
    //but we want our app to function in background as well so it might be best to not
    //close the database connection while the app is not focused
    /*
    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataSource.open();
    }
    */

    @Override
    protected void onStop() {
        super.onStop();
        mDataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Speed.class));
            }
        });

    }
}
