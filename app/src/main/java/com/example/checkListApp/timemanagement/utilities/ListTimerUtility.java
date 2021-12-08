package com.example.checkListApp.timemanagement.utilities;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.checkListApp.timemanagement.ListTimersParcel;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entries.Entry;

import java.util.ArrayList;

public abstract class ListTimerUtility {

    public int activeProcessTimeIndex = 1;
    public  Entry currentActiveTime;

   @RequiresApi(api = Build.VERSION_CODES.O)
   public  void accumulation(ArrayList<Entry> list){

       for(int i = 0; i < list.size(); i++){

           if(i != 0 && list.get(i).numberValueTime !=0) {
               list.get(i).setTimeAcclimated(
                       list.get(i - 1).numberValueTime);
           }

       }

    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public ArrayList<Entry> generateEntryList(ListTimersParcel parcel){

        int size = MainFragment.getCheckList().size();// parcel.listOfCountDownTimers.length;

        ArrayList<Entry> list = new ArrayList<>(size);

       Log.d("serviceTest"," size: "+size);

        for(int i = 0 ; i < size; i++){

            Entry entry =
                    new Entry(
                            parcel.listOfText[i],
                            parcel.listOfChecked[i],
                            parcel.listOfCountDownTimers[i]
                    );

            entry.setTimeAccumulatedNonAdditive(parcel.listOfAccumulatedTime[i]);
            list.add(entry);

        }


        return list;

    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public  int getSummationTime(ArrayList<Entry> list){

       int sum = 0;

       for(Entry viewModel:list){

           viewModel.setNumberValueTime(viewModel.timeTemp);

           if(viewModel.numberValueTime !=0)
               sum+= new TimeState(viewModel.numberValueTime).getTimeNumberValue();

       }

       return sum;
    }

   public  Entry getNextActiveProcessTime(ArrayList<Entry> list){

        int size = list.size()-2;

        if(activeProcessTimeIndex < size){
            ++activeProcessTimeIndex;
            return list.get(activeProcessTimeIndex);
        }else{
            activeProcessTimeIndex = 0;
        }

        return list.get(size);
    }

   public  void revertTimeIndex(){ activeProcessTimeIndex = 1; }

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

