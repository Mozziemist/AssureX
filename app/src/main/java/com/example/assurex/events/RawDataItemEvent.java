package com.example.assurex.events;

import com.example.assurex.model.RawDataItem;

public class RawDataItemEvent {
    private RawDataItem dataItem;

    public RawDataItemEvent(RawDataItem dataItem) {
        this.dataItem = dataItem;
    }

    public RawDataItem getRawDataItem() {
        return dataItem;
    }

    public void setRawDataItem(RawDataItem dataItem) {
        this.dataItem = dataItem;
    }
}
