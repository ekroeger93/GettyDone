package com.gettydone.app.ui.main.entry_management;

import android.os.Looper;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.gettydone.app.R;
import com.gettydone.app.ui.main.ColorHelper;
import com.gettydone.app.ui.main.MainUIDynamics;
import com.gettydone.app.ui.main.entry_management.entries.Entry;
import com.gettydone.app.ui.main.MainFragment;
import com.gettydone.app.ui.main.entry_management.list_component.RecyclerAdapter;
import com.gettydone.app.ui.main.data_management.ListUtility;

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
           //     if(!e.checked.getValue()){
                if(!adapter.toggleTracker) {

                    ColorHelper colorHelper = new ColorHelper(mainFragment.getContext());

                    if (e.getViewHolder().getBindingAdapterPosition() == selection - 1) {
                        e.getViewHolder().itemView.setBackgroundColor(colorHelper.Entry_ItemView_Selected);
                        listUtility.updateAllSelection(mainFragment.getCheckList());
                    } else {
                        e.getViewHolder().itemView.setBackgroundColor(colorHelper.Entry_ItemView);
                    }
                }else{
                    listUtility.updateAllSelection(mainFragment.getCheckList());
                }
            //    }



//     if (e.getViewHolder().getBindingAdapterPosition() == selection - 1)
//         listUtility.updateAllSelection(mainFragment.getCheckList());


            }catch (NullPointerException a){
                a.printStackTrace();
            }

        }


//        if(isMovingItem) moveItem(movingItem);


        return selection - 1;
    }

    public void setSelection(int selection){

        for (Entry e : mainFragment.getCheckList()) {

            try {
                //     if(!e.checked.getValue()){
                if (!adapter.toggleTracker) {

                    ColorHelper colorHelper = new ColorHelper(mainFragment.getContext());

                    if (e.getViewHolder().getBindingAdapterPosition() == selection - 1) {
                        e.getViewHolder().itemView.setBackgroundColor(colorHelper.Entry_ItemView_Selected);
                        listUtility.updateAllSelection(mainFragment.getCheckList());
                    } else {
                        e.getViewHolder().itemView.setBackgroundColor(colorHelper.Entry_ItemView);
                    }
                } else {
                    listUtility.updateAllSelection(mainFragment.getCheckList());
                }
                //    }


//     if (e.getViewHolder().getBindingAdapterPosition() == selection - 1)
//         listUtility.updateAllSelection(mainFragment.getCheckList());


            } catch (NullPointerException a) {
                a.printStackTrace();
            }
        }
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


//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                }, 100);

                //TODO: RESTRICT FROM SCROLLING WHILE ENTRIES ARE MOVING
                // moving too fast screws things up

                int oldPosition = mainFragment.getCheckList().indexOf(movingItem);

                mainFragment.getCheckList().remove(movingItem);
                mainFragment.getCheckList().add(selection-1, movingItem);

                Entry entrySwap = mainFragment.getCheckList().get(oldPosition);

                movingItem.swapIds(entrySwap);
                //entrySwap.swapIds(movingItem);

                adapter.notifyItemMoved(oldMovePosition,selection-1);
                adapter.notifyItemChanged(oldMovePosition);
                adapter.notifyItemChanged(selection-1);


                updateIndexes();

                oldMovePosition = selection-1;

            }

        }
        catch (NullPointerException e){
            Log.d("Error","error");
        }



    }

    public void moveEntry( int movingItemIndex, int placeIndex){

        Entry movingItem = mainFragment.getCheckList().get(movingItemIndex);
        Entry entrySwap =  mainFragment.getCheckList().get(placeIndex);

        mainFragment.getCheckList().get(movingItemIndex).setEntry(entrySwap);
        mainFragment.getCheckList().get(placeIndex).setEntry(movingItem);

        //TODO: relying on sanity check for subList
        //I'm having issues keeping the subListing json moving correctly
        //so relying on the sanityCheck to clear things up
        //too burned out atm to actually fix


        mainFragment.getSubListManager().loadSubLists();

//        adapter.notifyItemRangeChanged(movingItemIndex, placeIndex);

        adapter.notifyItemChanged(movingItemIndex,movingItem);
        adapter.notifyItemChanged(placeIndex,entrySwap);

        adapter.notifyItemMoved(movingItemIndex,placeIndex);




        MainUIDynamics.scrollPosition(placeIndex);

        updateIndexes();
        mainFragment.getSubListManager().sanityCheckSubList();

    }

    public void swapSubLists(int swap1, int swap2){

        Entry entrySwapOne = mainFragment.getCheckList().get(swap1);
        Entry entrySwapTwo = mainFragment.getCheckList().get(swap2);

        String jsonMovingSubList = entrySwapOne.subListJson.getValue();
        String jsonSwapSubList = entrySwapTwo.subListJson.getValue();


        if(entrySwapOne.isSubEntry && entrySwapTwo.isSubEntry){
            Log.d("subListingTest","RR");

            mainFragment.getSubListManager().setSubList(swap1,jsonSwapSubList);
            mainFragment.getSubListManager().setSubList(swap2,jsonMovingSubList);

            mainFragment.getCheckList().get(swap1)
                    .subListJson.postValue(jsonSwapSubList);

            mainFragment.getCheckList().get(swap2)
                    .subListJson.postValue(jsonMovingSubList);


        }

        if(!entrySwapOne.isSubEntry && entrySwapTwo.isSubEntry){

            mainFragment.getSubListManager().setSubList(swap1,entrySwapTwo.subListJson.getValue());
            mainFragment.getCheckList().get(swap2).unSetSubList();

            mainFragment.getCheckList().get(swap1)
                    .subListJson.postValue(entrySwapTwo.subListJson.getValue());

            entrySwapTwo.isSubEntry=false;

        }

        if(entrySwapOne.isSubEntry && !entrySwapTwo.isSubEntry){

            mainFragment.getSubListManager().setSubList(swap2,entrySwapOne.subListJson.getValue());
            mainFragment.getCheckList().get(swap1).unSetSubList();

            mainFragment.getCheckList().get(swap2)
                    .subListJson.postValue(entrySwapOne.subListJson.getValue());

            entrySwapOne.isSubEntry=false;
        }

    }

    public void moveItemUp(){

        int movingItemIndex = selection-1;
        int placeIndex = movingItemIndex - 1;

        if (movingItemIndex - 1 >= 1)
            moveEntry(movingItemIndex,placeIndex);



    }

    public void moveItemDown(){

        int movingItemIndex = selection-1;
        int placeIndex = movingItemIndex + 1;

        if (movingItemIndex + 1 < mainFragment.getCheckList().size()-1) {
            moveEntry(movingItemIndex, placeIndex);


        }


    }

    public void updateIndexes(){

        for(Entry n :mainFragment.getCheckList()) {

            if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
                n.orderIndex.setValue(mainFragment.getCheckList().indexOf(n));
            }else{
                n.orderIndex.postValue(mainFragment.getCheckList().indexOf(n));
            }
            mainFragment.getmViewModel().updateIndex(n, mainFragment.getCheckList().indexOf(n));
//            Log.d("orderingTest", ""+n.orderIndex.getValue());
        }


        mainFragment.getmViewModel().sortIndexes();
    }



}



