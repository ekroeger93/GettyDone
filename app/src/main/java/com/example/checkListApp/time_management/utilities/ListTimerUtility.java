package com.example.checkListApp.time_management.utilities;

import android.util.Log;

import com.example.checkListApp.time_management.parcel.ListTimersParcel;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public abstract class ListTimerUtility {

    public int activeProcessTimeIndex = 1;
    public Entry currentActiveTime;


   public void accumulation(ArrayList<Entry> list){

       for(int i = 0; i <= list.size()-2; i++){

           if(i != 0 ) {

               list.get(i).setTimeAcclimated(list.get(i - 1).timeAccumulated);
              }

       }

    }


   public ArrayList<Entry> generateEntryList(ListTimersParcel parcel){

      //  int size = MainFragment.getCheckList().size();// parcel.listOfCountDownTimers.length;

        //TODO: THIS May need to be fix
       int size = parcel.listOfChecked.length;

       ArrayList<Entry> list = new ArrayList<>(size);

       Log.d("serviceTest"," size: "+size);

        for(int i = 0 ; i < size; i++){

            Entry entry =
                    new Entry(
                            parcel.listOfText[i],
                            parcel.listOfChecked[i],
                            parcel.listOfCountDownTimers[i],
                            parcel.listOfOnToggle[i]
                    );

            entry.setTimeAccumulatedNonAdditive(parcel.listOfAccumulatedTime[i]);
            list.add(entry);

        }


        return list;

    }


   public  int getSummationTime(ArrayList<Entry> list){

       int sum = 0;

       for(Entry viewModel:list){

           viewModel.setNumberValueTime(viewModel.countDownTimer.getValue());

           int value = viewModel.getNumberValueTime();

               sum+= value;

       }

       return sum;
    }

    public Entry getCurrentActiveTime() {
        return currentActiveTime;
    }

    public  Entry getNextActiveProcessTime(ArrayList<Entry> list){

        int size = list.size()-1;

        if(activeProcessTimeIndex < size) {
            activeProcessTimeIndex++;

            return list.get(activeProcessTimeIndex);
        } else{
            activeProcessTimeIndex = 1;
        }

        return list.get(size-2);
    }

    public Entry getPreviousActiveProcessTime(ArrayList<Entry> list){


        if(activeProcessTimeIndex > 1) {
            activeProcessTimeIndex--;

            return list.get(activeProcessTimeIndex);
        } else{
            activeProcessTimeIndex = 1;
        }

        return list.get(1);

    }

   public  void revertTimeIndex(){ activeProcessTimeIndex = 1; }

   public int getActiveProcessTimeIndex(){return  activeProcessTimeIndex;}




}
