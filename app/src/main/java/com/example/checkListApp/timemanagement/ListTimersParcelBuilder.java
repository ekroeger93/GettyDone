package com.example.checkListApp.timemanagement;

import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public class ListTimersParcelBuilder {

    ListTimersParcel listTimersParcel;

    public ListTimersParcelBuilder(ArrayList<Entry> list){
        listTimersParcel = new ListTimersParcel(list);
    }


    public ListTimersParcelBuilder setEntryViewModelList(ArrayList<Entry> list) {
        ListTimersParcel.entryTimerViewModels = list;
        return this;
    }

    public ListTimersParcelBuilder setGlobalTimer(String time) {
        listTimersParcel.globalSetTimer = time;
        return this;
    }

    public ListTimersParcelBuilder setIndexActive(int index) {
        listTimersParcel.activeTimeIndex = index;
        return this;
    }

    public ListTimersParcel build() {
        return listTimersParcel;
    }
}
