package com.example.checkListApp.ui.main.data_management;

import com.example.checkListApp.time_management.utilities.ListTimerUtility;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;
import com.example.checkListApp.ui.main.entry_management.list_component.ToggleSwitchOrdering;

import java.util.ArrayList;

public class ListUtility  extends ListTimerUtility {


     public ToggleSwitchOrdering toggleSwitchOrdering = new ToggleSwitchOrdering();

     public ArrayList<Entry> updateToggleOrdering(ArrayList<Entry> data){

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

    public  ArrayList<Entry> updateAllSelection(ArrayList<Entry> data){

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

    public  void reInitializeAllSelection(ArrayList<Entry> data){

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




}
