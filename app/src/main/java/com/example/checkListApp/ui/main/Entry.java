package com.example.checkListApp.ui.main;

import android.graphics.Color;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.checkListApp.database.EntryRepository;
import com.example.checkListApp.ui.main.EntryManagement.ListComponent.RecyclerAdapter;

import java.util.ArrayList;

@Entity(tableName = "Entries")
public class Entry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entryID")
    private int entryID = 0;
    @ColumnInfo(name = "textEntry")
    public MutableLiveData<String> textEntry = new MutableLiveData<String>("o") ;
    @ColumnInfo(name = "isChecked")
    public MutableLiveData<Boolean> checked = new MutableLiveData<>(false);

    @Ignore
    private RecyclerAdapter.ViewHolder viewHolder;
    @Ignore
    public boolean swappable = true;

    @Ignore
    public String textTemp;
    @Ignore
    public boolean checkTemp;

    public Entry(){
    }

    public Entry(String text,boolean check){
        textEntry.setValue(text);
        checked.setValue(check);
    }

    public Entry(Entry entry) {
        textEntry.postValue(entry.textEntry.getValue());
        checked.postValue(entry.checked.getValue());
    }

    public void setEntry(Entry entry){

        textEntry.setValue(entry.textEntry.getValue());
        checked.setValue(entry.checked.getValue());
    }

    public void setEntryOptimized(String text, Boolean check){

        textEntry.setValue(text);
        checked.setValue(check);

    }

    public void postEntryOptimized(String text, Boolean check){

        textEntry.postValue(text);
        checked.postValue(check);

    }

    public void postEntry(Entry entry){
        textEntry.postValue(entry.textEntry.getValue());
        checked.postValue(entry.checked.getValue());
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
