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
import android.widget.TextView;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.MainViewModel;

import java.util.ArrayList;

public class EntryItemManager {


    private Context context;
    private MainViewModel mViewModel;
    private Operator operator;

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

    public void delete(){

        if(MainFragment.getCheckList().get(operator.selection-1).getClass() == Entry.class){

            View view = MainFragment.getCheckList().get(operator.selection-1).getViewHolder().itemView;

//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
//                    view,
//                    "translationX",
//                    1000);
//            objectAnimator.setDuration(300);
//            objectAnimator.setInterpolator(new OvershootInterpolator());
//            objectAnimator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//
//                     mViewModel.deleteEntry(MainFragment.getCheckList().get(operator.selection-1));
//                      operator.adapter.notifyItemRemoved(operator.selection - 1);
//                      operator.adapter.notifyItemChanged(MainFragment.getCheckList().size());
//
//                }
//            });
//            objectAnimator.start();


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




            // mViewModel.deleteEntry(MainFragment.getCheckList().get(operator.selection-1));
            //  operator.adapter.notifyItemRemoved(operator.selection - 1);
            //  operator.adapter.notifyItemChanged(MainFragment.getCheckList().size());


           // operator.adapter.notifyItemChanged(MainFragment.getCheckList().size());




    }}

    public void move(){

        boolean isMovingItem = operator.isMovingItem;
        int selection = operator.selection;
       // int oldMovePosition = operator.oldMovePosition;


        if (!isMovingItem) {
            operator.movingItem = MainFragment.getCheckList().get(selection - 1);
            operator.oldMovePosition = selection - 1;
            operator.movingItem.setViewHolder(MainFragment.getCheckList().get(selection - 1).getViewHolder());
            operator.movingItem.getViewHolder().itemView.setBackgroundColor(Color.BLUE);
            operator.moveItem(operator.movingItem);
            buttonPanelToggle.toggleDisableWithPlace();
            ;

        }else{
            operator.adapter.notifyItemChanged(selection-1);
            operator.movingItem = null;
            buttonPanelToggle.toggleDisableWithPlace();
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
