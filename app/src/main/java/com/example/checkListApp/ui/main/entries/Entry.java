package com.example.checkListApp.ui.main.entries;

import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.checkListApp.ui.main.EntryManagement.ListComponent.RecyclerAdapter;

@Entity(tableName = "Entries")
public class Entry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entryID")
    private int entryID = 0;
    @ColumnInfo(name = "textEntry")
    public MutableLiveData<String> textEntry = new MutableLiveData<String>("o") ;
    @ColumnInfo(name = "isChecked")
    public MutableLiveData<Boolean> checked = new MutableLiveData<>(false);
    @ColumnInfo(name = "timerLabel")
    public MutableLiveData<String> countDownTimer = new MutableLiveData<>("00:00:00");


    @Ignore
    private RecyclerAdapter.ViewHolder viewHolder;
    @Ignore
    public boolean swappable = true;

    @Ignore
    public String textTemp;
    @Ignore
    public boolean checkTemp;
    @Ignore
    public String timeTemp;

    public Entry(){
        textEntry = new MutableLiveData<>("o");
    }


    public Entry(Entry entry) {
        textEntry.postValue(entry.textEntry.getValue());
        checked.postValue(entry.checked.getValue());
        countDownTimer.postValue(entry.countDownTimer.getValue());
    }

    public Entry(String text, boolean isChecked, String timeText) {
        textEntry.setValue(text);
        checked.setValue(isChecked);
        countDownTimer.setValue(timeText);
    }

    public void setEntry(Entry entry){

        textEntry.setValue(entry.textEntry.getValue());
        checked.setValue(entry.checked.getValue());
        countDownTimer.setValue(entry.countDownTimer.getValue());
    }


    public void postEntryOptimized(String text, Boolean check, String time){

        textEntry.postValue(text);
        checked.postValue(check);
        countDownTimer.postValue(time);

    }




    public int getEntryID() {
        return this.entryID;
    }

    public void setEntryID(int ID) {
        this.entryID = ID;
    }

    public void setViewHolder(RecyclerAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }



    public RecyclerAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }





}
