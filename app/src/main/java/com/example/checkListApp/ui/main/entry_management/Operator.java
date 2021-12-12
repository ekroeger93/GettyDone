package com.example.checkListApp.ui.main.entry_management;

import android.graphics.Color;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entry_management.ListComponent.RecyclerAdapter;
import com.example.checkListApp.ui.main.data_management.ListUtility;

public class Operator {

    //Passed In and out (needs assignment)
    protected RecyclerView recyclerView;
    protected RecyclerAdapter adapter;
    protected final ListUtility listUtility;
    private final MainFragment mainFragment;

    public Operator(MainFragment mainFragment){

        this.mainFragment= mainFragment;
        this.recyclerView = mainFragment.getRecyclerView();
        this.adapter = mainFragment.getAdapter();
        this.listUtility = mainFragment.getListUtility();
    }

    //Passed Out (gets assigned here)
     public RecyclerView.ViewHolder currentViewHolder;

     public Entry movingItem;
     public int oldMovePosition;
     public int selection;
     public boolean isMovingItem = false;

    public int getSelection(){

        int currentScroll;

        final float recyclerScrollCompute = MainFragment.recyclerScrollCompute;
        final float itemHeightPx = MainFragment.itemHeightPx;

        currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();

        int solveForPos;
        solveForPos = (int) Math.round((currentScroll - ((int) recyclerScrollCompute - (itemHeightPx / 2f))) / (itemHeightPx));
        selection  = Math.max(1,Math.min(solveForPos,mainFragment.getCheckList().size()-1));

        for (Entry e : mainFragment.getCheckList()) {

            try{
                if(!e.checked.getValue()){

                if (e.getViewHolder().getBindingAdapterPosition() == selection - 1) {
                    e.getViewHolder().itemView.setBackgroundColor(Color.RED);
                } else {
                    e.getViewHolder().itemView.setBackgroundColor(Color.parseColor("#95FF8D"));
                }
                }



     if (e.getViewHolder().getBindingAdapterPosition() == selection - 1)
         listUtility.updateAllSelection(mainFragment.getCheckList());




            }catch (NullPointerException a){
                a.printStackTrace();
            }

        }


        if(isMovingItem) moveItem(movingItem);


        return selection - 1;
    }

    public void refreshSelection(boolean decremented){

         final float recyclerScrollCompute = MainFragment.recyclerScrollCompute;
         final float itemHeightPx = MainFragment.itemHeightPx;

        int lastScroll, lastSolvedPosition, lastSelection;

        lastScroll =recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
        lastSolvedPosition = (int) Math.round((lastScroll - ((int) recyclerScrollCompute - (itemHeightPx / 2f))) / (itemHeightPx));

        if(decremented) {
            lastSelection = Math.max(1, Math.min(lastSolvedPosition, mainFragment.getCheckList().size() -2 ));
        }else{
            lastSelection = Math.max(1, Math.min(lastSolvedPosition, mainFragment.getCheckList().size() - 1 ));
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
                    && selection < mainFragment.getCheckList().size()
            ) {


                //TODO: FIX BUG WITH EXECUTION TIMER
                //TODO:FIX BUG PARCEL TIME NOT BEING UPDATED AFTER MOVE

                mainFragment.getCheckList().remove(movingItem);
                adapter.notifyItemRemoved(oldMovePosition);
                mainFragment.getCheckList().add(selection-1, movingItem);
                adapter.notifyItemInserted(selection-1);

                adapter.notifyItemMoved(oldMovePosition,selection-1);
                 adapter.notifyItemChanged(oldMovePosition);
                adapter.notifyItemChanged(selection-1);



                oldMovePosition = selection-1;

            }

        }
        catch (NullPointerException e){
            Log.d("Error","error");
        }

    }




}



