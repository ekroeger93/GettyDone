package com.example.checkListApp.ui.main.entry_management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.selection.SelectionTracker;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.ui.main.ColorHelper;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.button_panel.ButtonPanelToggle;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.MainViewModel;
import com.example.checkListApp.ui.main.entry_management.list_component.ToggleSwitchOrdering;
import com.example.checkListApp.ui.main.data_management.ListUtility;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EntryItemManager {


    private final Context context;
    private final MainViewModel mViewModel;
    private final Operator operator;
    private static ListUtility listUtility;

    private final MainFragment mainFragment;
    private final TaskSortEntries taskSortEntries;

    public static boolean isAddingEntry = false;

    ButtonPanelToggle buttonPanelToggle;


    static public Entry lastDeletedEntry = new Entry();
    static public int lastDeletedIndex = 1;



    public EntryItemManager(MainFragment mainFragment, Operator operator){

        this.context = mainFragment.getContext();
        this.mViewModel = mainFragment.getmViewModel();
        this.operator = operator;
        listUtility = operator.listUtility;

        this.mainFragment = mainFragment;
        taskSortEntries = new TaskSortEntries(mainFragment);

    }

    public void setButtonPanelToggle(ButtonPanelToggle buttonPanelToggle) {
        this.buttonPanelToggle = buttonPanelToggle;
    }

    public void add(){
//(String text, boolean isChecked, String timeText, int orderIndex) {


        if(MainActivity.preferenceHelper.hintDuplicateMessageIsActive()) {
            Snackbar snackbar = Snackbar.make(mainFragment.binding.main, "hold to duplicate entry",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction(
                    "Got it!", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("hintDuplicateEntry", false);
                            editor.commit();


                            snackbar.dismiss();
                        }
                    });

            snackbar.show();
        }
        Entry entry = new Entry(
                "",
                false,
                "00:00:05",
                mainFragment.getCheckList().size()-1,
                false,
                0,Entry.globalCycle,"","");


        mViewModel.insertEntry(entry);

        operator.adapter.notifyItemInserted(mainFragment.getCheckList().size());

       // operator.adapter.notifyItemChanged(mainFragment.getCheckList().size());

        isAddingEntry = true;

        operator.refreshSelection(false);

        //TODO BUG AFTER MOVING ITEM IT UNDOS THE NEW ARRANGEMENT
        //NOTE: SOMETHING PROCEEDING THIS MAY BE EFFECTING IT

        operator.updateIndexes();
    }

    public void addDuplicate(){

        Entry entry = new Entry(mainFragment.getCheckList().get(operator.selection-1));

//

        mainFragment.getCheckList().add(operator.selection-1,entry);

        mViewModel.loadEntry(entry);
//        mViewModel.insertEntry(entry);
//        mViewModel.updateEntry(entry);

        operator.adapter.notifyItemInserted(operator.selection-1);

//        operator.adapter.notifyDataSetChanged();
        operator.refreshSelection(false);

        //TODO BUG AFTER MOVING ITEM IT UNDOS THE NEW ARRANGEMENT
        //NOTE: SOMETHING PROCEEDING THIS MAY BE EFFECTING IT
        operator.updateIndexes();

//        mainFragment.enforceUpdateCheckList();


    }

    public void deleteSelected(SelectionTracker<Long> tracker){

        for(Entry entry : mainFragment.getCheckList()){

            try{


            if(tracker.isSelected(entry.getViewHolder().getKey()
            )){

                int position = entry.getViewHolder().getBindingAdapterPosition();

                mViewModel.deleteEntry(entry);
                operator.adapter.notifyItemRemoved(position);
                operator.adapter.notifyItemChanged(mainFragment.getCheckList().size());

            }
            }catch (NullPointerException e){
                e.printStackTrace();
            }


        }
        tracker.clearSelection();

        operator.updateIndexes();

    }

    public void undoLastDeletionSingle(){

        mViewModel.insertEntry(lastDeletedEntry);
        operator.adapter.notifyDataSetChanged();

        // operator.adapter.notifyItemChanged(mainFragment.getCheckList().size());

//        isAddingEntry = true;

        operator.refreshSelection(false);

        operator.updateIndexes();

    }



