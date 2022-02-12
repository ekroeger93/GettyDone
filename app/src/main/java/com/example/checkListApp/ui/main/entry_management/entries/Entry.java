package com.example.checkListApp.ui.main.entry_management.entries;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.entry_management.list_component.RecyclerAdapter;

import java.util.ArrayList;
import java.util.Collection;

@Entity(tableName = "Entries")
public class Entry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entryID")
    private int entryID = 0;
    @ColumnInfo(name = "textEntry")
    public MutableLiveData<String> textEntry = new MutableLiveData<>("") ;
    @ColumnInfo(name = "isChecked")
    public MutableLiveData<Boolean> checked = new MutableLiveData<>(false);
    @ColumnInfo(name = "timerLabel")
    public MutableLiveData<String> countDownTimer = new MutableLiveData<>("00:00:00");

    //this is getting out of hand may dedicate a conjoined class complex
    @ColumnInfo(name="orderIndex")
    public MutableLiveData<Integer> orderIndex = new MutableLiveData<>(-1);
    @ColumnInfo(name="onTogglePrimer")
    public MutableLiveData<Boolean> onTogglePrimer = new MutableLiveData<>(false);
    @ColumnInfo(name="selectedAudio")
    public MutableLiveData<Integer> selectedAudio = new MutableLiveData<>(0);

    @ColumnInfo(name="globalCycle")
    static public int globalCycle = 0;
    @ColumnInfo(name="subListJson")
    public MutableLiveData<String> subListJson = new MutableLiveData<>("");
    @ColumnInfo(name="subListName")
    public MutableLiveData<String> subListName = new MutableLiveData<>("");


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
    public boolean onTogglePrimerTemp = false;

    @Ignore
    public int numberValueTime = 0;
    @Ignore
    public int timeAccumulated = 0;

    @Ignore
    public ArrayList<Entry> subCheckList = new ArrayList<>();

    @Ignore
    public boolean isSubEntry = false;
    @Ignore
    public int subNumberTimeValue = 0;
    @Ignore
    public int subLatestAccumulated = 0;


    @Ignore
    public MutableLiveData<String> activeTimerLabel = new MutableLiveData<>("");


    public Entry(){

    }


    public Entry(Entry entry) {
        textEntry.setValue(entry.textEntry.getValue());
        checked.setValue(entry.checked.getValue());
        countDownTimer.setValue(entry.countDownTimer.getValue());
        onTogglePrimer.setValue(entry.onTogglePrimer.getValue());
        selectedAudio.setValue(entry.selectedAudio.getValue());
        subListJson.setValue(entry.subListJson.getValue());
        subListName.setValue(entry.subListName.getValue());

        isSubEntry = entry.isSubEntry;
        subCheckList = entry.subCheckList;
        subNumberTimeValue = entry.subNumberTimeValue;
        subLatestAccumulated = entry.subLatestAccumulated;


        int numberTime = new TimeState(countDownTimer.getValue()).getTimeNumberValue();
        numberValueTime = numberTime;
        timeAccumulated = new TimeState(numberTime).getTimeNumberValue();//numberTime;//extractNumberValueTime(time);



    }

    public Entry(String text, boolean isChecked, String timeText, boolean onToggle) {


        textEntry.setValue(text);
        checked.setValue(isChecked);
        countDownTimer.setValue(timeText);
        onTogglePrimer.setValue(onToggle);

        textTemp = text;
        onTogglePrimerTemp = onToggle;

        setNumberValueTime(timeText);
        int numberTime = new TimeState(timeText).getTimeNumberValue();
        numberValueTime = numberTime;
        timeAccumulated = new TimeState(numberTime).getTimeNumberValue();//numberTime;//extractNumberValueTime(time);



    }

    public void refreshDataValues(){
        this.textEntry.setValue(textEntry.getValue());
        this.checked.setValue(checked.getValue());
        this.countDownTimer.setValue(countDownTimer.getValue());
    }

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

    public Entry(String text,
                 boolean isChecked,
                 String timeText,
                 int orderIndex,
                 boolean onToggle,
                 int audioSelect,
                 int globalCycle,
                 String jsonData,
                 String jsonNameData
    ) {

        textEntry.setValue(text);
        checked.setValue(isChecked);
        countDownTimer.setValue(timeText);
        onTogglePrimer.setValue(onToggle);
        selectedAudio.setValue(audioSelect);
        Entry.globalCycle = globalCycle;
        subListJson.setValue(jsonData);
        subListName.setValue(jsonNameData);

        this.orderIndex.setValue(orderIndex);

        textTemp = text;

        setNumberValueTime(timeText);
        int numberTime = new TimeState(timeText).getTimeNumberValue();
        numberValueTime = numberTime;
        timeAccumulated = new TimeState(numberTime).getTimeNumberValue();//numberTime;//extractNumberValueTime(time);



        //    numberValueTime = new TimeState(timeText).getTimeNumberValueDecimalTruncated();
    }


    public void updateActiveTimeLabel(int elapsedTimeNV){

        int timeRemainder = new TimeState().getValueAsTimeTruncated(timeAccumulated - elapsedTimeNV);

//        Log.d("timerLabelTest",""+timeRemainder);

        activeTimerLabel.postValue(new TimeState(timeRemainder).getTimeFormat());

    }

    public String getActiveTimeLabel(int elapsedTimeNV){

        int timeRemainder = new TimeState().getValueAsTimeTruncated(timeAccumulated - elapsedTimeNV);

        Log.d("timerLabelTest",elapsedTimeNV+" :"+ new TimeState(timeRemainder).getTimeFormat());
      return   new TimeState(timeRemainder).getTimeFormat();

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

    public void setEntry(Entry entry){


        textEntry.postValue(entry.textEntry.getValue());
        checked.postValue(entry.checked.getValue());
        countDownTimer.postValue(entry.countDownTimer.getValue());
        onTogglePrimer.postValue(entry.onTogglePrimer.getValue());
        selectedAudio.postValue(entry.selectedAudio.getValue());
        subListName.postValue(entry.subListName.getValue());
        isSubEntry = entry.isSubEntry;

        textTemp = entry.textTemp;

        setNumberValueTime(countDownTimer.getValue());

        setSubList(entry.subListJson.getValue()
                    , entry.subCheckList
                    , entry.subNumberTimeValue
                    , entry.subLatestAccumulated);


        swapIds(entry);
    }



    public void setSubList(String json, ArrayList<Entry> list, int subNumT, int subLateAcc){



        if(!isSubEntry && subListName.getValue().isEmpty()){
            unSetSubList();
        }else{
            this.subListJson.postValue(json);
            this.subCheckList = list;
            this.subNumberTimeValue = subNumT;
            this.subLatestAccumulated = subLateAcc;

            for(Entry n: list) {
                n.isSubEntry = true;
                n.setViewHolder(getViewHolder());
            }

//            setSubCheckList(list);


        }


    }

    public void unSetSubList(){

        this.subListJson.postValue("");
        this.subCheckList.clear();
        this.isSubEntry = false;
        this.subNumberTimeValue = 0;
        this.subLatestAccumulated = 0;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postEntryOptimized(String text, Boolean check, String time){

        textEntry.postValue(text);
        checked.postValue(check);
        countDownTimer.postValue(time);

        setNumberValueTime(time);

    }

    public void setSubCheckList(ArrayList<Entry> subCheckList) {

        this.subCheckList = subCheckList;
        this.isSubEntry = true;

        numberValueTime = new TimeState(countDownTimer.getValue()).getTimeNumberValue();
        countDownTimer.postValue( new TimeState(numberValueTime).getTimeFormat());


    }

    public int getSelectAudio(){
        return selectedAudio.getValue();
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

    public int getTimeAccumulated() {
        return timeAccumulated;
    }



    public void setTimeAcclimated(Entry entry ) {

        int initialTime = numberValueTime;
        int addedTime = entry.timeAccumulated;

         this.timeAccumulated = initialTime + addedTime;

        if(!entry.subCheckList.isEmpty()) {//if the last entry has a sublist

            int subLateAcc = entry.subLatestAccumulated;

            this.timeAccumulated = subLateAcc + initialTime;

        }

    }

    public void setTimeAcclimatedLastSub(Entry lastEntryProcessed){

        if(!subCheckList.isEmpty()){

       int subAcc = 0;

            subNumberTimeValue = numberValueTime + lastEntryProcessed.subLatestAccumulated;

            for(Entry n : subCheckList) {

                n.setNumberValueTime(n.countDownTimer.getValue());

                subAcc += n.numberValueTime;
                n.timeAccumulated = timeAccumulated + subAcc;
                subLatestAccumulated = n.timeAccumulated;

                Log.d("subListingTest","Sub acc:"+n.timeAccumulated);
            }

            subNumberTimeValue = numberValueTime + subLatestAccumulated;


//            subNumberTimeValue = subLatestAccumulated;

//            timeAccumulated = subNumberTimeValue;

            Log.d("subListingTest","parent: "+subAcc+" sL: "+subLatestAccumulated+" nV:"+numberValueTime+" acc: "+timeAccumulated);


        }

    }



    public MutableLiveData<String> getCountDownTimer() {
        return countDownTimer;
    }

    public void setTimeAccumulatedNonAdditive(int timeAccumulated) {
        this.timeAccumulated = timeAccumulated;

    }




}
