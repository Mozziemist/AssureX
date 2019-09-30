package com.example.assurex.sample;

import com.example.assurex.model.DataItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//date
//hazard level
//avg speed
//top speed
//diagnostic messages
//top acceleration rate
//avg acceleration rate
//top deceleration rate
//avg deceleration rate

public class SampleDataProvider {
    public static List<DataItem> dataItemList;
    public static Map<String, DataItem> dataItemMap;

    static {
        dataItemList = new ArrayList<>();
        dataItemMap = new HashMap<>();

        addItem(new DataItem(null,"09-26-2019", "12:18:00", 65.1, 2.3));
        addItem(new DataItem(null,"09-26-2019", "12:18:30", 54.3, -6.8));
        addItem(new DataItem(null,"09-26-2019", "12:19:00", 32.8, -2.1));
        addItem(new DataItem(null,"09-26-2019", "12:19:30", 26.4, -0.1));
        addItem(new DataItem(null,"09-26-2019", "12:20:00", 29.5, 3.4));

    }
    /*
    this.date = calendar.get(Calendar.DAY_OF_MONTH) + "-" +
            calendar.get(Calendar.MONTH) + "-" +
            calendar.get(Calendar.YEAR);
        this.timeStamp = calendar.get(Calendar.HOUR_OF_DAY) + ":" +
            calendar.get(Calendar.MINUTE) + ":" +
            calendar.get(Calendar.SECOND);


     */
    private static void addItem(DataItem item) {
        dataItemList.add(item);
        dataItemMap.put(item.getTripId(), item);
    }
}
