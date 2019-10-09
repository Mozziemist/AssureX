package com.example.assurex;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.events.DataItemsEvent;
import com.example.assurex.model.DataItem;
import com.example.assurex.sample.SampleDataProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    List<DataItem> dataItemList;
    List<DataItem> dataItemSampleList = SampleDataProvider.dataItemList;

    private AppDatabase db;

    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                int itemCount = db.dataItemDao().countItems();
                if (itemCount == 0) {
                    db.dataItemDao().insertAll(dataItemSampleList);
                    Log.i(TAG, "onCreate: data inserted");
                } else {
                    Log.i(TAG, "onCreate: data already exists");
                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                dataItemList = db.dataItemDao().getAll();
                EventBus.getDefault().post(new DataItemsEvent(dataItemList));
            }
        });

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataItemsEventHandler(DataItemsEvent event){
        dataItemList = event.getItemList();
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mDataSource.close();
    }


    public void LoginClicked(View view) {
        //startActivity(new Intent(getApplicationContext(), Speed.class));
        Intent intent = new Intent(getApplicationContext(), Speed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}
