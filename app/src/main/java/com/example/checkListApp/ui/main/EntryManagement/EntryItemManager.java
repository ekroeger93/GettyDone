package com.example.checkListApp.ui.main.EntryManagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.recyclerview.selection.SelectionTracker;

import com.example.checkListApp.R;
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



    private final ArrayList<Entry> swapEntry = new ArrayList<>();

    public void sortSelected(SelectionTracker<Long> tracker){

        MainFragment.updateAllSelection();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {


//            initialize();
//            swapping();

            initializeAndSwap();

            new Handler(Looper.getMainLooper()).post(new Runnable () {
                @Override
                public void run () {
                    assignSorted();
                }
            });

        });

        executorService.shutdown();


    }

    void initialize(){

        int size = MainFragment.getCheckList().size();

        swapEntry.clear();

        while(swapEntry.size() < size)swapEntry.add(new Entry());


        for(ToggleSwitchOrdering.tNumber tNumber : MainFragment.toggleSwitchOrdering.listToOrder){


            int indexOf = MainFragment.toggleSwitchOrdering.listToOrder.indexOf(tNumber);
            Entry entry = MainFragment.getCheckList().get(indexOf+1);

            swapEntry.get(indexOf).postEntry(entry);

                   //  System.out.println(entry.checked.getValue());

        }

    }

    void swapping(){
        int size =  MainFragment.getCheckList().size();
        ArrayList<Boolean> swappable = new ArrayList<>();

        while(swappable.size() < size)swappable.add(false);

        for(ToggleSwitchOrdering.tNumber tNumber : MainFragment.toggleSwitchOrdering.listToOrder){

            try {
                int indexOf = MainFragment.toggleSwitchOrdering.listToOrder.indexOf(tNumber);
                int swapper = tNumber.number-1;//entry.getViewHolder().orderInt.getValue();

                swappable.set(indexOf,true);

                Entry entry1 = swapEntry.get(indexOf);
                Entry entry2 = swapEntry.get(swapper);

//              System.out.println(indexOf+"  swap  "+swapper + " swappable?: "+swappable.get(swapper));

                if(entry1 != null && entry2 != null && swappable.get(swapper)){

                    swapEntry.set(swapper, entry1);
                    swapEntry.set(indexOf, entry2);

                }




            }catch (NullPointerException | ArrayIndexOutOfBoundsException e ){

            }


        }


    }


    void initializeAndSwap(){

        int size =  MainFragment.getCheckList().size();
        ArrayList<Boolean> swappable = new ArrayList<>();

        swapEntry.clear();

        while(swappable.size() < size){swappable.add(false); swapEntry.add(new Entry());}

        for(ToggleSwitchOrdering.tNumber tNumber : MainFragment.toggleSwitchOrdering.listToOrder){

            try {
                int indexOf = MainFragment.toggleSwitchOrdering.listToOrder.indexOf(tNumber);
                int swapper = tNumber.number-1;//entry.getViewHolder().orderInt.getValue();

                Entry entry = MainFragment.getCheckList().get(indexOf+1);
                swapEntry.get(indexOf).postEntry(entry);

                swappable.set(indexOf,true);

                Entry entry1 = swapEntry.get(indexOf);
                Entry entry2 = swapEntry.get(swapper);

                if(entry1 != null && entry2 != null && swappable.get(swapper)){

                    swapEntry.set(swapper, entry1);
                    swapEntry.set(indexOf, entry2);

                }




            }catch (NullPointerException | ArrayIndexOutOfBoundsException e ){

            }


        }


    }


    void assignSorted(){

        //Merge

        for(Entry entry : swapEntry){

            try{
                int index = swapEntry.indexOf(entry)+1;
                Entry entryRef = MainFragment.getCheckList().get(index);
                entryRef.setEntry(entry);


            }catch (IndexOutOfBoundsException | NullPointerException e){

            }


        }

    }


    public void delete(){

        if(MainFragment.getCheckList().get(operator.selection-1).getClass() == Entry.class){

            View view = MainFragment.getCheckList().get(operator.selection-1).getViewHolder().itemView;


            view.setZ(-10f);
            view.animate()
                    .translationYBy(1000)
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
