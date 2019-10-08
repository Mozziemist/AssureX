package com.example.assurex.events;

import com.example.assurex.model.RawDataItem;

import java.util.List;

public class RawDataItemsEvent {
    private List<RawDataItem> itemList;

    public RawDataItemsEvent(List<RawDataItem> itemList) {
        this.itemList = itemList;
    }

    public List<RawDataItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<RawDataItem> itemList) {
        this.itemList = itemList;
    }
}
