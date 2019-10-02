package com.example.assurex.events;

import com.example.assurex.model.DataItem;

import java.util.List;

public class DataItemsEvent {
    private List<DataItem> itemList;

    public DataItemsEvent(List<DataItem> itemList) {
        this.itemList = itemList;
    }

    public List<DataItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<DataItem> itemList) {
        this.itemList = itemList;
    }
}
