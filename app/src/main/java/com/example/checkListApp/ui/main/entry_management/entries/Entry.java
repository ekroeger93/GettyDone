package com.example.checkListApp.ui.main.entry_management.entries;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.example.checkListApp.ui.main.entry_management.ListComponent.RecyclerAdapter;

import java.util.ArrayList;

@Entity(tableName = "Entries")
public class Entry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entryID")
    private int entryID = 0;
    @ColumnInfo(name = "textEntry")
    public MutableLiveData<String> textEntry = new MutableLiveData<>("o") ;
    @ColumnInfo(name = "isChecked")
    public MutableLiveData<Boolean> checked = new MutableLiveData<>(false);
    @ColumnInfo(name = "timerLabel")
    public MutableLiveData<String> countDownTimer = new MutableLiveData<>("00:00:00");
    @ColumnInfo(name="orderIndex")
    public MutableLiveData<Integer> orderIndex = new MutableLiveData<>(-1);


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

    @Ignore
    public int numberValueTime = 0;
    @Ignore
    public int timeAccumulated = 0;

    public Entry(){

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Entry(Entry entry) {
        textEntry.postValue(entry.textEntry.getValue());
        checked.postValue(entry.checked.getValue());
        countDownTimer.postValue(entry.countDownTimer.getValue());


        int numberTime = new TimeState(countDownTimer.getValue()).getTimeNumberValue();
        numberValueTime = numberTime;
        timeAccumulated = new TimeState(numberTime).getTimeNumberValue();//numberTime;//extractNumberValueTime(time);


    }

    public void refreshDataValues(){
        this.textEntry.setValue(textEntry.getValue());
        this.checked.setValue(checked.getValue());
        this.countDownTimer.setValue(countDownTimer.getValue());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Entry(String text, boolean isChecked, String timeText) {

        textEntry.setValue(text);
        checked.setValue(isChecked);
        countDownTimer.setValue(timeText);

        textTemp = text;

        setNumberValueTime(timeText);
        int numberTime = new TimeState(timeText).getTimeNumberValue();
        numberValueTime = numberTime;
        timeAccumulated = new TimeState(numberTime).getTimeNumberValue();//numberTime;//extractNumberValueTime(time);



        //    numberValueTime = new TimeState(timeText).getTimeNumberValueDecimalTruncated();
    }

    public Entry(String text, boolean isChecked, String timeText, int orderIndex) {

        textEntry.setValue(text);
        checked.setValue(isChecked);
        countDownTimer.setValue(timeText);

        this.orderIndex.setValue(orderIndex);

        textTemp = text;

        setNumberValueTime(timeText);
        int numberTime = new TimeState(timeText).getTimeNumberValue();
        numberValueTime = numberTime;
        timeAccumulated = new TimeState(numberTime).getTimeNumberValue();//numberTime;//extractNumberValueTime(time);



        //    numberValueTime = new TimeState(timeText).getTimeNumberValueDecimalTruncated();
    }


    public int getNumberValueTime(){
        String timeText;

        try {

            timeText = countDownTimer.getValue().replace(":", "").trim();
            return new TimeState(Integer.parseInt(timeText.replaceAll("\"",""))).getTimeNumberValue();

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return 0;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setNumberValueTime(String timeText){


        try {
            timeText = timeText.replace(":", "").trim();
            numberValueTime = new TimeState(Integer.parseInt(timeText.replaceAll("\"",""))).getTimeNumberValueDecimalTruncated();

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }


    public void swapIds(Entry entry){

        int tempId = entryID;

        entryID = entry.entryID;
        entry.entryID = tempId;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEntry(Entry entry){

//        entryID = entry.getEntryID();
        textEntry.postValue(entry.textEntry.getValue());
        checked.postValue(entry.checked.getValue());
        countDownTimer.postValue(entry.countDownTimer.getValue());

        textTemp = entry.textTemp;

        setNumberValueTime(countDownTimer.getValue());

        swapIds(entry);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postEntryOptimized(String text, Boolean check, String time){

        textEntry.postValue(text);
        checked.postValue(check);
        countDownTimer.postValue(time);

        setNumberValueTime(time);

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


    public boolean timeElapsed(int time){
        return  (timeAccumulated == time);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTimeAcclimated(int timeAcclimated) {

    int original = new TimeState(numberValueTime).getTimeNumberValue();
    int addedTime  = new TimeState(timeAcclimated).getTimeNumberValue();

    this.timeAccumulated = original + addedTime;

}


    public MutableLiveData<String> getCountDownTimer() {
        return countDownTimer;
    }

    public void setTimeAccumulatedNonAdditive(int timeAccumulated) {
        this.timeAccumulated = timeAccumulated;

    }




}
