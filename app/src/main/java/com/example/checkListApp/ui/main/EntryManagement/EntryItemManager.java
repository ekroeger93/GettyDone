package com.example.checkListApp.ui.main.EntryManagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.MainViewModel;
import com.example.checkListApp.ui.main.RecyclerAdapter;
import com.example.checkListApp.ui.main.Spacer;
import com.example.checkListApp.ui.main.ToggleSwitchOrdering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

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

    public void deleteSelected(SelectionTracker tracker){

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


    static ArrayList<String> swapEntries;
    static ArrayList<Boolean> swapChecks;

    public void sortSelected(SelectionTracker<Long> tracker){

        MainFragment.updateAllSelection();

        initializeSwapArrays();
        swapping();
        assignSorted();

    }

    void initializeSwapArrays(){

        int size = MainFragment.getCheckList().size();

        swapEntries = new ArrayList<>(size);
        swapChecks = new ArrayList<>(size);

        while(swapEntries.size() < size)swapEntries.add(null);
        while(swapChecks.size() < size)swapChecks.add(Boolean.FALSE);

        for(ToggleSwitchOrdering.tNumber tNumber : MainFragment.toggleSwitchOrdering.listToOrder){


            int indexOf = MainFragment.toggleSwitchOrdering.listToOrder.indexOf(tNumber);
            Entry entry = MainFragment.getCheckList().get(indexOf+1);

            if(entry.textEntry.getValue() != null)
                swapEntries.set(indexOf, entry.textEntry.getValue());

            if(entry.checked.getValue() != null){
                swapChecks.set(indexOf, entry.checked.getValue());
            }

                     System.out.println(entry.checked.getValue());

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

                String entry1 = swapEntries.get(indexOf);
                String entry2 = swapEntries.get(swapper);

                Boolean check1 = swapChecks.get(indexOf);
                Boolean check2 = swapChecks.get(swapper);
                 System.out.println(indexOf+"  swap  "+swapper + " swappable?: "+swappable.get(swapper));

                if(entry1 != null && entry2 != null && swappable.get(swapper)){
                    swapEntries.set(swapper, entry1);
                    swapEntries.set(indexOf, entry2);

                    swapChecks.set(swapper,check1);
                    swapChecks.set(indexOf,check2);
                }




            }catch (NullPointerException | ArrayIndexOutOfBoundsException e ){

            }


        }


    }

    void assignSorted(){

        //Merge
        for(String s : swapEntries){

            Entry entry = MainFragment.getCheckList().get(swapEntries.indexOf(s)+1);

            if(s != null && !s.isEmpty())
                entry.textEntry.postValue(s);

            // System.out.println(s);
        }

        for(Boolean b : swapChecks){


            Entry entry = MainFragment.getCheckList().get(swapChecks.indexOf(b)+1);

            if(b != null)
            entry.checked.postValue(b);

            System.out.println(b);

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
