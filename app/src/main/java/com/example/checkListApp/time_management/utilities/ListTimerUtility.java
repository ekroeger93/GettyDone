package com.example.checkListApp.time_management.utilities;

import android.util.Log;

import com.example.checkListApp.time_management.parcel.ListTimersParcel;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.entry_management.MainListTimeProcessHandler;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ListTimerUtility {

     public int activeProcessTimeIndex = 1;
     public int subActiveProcessTimeIndex = 0 ;

    public volatile Entry currentActiveTime;
    public  Entry parentEntry = null;

    public ListTimerUtility(){

    }


    public void accumulation(ArrayList<Entry> checkList){

        int subAcc = 0;

        checkList.get(1).timeAccumulated = checkList.get(1).numberValueTime;

        for(int i = 1; i <= checkList.size()-2; i++){

            Entry entry = checkList.get(i);

            if(!entry.subCheckList.isEmpty()){

                ArrayList<Entry> subList = entry.subCheckList;

                if( i != 1) {
                    entry.setTimeAcclimated(checkList.get(i - 1));
                }

                subAcc = 0;

                for(Entry n : subList) {
                    subAcc += n.numberValueTime;
                    n.timeAccumulated = entry.timeAccumulated + subAcc;

                    entry.subLatestAccumulated = n.timeAccumulated;
                    Log.d("subListingTest",entry.timeAccumulated+"  subAcc: "+n.timeAccumulated+" fm: "+subAcc);
                }
                entry.subNumberTimeValue = entry.numberValueTime + subAcc;


            }

            else{

                checkList.get(i).setTimeAcclimated(checkList.get(i - 1));

            }


            Log.d("subListingTest",entry.textTemp+" * Acc: "+entry.timeAccumulated);

        }


    }


   public  ArrayList<Entry> generateEntryList(ListTimersParcel parcel){

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

   public int getSummationTime(ArrayList<Entry> list){

        int sum = 0;

        for(Entry entry:list){

            entry.setNumberValueTime(entry.countDownTimer.getValue());

            int value;

            if (entry.subNumberTimeValue == 0) {
                value = entry.getNumberValueTime();
            }else{
                value = entry.subNumberTimeValue;
            }
            sum+= value;

        }

        return sum;
    }

    public Entry getCurrentActiveTime() {
        return currentActiveTime;
    }

    public  Entry getNextActiveProcessTime(ArrayList<Entry> list){

        int size = list.size()-1;

        currentActiveTime = list.get(activeProcessTimeIndex);

        if(currentActiveTime.subCheckList.isEmpty() && !currentActiveTime.isSubEntry
        ){

            Log.d("subListingTest","standard!");

            if(activeProcessTimeIndex < size) {
                activeProcessTimeIndex++;

                return list.get(activeProcessTimeIndex);
            } else{

                Log.d("subListTest","reverted");
                activeProcessTimeIndex = 1;
            }

        }else{

            Log.d("subListingTest","sublist!");
            if(parentEntry == null)
            parentEntry = list.get(activeProcessTimeIndex);

            if(subActiveProcessTimeIndex < parentEntry.subCheckList.size()){


                Entry subEntry = parentEntry.subCheckList.get(subActiveProcessTimeIndex);

//                list.get(activeProcessTimeIndex).setEntry( subEntry);

                subActiveProcessTimeIndex++;

                return subEntry;

            }else{

                Log.d("subListingTest","finish Sub list");
                subActiveProcessTimeIndex = 0;

                list.get(activeProcessTimeIndex)
                        .getViewHolder().textView.setText(parentEntry.textTemp);

                activeProcessTimeIndex++;
                parentEntry = null;

                Log.d("subListingTest",""+activeProcessTimeIndex);
                return list.get(activeProcessTimeIndex);
            }

        }

        Log.d("subListingTest","failed!");
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

   public  void revertTimeIndex(){
       Log.d("subListingTest","called");
       activeProcessTimeIndex = 1; }

   public  void revertSubTimeIndex(){
       parentEntry = null;
        subActiveProcessTimeIndex = 0;}

   public int getActiveProcessTimeIndex(){return  activeProcessTimeIndex;}




}

