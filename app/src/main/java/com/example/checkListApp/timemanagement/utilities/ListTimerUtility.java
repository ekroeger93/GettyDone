package com.example.checkListApp.timemanagement.utilities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.checkListApp.timemanagement.ListTimersParcel;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.entries.Entry;

import java.util.ArrayList;

public abstract class ListTimerUtility {

    int activeProcessTimeIndex;

   public void accumulation(ArrayList<Entry> list){

        for(int i = 0; i < list.size(); i++){

            if(i != 0) {
                list.get(i).setTimeAcclimated(
                        list.get(i - 1).timeAccumulated);
            }

        }

    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public ArrayList<Entry> generateEntryList(ListTimersParcel parcel){

        int size = parcel.listOfCountDownTimers.length;

        ArrayList<Entry> list = new ArrayList<>(size);

        for(int i = 0 ; i < size; i++){

            Entry entry =
                    new Entry(
//                            parcel.listOfCountDownTimers[i],
//                            parcel.listOfNumberValueTime[i]
                    parcel.listOfText[i],parcel.listOfChecked[i],parcel.listOfCountDownTimers[i]
                    );

            entry.setTimeAccumulatedNonAdditive(parcel.listOfAccumulatedTime[i]);

            list.add(entry);

        }

        return list;

    }

   public int getSummationTime(ArrayList<Entry> list){

        int sum = 0;

        for(Entry viewModel:list){

            if(viewModel.numberValueTime !=0)
                sum+=  new TimeState(viewModel.numberValueTime).getTimeNumberValue();

        }

        return sum;
    }

   public Entry getNextActiveProcessTime(ArrayList<Entry> list){

        int size = list.size()-1;

        if(activeProcessTimeIndex < size){
            ++activeProcessTimeIndex;
            return list.get(activeProcessTimeIndex);
        }else{
            activeProcessTimeIndex = 0;
        }

        return list.get(size);
    }

   public void revertTimeIndex(){ activeProcessTimeIndex = 0; }

   public int getActiveProcessTimeIndex(){return  activeProcessTimeIndex;}

//   public ArrayList<Entry> generateListOfTimers(ArrayList<String> listTime, ArrayList<Integer> listNumTime){
//
//        ArrayList<Entry> timerLogsList = new ArrayList<>(5);
//
//        for(int i =0; i<listNumTime.size(); i++){
//            String time = listTime.get(i);
//            Integer numberTime = listNumTime.get(i);
//            timerLogsList.add(new Entry(time,numberTime));
//        }
//
//        return  timerLogsList;
//    }



}

