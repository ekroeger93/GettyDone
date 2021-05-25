package com.example.checkListApp.ui.main.EntryManagement;

import android.graphics.Color;
import android.util.Log;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.RecyclerAdapter;

import java.util.Objects;

public class Operator {


    //Passed In and out (needs assignment)
    public RecyclerView recyclerView;
    public RecyclerAdapter adapter;


    public Operator(RecyclerView recyclerView, RecyclerAdapter adapter){
        this.recyclerView = recyclerView; this.adapter = adapter;
    }


    //Passed Out (gets assigned here)
     public RecyclerView.ViewHolder currentViewHolder;

     public Entry movingItem;
     public int oldMovePosition;
     public int selection;

     public boolean isMovingItem = false;


    public void getSelection(){

        int currentScroll;

        final float recyclerScrollCompute = MainFragment.recyclerScrollCompute;
        final float itemHeightPx = MainFragment.itemHeightPx;

        currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();

        int solveForPos;
        solveForPos = (int) Math.round((currentScroll - ((int) recyclerScrollCompute - (itemHeightPx / 2f))) / (itemHeightPx));
        selection  = Math.max(1,Math.min(solveForPos,MainFragment.getCheckList().size()-1));

        for (Entry e : MainFragment.getCheckList()) {

            try{
                if(!e.checked.getValue()){

                if (e.getViewHolder().getBindingAdapterPosition() == selection - 1) {
                    e.getViewHolder().itemView.setBackgroundColor(Color.RED);
                } else {
                    e.getViewHolder().itemView.setBackgroundColor(Color.parseColor("#95FF8D"));
                }
                }


                //e.getViewHolder().selectionUpdate();

//                MainFragment.updateAllSelection();

                if (e.getViewHolder().getBindingAdapterPosition() == selection - 1) {

                  //  if(e.getViewHolder().isSelected.getValue()){
                    MainFragment.updateAllSelection();
               //}

                }

            }catch (NullPointerException a){
                a.printStackTrace();
            }

        }


        if(isMovingItem) moveItem(movingItem);


    }

    public void refreshSelection(boolean decremented){

         final float recyclerScrollCompute = MainFragment.recyclerScrollCompute;
         final float itemHeightPx = MainFragment.itemHeightPx;

        int lastScroll, lastSolvedPosition, lastSelection;

        lastScroll =recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
        lastSolvedPosition = (int) Math.round((lastScroll - ((int) recyclerScrollCompute - (itemHeightPx / 2f))) / (itemHeightPx));

        if(decremented) {
            lastSelection = Math.max(1, Math.min(lastSolvedPosition, MainFragment.getCheckList().size() -2 ));
        }else{
            lastSelection = Math.max(1, Math.min(lastSolvedPosition, MainFragment.getCheckList().size() - 1 ));
        }




        currentViewHolder = recyclerView.findViewHolderForLayoutPosition(lastSelection-1);
        selection = lastSelection;

        //adapter.setSelectedView(currentViewHolder);
        adapter.highlightSelected(currentViewHolder);



    }


    public void moveItem(Entry movingItem){

        try {
            if (oldMovePosition != selection-1
                    && selection > 0
                    && selection < MainFragment.getCheckList().size()
            ) {


                MainFragment.getCheckList().remove(movingItem);
                adapter.notifyItemRemoved(oldMovePosition);
                MainFragment.getCheckList().add(selection-1, movingItem);
                adapter.notifyItemInserted(selection-1);

                oldMovePosition = selection-1;

            }

        }
        catch (NullPointerException e){
            Log.d("Error","error");
        }

    }


    static StringBuilder memorySwap = new StringBuilder();
    static StringBuilder memorySwap2= new StringBuilder();

    static public class TaskSortItem2 implements Runnable{

        Entry copyEntry, copyEntrySwap, refEntry, refEntrySwap;
        Adapter adapter;

        TaskSortItem2(Adapter adapter, Entry swap1, Entry swap2, Entry entry1, Entry refEntrySwap){
            this.adapter = adapter;
            this.copyEntry = swap1;
            this.copyEntrySwap = swap2;
            this.refEntry = entry1;
            this.refEntrySwap = refEntrySwap;

        }

        @Override
        public void run() {

            final String textEntryOne = Objects.requireNonNull(copyEntry.textEntry.getValue());
            final String textEntryTwo = copyEntrySwap.textEntry.getValue();

            final Boolean isCheckOne = copyEntry.checked.getValue();
            final Boolean isCheckTwo = copyEntrySwap.checked.getValue();


//            if (!refEntrySwap.swappable){
//                refEntrySwap.textEntry.postValue(memorySwap.toString());
//                memorySwap = new StringBuilder();;
//            }


          if(refEntry.swappable && refEntrySwap.swappable)
           {
              // memorySwap = new StringBuilder();;

               refEntry.textEntry.postValue(textEntryTwo.toString());
               refEntrySwap.textEntry.postValue(textEntryOne.toString());

               refEntry.checked.postValue(isCheckTwo);
               refEntrySwap.checked.postValue(isCheckOne);

           refEntry.swappable = false;
           refEntrySwap.swappable = false;

           memorySwap.append(textEntryTwo);

           }



//            memorySwap.append(textEntryOne);
//            memorySwap2.append(textEntryTwo);


    }




    }



}



