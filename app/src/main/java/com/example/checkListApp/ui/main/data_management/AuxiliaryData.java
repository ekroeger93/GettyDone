package com.example.checkListApp.ui.main.data_management;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.checkListApp.settimer.SetTimerFragmentArgs;
import com.example.checkListApp.timemanagement.parcel.TimeParcel;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;
import com.example.checkListApp.ui.main.MainFragmentArgs;
import com.example.checkListApp.ui.main.MainViewModel;

import java.util.ArrayList;
import java.util.Comparator;

public final class AuxiliaryData {

    static TimeParcel timeParcel;


    public static ArrayList<Entry> receiveParcelTime(ArrayList<Entry> data, Bundle bundle) {


        try {
            SetTimerFragmentArgs args = SetTimerFragmentArgs.fromBundle(bundle);
            timeParcel = args.getTimeParcel();

            int index = timeParcel.getTimeIndex()-1;
            String time = timeParcel.getTimeStringVal();


            //TODO: it works but I don't like it
//           String retainJson = timeParcel.getRetainedJsonData();
//           ArrayList<Entry> retainedState = JsonService.getJsonGeneratedArray(retainJson);
//           retainedState.get(index).countDownTimer.postValue(time);

            data.get(index).countDownTimer.postValue(time);

            Log.d("timerTest", "" + timeParcel.getTimeStringVal());

            return  data;
           // return retainedState;

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return data;

    }

    public  TimeParcel getTimeParcel() {
        return timeParcel;
    }

    public static  void loadParcelTime(ArrayList<Entry> data, int index, String time){


//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {

                data.get(index).countDownTimer.postValue(time);

//            }
//        });

        Log.d("timerTest",":: " + data.get(timeParcel.getTimeIndex()).countDownTimer.getValue());

    }

    public static ArrayList<Entry> loadFile( Bundle bundle){


            MainFragmentArgs args = MainFragmentArgs.fromBundle(bundle);

            ArrayList<Entry> loadedCheckList = JsonService.getJsonGeneratedArray(args.getJsonData());

            if(loadedCheckList != null){

                for(Entry entry : loadedCheckList){
                    entry.textEntry.setValue(entry.textEntry.getValue().replaceAll("\"" , ""));
                    entry.countDownTimer.setValue(entry.countDownTimer.getValue().replaceAll("\"",""));
                }


                return loadedCheckList;

            }



        return null;
    }



}
