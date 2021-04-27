package com.example.checkListApp.ui.main.EntryManagement;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.ui.main.MainFragment;


public class ButtonPanel {


    float touchDistance;
    boolean hasSetButtons = false;

    Button activeButton;

    List<DuoAction> actionList = new ArrayList<>();

    private final int mWidth;
    Context context;

    public ButtonPanel(Context context){
        this.context = context;
        mWidth = context.getResources().getDisplayMetrics().widthPixels;

    }

    public void addButton(Button button, View.OnClickListener primary, View.OnClickListener alternative){
        actionList.add(new DuoAction(button, primary ,alternative));
    }

    public void setButtons(){

        //Here we just set the X, Y params of button
        if(!hasSetButtons)
            for(DuoAction duoAction : actionList){
                duoAction.setInitialization();
            }

        hasSetButtons = true;
    }

    public void checkWithinButtonBoundary(float touch_X , float touch_Y){

        for(DuoAction duoAction : actionList){
            duoAction.inBound = duoAction.withinBoundary(touch_X,touch_Y);
            duoAction.setListener();
            if(duoAction.inBound) activeButton = duoAction.button;
        }

    }

    public void revertListeners(){

        for(DuoAction duoAction : actionList){
            duoAction.inBound = false;
            duoAction.setListener();
        }

        activeButton = null;

    }


    public void executeAlternative( ){

        if (activeButton != null// && motionEvent == MotionEvent.ACTION_UP
        ){ 
            activeButton.callOnClick();
            revertListeners();
        }

    }


    private float computeRatio(float touch_X) {

        if (touch_X < mWidth / 2f) {
            touchDistance = Math.abs(touch_X - mWidth / 2f);
        } else {
            touchDistance = Math.abs(mWidth / 2f - touch_X);
        }

        return 1 + ((touchDistance / mWidth) * 12);

    }


    public void animationHandler(int motionEvent, float touch_x, MainFragmentBinding binding) {


        binding.addDeleteBtn.setText((CharSequence) "DELETE");
        binding.editMoveBtn.setText((CharSequence)"MOVE");


      try {
          binding.touchExpander.setScaleX(computeRatio(touch_x));

          if (motionEvent == MotionEvent.ACTION_UP) {

              binding.touchExpander.animate()
                      .setDuration(1000)
                      .setInterpolator(new BounceInterpolator())
                      .scaleX(1)
                      .start();


              binding.addDeleteBtn.setText((CharSequence) "ADD");
              binding.editMoveBtn.setText((CharSequence)"EDIT");
          }
      } catch (NullPointerException e){


      }




    }




}
