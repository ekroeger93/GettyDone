package com.example.checkListApp.ui.main.data_management;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.entries.Entry;
import com.example.checkListApp.ui.main.entries.Spacer;
import com.example.checkListApp.ui.main.entry_management.ListComponent.ToggleSwitchOrdering;

import java.util.ArrayList;

public final class ListUtility {

   public static int activeProcessTimeIndex = 1;
   public static Entry currentActiveTime;

    static public ToggleSwitchOrdering toggleSwitchOrdering = new ToggleSwitchOrdering();

    static public ArrayList<Entry> updateToggleOrdering(ArrayList<Entry> data){

        toggleSwitchOrdering.listToOrder.clear();

        int count = 0;
        for(Entry entry : data) {
            int index = data.indexOf(entry);


            if(entry instanceof Spacer) {}else {
                count++;
                toggleSwitchOrdering.listToOrder
                        .add(new ToggleSwitchOrdering.tNumber(
                                count, false));
            }
        }

        //MainFragment.setCheckList(data);

        return data;
    }

    public static ArrayList<Entry> updateAllSelection(ArrayList<Entry> data){

        for(Entry entry : data){
            try {
                if (entry instanceof Spacer) {
                } else {
                    int index= data.indexOf(entry);

                    for(ToggleSwitchOrdering.tNumber tNumber : toggleSwitchOrdering.listToOrder) {
                        int indexOf = toggleSwitchOrdering.listToOrder.indexOf(tNumber);
                        if(indexOf == index-1){
                            entry.getViewHolder().isSelected.postValue(tNumber.toggle);
                            entry.getViewHolder().orderInt.postValue(tNumber.number);
                            break;
                        }


                    }
                    entry.getViewHolder().selectionUpdate();
                }
            }catch (NullPointerException | IndexOutOfBoundsException e){

            }
        }

      //  MainFragment.setCheckList(data);

        return data;

    }

    public static void reInitializeAllSelection(ArrayList<Entry> data){

        System.out.println("clearing...");

        for(Entry entry : data){
            try {
                if (entry instanceof Spacer) {
                } else {

                    entry.getViewHolder().orderInt.postValue(-1);
                    entry.getViewHolder().isSelected.postValue(false);
                    entry.getViewHolder().selectOrder=0;
                    entry.swappable = true;
                    entry.getViewHolder().selectionUpdate();

                    //    System.out.print("["+entry.getViewHolder().orderInt.getValue()+']');
                }
            }catch (NullPointerException | IndexOutOfBoundsException e){

            }

        }

        toggleSwitchOrdering.listToOrder.clear();
        updateToggleOrdering(data);
        updateAllSelection(data);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getSummationTime(ArrayList<Entry> list){

        int sum = 0;

        for(Entry viewModel:list){

            viewModel.setNumberValueTime(viewModel.timeTemp);

            if(viewModel.numberValueTime !=0)
                sum+= new TimeState(viewModel.numberValueTime).getTimeNumberValue();

        }

        return sum;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Entry> accumulation(ArrayList<Entry> list){

//        for(int i = 0; i < list.size(); i++){
//
//            if(i != 0) {
//                list.get(i).setTimeAcclimated(
//                        list.get(i - 1).timeAccumulated);
//            }
//
//          //  Log.d("testTime", i+" = "+list.get(i).timeAccumulated);
//        }

        for(int i = 0; i < list.size(); i++){

            if(i != 0 && list.get(i).numberValueTime !=0) {
                list.get(i).setTimeAcclimated(
                        list.get(i - 1).numberValueTime);
            }

        }

        return list;
    }

    public static Entry getNextActiveProcessTime(ArrayList<Entry> list){

        if(activeProcessTimeIndex < list.size()-2){
            ++activeProcessTimeIndex;
            return list.get(activeProcessTimeIndex);
        }else{
            activeProcessTimeIndex = 0;
        }

        return list.get(activeProcessTimeIndex);
    }

    public static void revertTimeIndex(){
        activeProcessTimeIndex = 1;
    }


}
