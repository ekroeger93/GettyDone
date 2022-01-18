package com.example.checkListApp.time_management.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public class ListTimersParcel implements Parcelable {

    static ArrayList<Entry> entryTimerViewModels;

   public int[] listOfNumberValueTime;
   public int[] listOfAccumulatedTime;
   public String[] listOfCountDownTimers;

   public boolean[] listOfChecked;
   public String[] listOfText;
   public boolean[] listOfOnToggle;

    public String globalSetTimer = "";
    int activeTimeIndex= 0;


    public ListTimersParcel(ArrayList<Entry> list) {
        entryTimerViewModels = list;
    }

    public ArrayList<Entry> getEntryTimerViewModels() {
        return entryTimerViewModels;
    }

    public String getGlobalSetTimer() {
        return globalSetTimer;
    }

    public int getActiveTimeIndex() {
        return activeTimeIndex;
    }

    protected ListTimersParcel(Parcel in) {

        //Since Type array is being a bitch we'll grab properties instead

        int size = entryTimerViewModels.size();

        listOfNumberValueTime = new int[size];
        for(Entry n : entryTimerViewModels){
            int index = entryTimerViewModels.indexOf(n);
            listOfNumberValueTime[index] = n.numberValueTime;
        }

       // in.readIntArray(); DOES NOT WORK!!! WTF!
        in.createIntArray();//really motherfucker really!
        //Fucking cunts don't realize the grief this caused me

        listOfAccumulatedTime = new int[size];
        for(Entry n : entryTimerViewModels){
            int index = entryTimerViewModels.indexOf(n);
            listOfAccumulatedTime[index] = n.timeAccumulated;
        }
        in.createIntArray();


        listOfCountDownTimers = new String[size];

        for(Entry n : entryTimerViewModels){
            int index = entryTimerViewModels.indexOf(n);
            listOfCountDownTimers[index] = n.getCountDownTimer().getValue();
        }

        in.readStringArray(listOfCountDownTimers);


        listOfChecked = new boolean[size];
        for(Entry n : entryTimerViewModels){
            int index = entryTimerViewModels.indexOf(n);
            listOfChecked[index] = n.checkTemp;
        }
        in.createBooleanArray();

        listOfText = new String[size];

        for(Entry n : entryTimerViewModels){
            int index = entryTimerViewModels.indexOf(n);
            listOfText[index] = n.textEntry.getValue();
        }
        in.createStringArray();

        listOfOnToggle = new boolean[size];

        for(Entry n : entryTimerViewModels){
            int index = entryTimerViewModels.indexOf(n);
            listOfOnToggle[index]= n.onTogglePrimerTemp;
        }
        in.createBooleanArray();

        globalSetTimer = in.readString();
        activeTimeIndex = in.readInt();

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {


        parcel.writeIntArray(listOfNumberValueTime);
        parcel.writeIntArray(listOfAccumulatedTime);

        parcel.writeStringArray(listOfCountDownTimers);

        parcel.writeBooleanArray(listOfChecked);
        parcel.writeStringArray(listOfText);
        parcel.writeBooleanArray(listOfOnToggle);

        parcel.writeString(globalSetTimer);
        parcel.writeInt(activeTimeIndex);

    }

    public static final Creator<ListTimersParcel> CREATOR = new Creator<ListTimersParcel>() {



        @Override
        public ListTimersParcel createFromParcel(Parcel in) {
           // entryTimerViewModels = ListOfTimersFragment.getTimerLogsList();
            return new ListTimersParcel(in);
        }

        @Override
        public ListTimersParcel[] newArray(int size) {
            return new ListTimersParcel[size];
        }


    };

    @Override
    public int describeContents() {
        return 0;
    }






}
