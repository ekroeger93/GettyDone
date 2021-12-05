package com.example.checkListApp.ui.main.entry_management.ButtonPanel;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import com.example.checkListApp.databinding.MainFragmentBinding;


public class ButtonPanel {


    float touchDistance;
    boolean hasSetButtons = false;

    Button activeButton;
    MainFragmentBinding binding;

    List<DuoAction> actionList = new ArrayList<>();

    public ButtonPanelToggle buttonPanelToggle;

    private final int mWidth;
    Context context;

    public ButtonPanel(Context context, MainFragmentBinding binding){
        this.context = context;
        this.binding = binding;
        mWidth = context.getResources().getDisplayMetrics().widthPixels;

        buttonPanelToggle = new ButtonPanelToggle(context,binding);
    }

    public void addButton(Button button, View.OnClickListener primary, View.OnClickListener alternative){
        actionList.add(new DuoAction(button, primary ,alternative));
    }


    public void addButtonWithLeaf(Button button, View.OnClickListener primary, View.OnClickListener alternative, LeafButton leafButton) {
        actionList.add(new DuoAction(button, primary, alternative,leafButton));
    }


    public void setButtons(){

        //Here we just set the X, Y params of button
        if(!hasSetButtons)
            for(DuoAction duoAction : actionList){
                duoAction.setInitialization();
            }

        hasSetButtons = true;
    }

    public boolean checkWithinButtonBoundary(float touch_X, float touch_Y){

        boolean flag =false;

        for(DuoAction duoAction : actionList){
            duoAction.inBound = duoAction.withinBoundary(touch_X,touch_Y);
            duoAction.setListener();

            if(duoAction.inBound) {activeButton = duoAction.button;}

            if(duoAction.hasLeaf){

                if(duoAction.inBound){
                    flag = true;
                duoAction.leaf.toggleLeaf(true);
                duoAction.setInitialization();}else{
                    flag  = false;
                   // duoAction.leaf.toggleLeaf(false);
                }

                duoAction.leaf.inBound = duoAction.leaf.withinBoundary(touch_X,touch_Y);
                duoAction.leaf.setListener();

                if(duoAction.leaf.inBound) {
                    activeButton = duoAction.leaf.button;
                }

            }
        }

        return flag;

    }



    public void revertListeners(){

        for(DuoAction duoAction : actionList){
            duoAction.inBound = false;
            duoAction.setListener();

            if(duoAction.hasLeaf){
                duoAction.leaf.inBound = false;
                duoAction.leaf.setListener();
                duoAction.leaf.toggleLeaf(false);
            }

        }

        activeButton = null;

    }


    public void executeAlternative(){

        if (activeButton != null// && motionEvent == MotionEvent.ACTION_UP
        ){
            for(DuoAction duoAction : actionList) {

                if (duoAction.inBound && duoAction.button == activeButton)
                    activeButton.callOnClick();

                if(duoAction.hasLeaf){

                if (duoAction.leaf.inBound && duoAction.leaf.button == activeButton)
                    activeButton.callOnClick();
                }

            }


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
