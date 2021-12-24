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


    public static void receiveParcelTime(ArrayList<Entry> data, Bundle bundle) {


        try {
            SetTimerFragmentArgs args = SetTimerFragmentArgs.fromBundle(bundle);
            timeParcel = args.getTimeParcel();

            int index = timeParcel.getTimeIndex()-1;
            String time = timeParcel.getTimeStringVal();

            data.get(index).countDownTimer.postValue(time);

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




}
