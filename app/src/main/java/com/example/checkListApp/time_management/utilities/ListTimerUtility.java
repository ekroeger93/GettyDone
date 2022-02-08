package com.example.checkListApp.time_management.utilities;

import android.util.Log;

import com.example.checkListApp.time_management.parcel.ListTimersParcel;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entry_management.MainListTimeProcessHandler;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;

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

        int subAcc;

        try {
            checkList.get(1).timeAccumulated = checkList.get(1).numberValueTime;
        }catch (IndexOutOfBoundsException e){
            checkList.get(0).timeAccumulated = checkList.get(0).numberValueTime;
        }

        for(int i = 1; i <= checkList.size()-2; i++){

            Entry entry = checkList.get(i);

            entry.setNumberValueTime(entry.countDownTimer.getValue());

            if(!entry.subCheckList.isEmpty()){

                ArrayList<Entry> subList = entry.subCheckList;

                if( i != 1) {
                    entry.setTimeAcclimated(checkList.get(i - 1));
                }

                subAcc = 0;

                for(Entry n : subList) {

                    n.setNumberValueTime(n.countDownTimer.getValue());

                    subAcc += n.numberValueTime;
                    n.timeAccumulated = entry.timeAccumulated + subAcc;
                    entry.subLatestAccumulated = n.timeAccumulated;

                    Log.d("subListingTest","Sub acc:"+n.timeAccumulated);
                 }

                entry.subNumberTimeValue = entry.numberValueTime + entry.subLatestAccumulated;

                if(i == checkList.size()-2){
                    entry.setTimeAcclimatedLastSub();
                }

                Log.d("subListingTest"," Parent subNTV "+entry.subNumberTimeValue);


            }

            else{

                checkList.get(i).setTimeAcclimated(checkList.get(i - 1));

            }



        }




    }


   public  ArrayList<Entry> generateEntryList(ListTimersParcel parcel){

       int size = parcel.listOfChecked.length;

       ArrayList<Entry> list = new ArrayList<>(size);


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

//        int sum = 0;
//
//        for(Entry entry:list){
//
//            if (!(entry instanceof Spacer)) {
//
//            entry.setNumberValueTime(entry.countDownTimer.getValue());
//
//            int value;
//
//
//
//            if (entry.subNumberTimeValue == 0) {
//                value = entry.timeAccumulated;
//                Log.d("summationTest"," parVal "+value);
//            }else{
//                value = new TimeState(entry.subNumberTimeValue).timeTruncated();
//                Log.d("summationTest"," subVal "+value);
//            }
//
//
//
//                sum =value;
//
//                Log.d("summationTest","sum: "+sum);
//            }
//
//
//        }

       Entry lastEntry = list.get(list.size()-2);

        return new TimeState(lastEntry.timeAccumulated).timeTruncated();
    }



    public  Entry getNextActiveProcessTime(ArrayList<Entry> list){

        int size = list.size()-1;

        currentActiveTime = list.get(activeProcessTimeIndex);

        if(currentActiveTime.subCheckList.isEmpty() && !currentActiveTime.isSubEntry
        ){
            if(activeProcessTimeIndex < size) {
                activeProcessTimeIndex++;
                return list.get(activeProcessTimeIndex);

            } else{

                activeProcessTimeIndex = 1;
            }

        }else{

            if(parentEntry == null)
            parentEntry = list.get(activeProcessTimeIndex);

            if(subActiveProcessTimeIndex < parentEntry.subCheckList.size()){

                Entry subEntry = parentEntry.subCheckList.get(subActiveProcessTimeIndex);

//                    parentEntry.getViewHolder().textView.setText(subEntry.textEntry.getValue());
//                    parentEntry.getViewHolder().timerLabelText.setText(subEntry.getCountDownTimer().getValue());


                subActiveProcessTimeIndex++;

                return subEntry;

            }else{

                subActiveProcessTimeIndex = 0;

//                parentEntry.getViewHolder().textView.setText(parentEntry.textTemp);
                parentEntry.getViewHolder().checkOff();



                activeProcessTimeIndex++;


                parentEntry = null;

                 return list.get(activeProcessTimeIndex);
            }

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

   public  void revertTimeIndex(){
       activeProcessTimeIndex = 1; }

   public  void revertSubTimeIndex(){
       parentEntry = null;
        subActiveProcessTimeIndex = 0;}

   public int getActiveProcessTimeIndex(){return  activeProcessTimeIndex;}




}