//TODO: fix memory leak here:

    
    public void sortSelected(SelectionTracker<Long> tracker){


//        TaskSortEntries.executeAsync();

        taskSortEntries.executeAsync();


//  notifyDataSetChanged();

    }

    public final class TaskSortEntries {

        private final  Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements

        MainFragment mainFragment;

        public TaskSortEntries(MainFragment mainFragment){

            this.mainFragment = mainFragment;
        }

        
        public void executeAsync() {

            executor.execute(() -> {

                listUtility.updateAllSelection(mainFragment.getCheckList());
                assignSorted(outputSort());

                //Handle at main thread
//                new Handler(Looper.getMainLooper()).post(new Runnable () {
//                    @Override
//                    public void run () {
//                       assignSorted(initializeAndSwap());
//                    }
//                });

            });

//           mainFragment.mainListTimeProcessHandler
//                   .subAccumulation(mainFragment.getCheckList());

            mainFragment.getSubListManager().loadSubLists();

        }


    }

     ArrayList<Entry> outputSort(){

        int size =  mainFragment.getCheckList().size();

        ArrayList<Boolean> swappable = new ArrayList<>(size);
        ArrayList<Entry>swapList = new ArrayList<>(size);

       // swapEntry.clear();
        while(swappable.size() < size){

       // swapEntry.add(new Entry());
            swapList.add(new Entry());
            swappable.add(false);
        }


        for(ToggleSwitchOrdering.tNumber tNumber : listUtility.toggleSwitchOrdering.listToOrder){

            try {
                int indexOf = listUtility.toggleSwitchOrdering.listToOrder.indexOf(tNumber);
                int swapper = tNumber.number-1;//entry.getViewHolder().orderInt.getValue();

                Entry entry = mainFragment.getCheckList().get(indexOf+1);

                swapList.set(indexOf,entry);
                swappable.set(indexOf,true);

                Entry entry1 = swapList.get(indexOf);
                Entry entry2 = swapList.get(swapper);

                if(entry1 != null && entry2 != null && swappable.get(swapper)){
                    swapList.set(swapper, entry1);
                    swapList.set(indexOf, entry2);
                }





            }catch (NullPointerException | ArrayIndexOutOfBoundsException e ){

            }


        }



        return swapList;

    }

    
     void assignSorted(ArrayList<Entry> entries){


        for(Entry entry : entries){

            Log.d("arrayTest",entry.textEntry.getValue());

            try{
                int index = entries.indexOf(entry)+1;

                //Post seems to cause GC issues?

                //TODO: performance issues
                //TODO: timeTemp not working used getValue instead

//                mainFragment.getCheckList().get(index)
//                        .postEntryOptimized(entry.textTemp,entry.checkTemp,entry.countDownTimer.getValue());



                mainFragment.getCheckList().get(index).setEntry(entry);

//                mainFragment.getCheckList().get(index).setSubList(
//                        entry.subListJson.getValue()
//                        ,entry.subCheckList
//                        ,entry.subNumberTimeValue
//                        ,entry.subLatestAccumulated);


            }catch (IndexOutOfBoundsException | NullPointerException e){

            }

        }

//        mainFragment.mainListTimeProcessHandler.subAccumulation();
         operator.updateIndexes();

    }


    public void delete(){

        if(mainFragment.getCheckList().get(operator.selection-1).getClass() == Entry.class){

            View view = mainFragment.getCheckList().get(operator.selection-1).getViewHolder().itemView;

            view.clearAnimation();

            view.setZ(-10f);
            view.animate()
                    .translationX(1000)
                    .scaleX(.5f)
                    .scaleY(.5f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            buttonPanelToggle.toggleDisable();
                        }

                        @Override
                public void onAnimationEnd(Animator animation) {

                            super.onAnimationStart(animation);

                            lastDeletedEntry= mainFragment.getCheckList().get(operator.selection - 1);
                            lastDeletedIndex =operator.selection -1;

                            mViewModel.deleteEntry(mainFragment.getCheckList().get(operator.selection - 1));
                            operator.adapter.notifyItemRemoved(operator.selection - 1);
                            operator.adapter.notifyItemChanged(mainFragment.getCheckList().size());

                            operator.updateIndexes();
                            buttonPanelToggle.toggleDisable();
                        }

            }).start();

            mainFragment.getMainUIDynamics().showUndoSnackBar();


    }}

    public void move(){

        boolean isMovingItem = operator.isMovingItem;
        int selection = operator.selection;
        int oldMovePosition = operator.oldMovePosition;


        buttonPanelToggle.setSubmitBtnOnClickListener(    new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move();
//                mainFragment.getCustomLayoutManager().setScrollEnabled(true);

            }
        });

        buttonPanelToggle.setMoveUpButtonOnClickListener(view -> {

//            operator.movingItem = mainFragment.getCheckList().get(selection - 1);
            operator.moveItemUp();

        });

        buttonPanelToggle.setMoveDownButtonOnClickListener( view -> {

//            operator.movingItem = mainFragment.getCheckList().get(selection -1);
            operator.moveItemDown();

        });


        if (!isMovingItem) {

            mainFragment.getMainUIDynamics().hideTimeExecuteBtn(true);

            operator.movingItem = mainFragment.getCheckList().get(selection - 1);
            operator.oldMovePosition = selection - 1;
         //   operator.movingItem.setViewHolder(mainFragment.getCheckList().get(selection - 1).getViewHolder());
            operator.movingItem.getViewHolder().itemView.setBackgroundColor(new ColorHelper(context).Entry_ItemView_Selected);
//            operator.moveItem(operator.movingItem);



            operator.updateIndexes();

         //   buttonPanelToggle.toggleDisableToButton();

        }else{

            mainFragment.getMainUIDynamics().hideTimeExecuteBtn(false);
            operator.movingItem = null;
        }

         buttonPanelToggle.toggleDisableMoveButtons();
        operator.isMovingItem = !isMovingItem;



    }

    public void edit(){



      //  if (operator.currentViewHolder.getAdapterPosition() == -1) {
            operator.currentViewHolder = operator.recyclerView.findViewHolderForAdapterPosition(operator.selection - 1);
     //   }


        TextView textHolderText = operator.currentViewHolder.itemView.findViewById(R.id.entryText);
        CustomEditText editHolderText = operator.currentViewHolder.itemView.findViewById(R.id.entryEditTxt);

        new DetectKeyboardBack(
                context,
                editHolderText,
                textHolderText, mainFragment.getCheckList().get(operator.selection - 1));

        operator.recyclerView.smoothScrollToPosition(operator.selection - 1);

      //  operator.recyclerView.scrollToPosition(operator.selection - 1 );

    }






}
