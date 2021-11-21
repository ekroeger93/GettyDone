package com.example.checkListApp.ui.main.EntryManagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.collection.ArraySet;
import androidx.recyclerview.selection.SelectionTracker;

import com.example.checkListApp.R;
import com.example.checkListApp.database.EntryDao;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel.ButtonPanelToggle;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.MainViewModel;
import com.example.checkListApp.ui.main.EntryManagement.ListComponent.ToggleSwitchOrdering;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class EntryItemManager {


    private final Context context;
    private final MainViewModel mViewModel;
    private final Operator operator;

    ButtonPanelToggle buttonPanelToggle;


    public EntryItemManager(Context context, MainViewModel mainViewModel, Operator operator){

        this.context = context;
        this.mViewModel = mainViewModel;
        this.operator = operator;

    }

    public void setButtonPanelToggle(ButtonPanelToggle buttonPanelToggle) {
        this.buttonPanelToggle = buttonPanelToggle;
    }

    public void add(){

        Entry entry = new Entry();

        operator.adapter.notifyItemInserted(MainFragment.getCheckList().size() - 1);

        mViewModel.insertEntry(entry);

        operator.adapter.notifyItemChanged(MainFragment.getCheckList().size());

        operator.refreshSelection(false);

    }

    public void deleteSelected(SelectionTracker<Long> tracker){

        for(Entry entry : MainFragment.getCheckList()){

            try{


            if(tracker.isSelected(entry.getViewHolder().getKey()
            )){

                int position = entry.getViewHolder().getBindingAdapterPosition();

                mViewModel.deleteEntry(entry);
                operator.adapter.notifyItemRemoved(position);
                operator.adapter.notifyItemChanged(MainFragment.getCheckList().size());

            }
            }catch (NullPointerException e){
                e.printStackTrace();
            }


        }
        tracker.clearSelection();
    }



//TODO: fix memory leak here:

    public void sortSelected(SelectionTracker<Long> tracker){

     //   MainFragment.updateAllSelection();
//        assignSorted(outputSort());
        TaskSortEntries.executeAsync();



    }

    public static final class TaskSortEntries {

        private final static Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements


        public static void executeAsync() {
            executor.execute(() -> {

                MainFragment.updateAllSelection();
                assignSorted(outputSort());

                //Handle at main thread
//                new Handler(Looper.getMainLooper()).post(new Runnable () {
//                    @Override
//                    public void run () {
//                       assignSorted(initializeAndSwap());
//                    }
//                });

            });
        }


    }

    static ArrayList<Entry> outputSort(){

        int size =  MainFragment.getCheckList().size();

        ArrayList<Boolean> swappable = new ArrayList<>(size);
        ArrayList<Entry>swapList = new ArrayList<>(size);

       // swapEntry.clear();
        while(swappable.size() < size){

       // swapEntry.add(new Entry());
            swapList.add(new Entry());
            swappable.add(false);
        }


        for(ToggleSwitchOrdering.tNumber tNumber : MainFragment.toggleSwitchOrdering.listToOrder){

            try {
                int indexOf = MainFragment.toggleSwitchOrdering.listToOrder.indexOf(tNumber);
                int swapper = tNumber.number-1;//entry.getViewHolder().orderInt.getValue();

                Entry entry = MainFragment.getCheckList().get(indexOf+1);

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

    static void assignSorted(ArrayList<Entry> entries){

        for(Entry entry : entries){
            System.out.println(entry.textEntry.getValue());
        }


        for(Entry entry : entries){

            Log.d("arrayTest",entry.textEntry.getValue());

            try{
                int index = entries.indexOf(entry)+1;

                //Post seems to cause GC issues?
                MainFragment.getCheckList().get(index).postEntryOptimized(entry.textTemp,entry.checkTemp);

            }catch (IndexOutOfBoundsException | NullPointerException e){

            }

        }

        System.gc();
    }


    public void delete(){

        if(MainFragment.getCheckList().get(operator.selection-1).getClass() == Entry.class){

            View view = MainFragment.getCheckList().get(operator.selection-1).getViewHolder().itemView;

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

                            mViewModel.deleteEntry(MainFragment.getCheckList().get(operator.selection - 1));
                            operator.adapter.notifyItemRemoved(operator.selection - 1);
                            operator.adapter.notifyItemChanged(MainFragment.getCheckList().size());

                            buttonPanelToggle.toggleDisable();
                        }

            }).start();



    }}

    public void move(){

        boolean isMovingItem = operator.isMovingItem;
        int selection = operator.selection;
       // int oldMovePosition = operator.oldMovePosition;

        buttonPanelToggle.setOnClickListener(    new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move();
            }
        });


        if (!isMovingItem) {
            operator.movingItem = MainFragment.getCheckList().get(selection - 1);
            operator.oldMovePosition = selection - 1;
            operator.movingItem.setViewHolder(MainFragment.getCheckList().get(selection - 1).getViewHolder());
            operator.movingItem.getViewHolder().itemView.setBackgroundColor(Color.BLUE);
            operator.moveItem(operator.movingItem);
            buttonPanelToggle.toggleDisableToButton();
            ;

        }else{
            operator.adapter.notifyItemChanged(selection-1);
            operator.movingItem = null;
            buttonPanelToggle.toggleDisableToButton();
        }

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
                textHolderText, MainFragment.getCheckList().get(operator.selection - 1));

        operator.recyclerView.smoothScrollToPosition(operator.selection - 1);

      //  operator.recyclerView.scrollToPosition(operator.selection - 1 );

    }






}
