package com.gettydone.app.ui.main.data_management;

import android.os.Bundle;
import android.util.Log;

import com.gettydone.app.set_timer.SetTimerFragmentArgs;
import com.gettydone.app.time_management.parcel.TimeParcel;
import com.gettydone.app.ui.main.entry_management.entries.Entry;
import com.gettydone.app.ui.main.MainFragmentArgs;

import java.util.ArrayList;

public final class AuxiliaryData {

    static TimeParcel timeParcel;


    public static void receiveParcelTime(ArrayList<Entry> data, Bundle bundle) {


        try {
            SetTimerFragmentArgs args = SetTimerFragmentArgs.fromBundle(bundle);
            timeParcel = args.getTimeParcel();

            int index = timeParcel.getTimeIndex()-1;
            String time = timeParcel.getTimeStringVal();
            data.get(index).countDownTimer.postValue(time);
            data.get(index).onTogglePrimer.postValue(timeParcel.getOnTogglePrimer());
            data.get(index).selectedAudio.postValue(timeParcel.getSelectAudio());

            Log.d("timerTest", "" + timeParcel.getTimeStringVal());

          ///  return  data;

        }catch (NullPointerException e){
            e.printStackTrace();
        }

       // return data;

    }

    public static ArrayList<Entry> loadFile(Bundle bundle){


            MainFragmentArgs args = MainFragmentArgs.fromBundle(bundle);
            ArrayList<Entry> loadedCheckList;

            try {
              loadedCheckList = JsonService.getJsonGeneratedArray(args.getJsonData());
            }catch (NullPointerException e){
                return  null;
            }

        for(Entry entry : loadedCheckList){
            entry.textEntry.setValue(entry.textEntry.getValue().replaceAll("\"" , ""));
            entry.countDownTimer.setValue(entry.countDownTimer.getValue().replaceAll("\"",""));
        }

        return loadedCheckList;


    }


    public static ArrayList<Entry> loadFile(String file){

        ArrayList<Entry> loadedCheckList;

        try {
            loadedCheckList = JsonService.getJsonGeneratedArray(file);
        }catch (NullPointerException e){
            return  null;
        }

        for(Entry entry : loadedCheckList){
            entry.textEntry.setValue(entry.textEntry.getValue().replaceAll("\"" , ""));
            entry.countDownTimer.setValue(entry.countDownTimer.getValue().replaceAll("\"",""));
        }

        return loadedCheckList;


    }


}
