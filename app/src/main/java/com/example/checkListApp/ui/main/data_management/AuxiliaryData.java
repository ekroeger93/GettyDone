package com.example.checkListApp.ui.main.data_management;

import android.util.Log;

import com.example.checkListApp.settimer.SetTimerFragmentArgs;
import com.example.checkListApp.timemanagement.TimeParcel;
import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.Spacer;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.MainFragmentArgs;
import com.example.checkListApp.ui.main.MainViewModel;

import java.util.ArrayList;

public final class AuxiliaryData {

   static TimeParcel timeParcel;


    public static void receiveParcelTime(ArrayList<Entry> data, MainFragment mainFragment) {

        try {
            SetTimerFragmentArgs args = SetTimerFragmentArgs.fromBundle(mainFragment.getArguments());
            timeParcel = args.getTimeParcel();
            loadParcelTime(data);
        }catch (NullPointerException e){
            Log.d("timeParcel","error");
        }

    }

    public static TimeParcel getTimeParcel() {
        return timeParcel;
    }

    public static void loadParcelTime(ArrayList<Entry> data){

        data.get(timeParcel.getTimeIndex()).countDownTimer.postValue(timeParcel.getTimeStringVal());

        Log.d("timeParcel",":: " + data.get(timeParcel.getTimeIndex()).countDownTimer.getValue());

    }

    public static ArrayList<Entry> loadFile(ArrayList<Entry> data, MainViewModel mViewModel, MainFragment mainFragment){

        try{
            MainFragmentArgs args = MainFragmentArgs.fromBundle(mainFragment.getArguments());
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
                    Log.d("test",entry.textEntry.getValue());
                }
                loadedCheckList.add(0,new Spacer());
                loadedCheckList.add(new Spacer());

                data.addAll(loadedCheckList);

               // MainFragment.setCheckList(loadedCheckList);

                return loadedCheckList;

            }


        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }


        return null;
    }



}
