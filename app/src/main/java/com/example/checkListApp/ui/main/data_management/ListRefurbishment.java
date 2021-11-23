package com.example.checkListApp.ui.main.data_management;

import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.Spacer;
import com.example.checkListApp.ui.main.EntryManagement.ListComponent.ToggleSwitchOrdering;

import java.util.ArrayList;

public final class ListRefurbishment {

    static public ToggleSwitchOrdering toggleSwitchOrdering = new ToggleSwitchOrdering();


    static public void updateToggleOrdering(ArrayList<Entry> data){

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

        MainFragment.setCheckList(data);

    }

    public static void updateAllSelection(ArrayList<Entry> data){


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

        MainFragment.setCheckList(data);



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


}
