package com.example.checkListApp.ui.main.data_management;

import android.os.Bundle;
import android.util.Log;

import com.example.checkListApp.settimer.SetTimerFragmentArgs;
import com.example.checkListApp.timemanagement.parcel.TimeParcel;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;
import com.example.checkListApp.ui.main.MainFragmentArgs;
import com.example.checkListApp.ui.main.MainViewModel;

import java.util.ArrayList;

public final class AuxiliaryData {

   static TimeParcel timeParcel;


    public static void receiveParcelTime(ArrayList<Entry> data, Bundle bundle) {

        try {
            SetTimerFragmentArgs args = SetTimerFragmentArgs.fromBundle(bundle);
            timeParcel = args.getTimeParcel();

            int index = timeParcel.getTimeIndex();
            String time = timeParcel.getTimeStringVal();
            Log.d("timerTest",">>"+data);

            loadParcelTime(data, index, time);
            Log.d("timerTest",""+timeParcel.getTimeStringVal());

        }catch (NullPointerException e){
            Log.d("timeParcel","error");
        }

    }

    public static TimeParcel getTimeParcel() {
        return timeParcel;
    }

    public static void loadParcelTime(ArrayList<Entry> data, int index, String time){


//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {

                data.get(index).countDownTimer.postValue(time);

//            }
//        });

        Log.d("timerTest",":: " + data.get(timeParcel.getTimeIndex()).countDownTimer.getValue());

    }

    public static ArrayList<Entry> loadFile(ArrayList<Entry> data, MainViewModel mViewModel, Bundle bundle){


            MainFragmentArgs args = MainFragmentArgs.fromBundle(bundle);

            Log.d("checkListTest","args: "+args.getJsonData());

            ArrayList<Entry> loadedCheckList = JsonService.getJsonGeneratedArray(args.getJsonData());

            if(loadedCheckList != null){

                for(Entry entry : loadedCheckList){
                    entry.textEntry.setValue(entry.textEntry.getValue().replaceAll("\"" , ""));
                    entry.countDownTimer.setValue(entry.countDownTimer.getValue().replaceAll("\"",""));
                }

                if(data.size() > 0){
                    mViewModel.deleteAllEntries(data);
                }

                for(Entry entry : loadedCheckList) {
                    mViewModel.loadEntry(entry);
                   // Log.d("checkListTest",entry.textEntry.getValue());
                }
                loadedCheckList.add(0,new Spacer());
                loadedCheckList.add(new Spacer());

                data.addAll(loadedCheckList);


                return loadedCheckList;

            }



        return data;
    }



}
