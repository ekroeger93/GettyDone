package com.example.checkListApp.ui.main.entry_management.ButtonPanel;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;

public class LeafButton implements iButtonComponent {

    float Button_X, Button_Y;
    float Button_Width, Button_Height;

    boolean inBound = false;

    Button button;

    View.OnClickListener listener;

    ViewGroup viewGroup;
    View attachedView;

    MainFragmentBinding binding;

    public LeafButton(@NonNull Context context, MainFragmentBinding binding) {
        this.button = new Button(context);
        toggleLeaf(false);
        this.binding = binding;
    }

    public LeafButton setViewGroup(ViewGroup viewGroup){
        this.viewGroup = viewGroup;
        return  this;
    }

    public LeafButton setAttachedView(View attachedView) {
        this.attachedView = attachedView;
        return this;
    }




    public LeafButton assignListener(View.OnClickListener listener){
        this.listener = listener;
        return this;
    }

    public void toggleLeaf(boolean flag){

        if(flag) {

            button.setVisibility(View.VISIBLE);

        }else{
            button.setVisibility(View.GONE);
        }
    }


    public LeafButton create(){

        //TODO: Modify
//        button.setText("multiple");;

        button.setBackground(ContextCompat.getDrawable(
                binding.addDeleteBtn.getContext(),
                R.drawable.outline_format_list_numbered_black_48));


        viewGroup.addView(button);
        return  this;
    }

    public void setInitialization(){

        int[] location = new int[2];
        button.getLocationInWindow(location);

        this.Button_X = location[0];
        this.Button_Y = location[1];

        this.Button_Width = button.getWidth();
        this.Button_Height = button.getHeight();

        toggleLeaf(true);

    }

    public void setConstraint(Button leaf){


        ConstraintSet set = new ConstraintSet();

        set.connect(leaf.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        set.connect(leaf.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        set.connect(leaf.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.connect(leaf.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);

        set.applyTo(binding.buttonPanel);


    }



    public void setListener(){

        if(inBound && button.getVisibility() == View.VISIBLE){
        button.setOnClickListener(listener);}
        else{
            button.setOnClickListener(null);
        }

    }


    public boolean withinBoundary(float x, float y){
        return (x > Button_X &&
                y < Button_Y+Button_Height &&
                x < Button_X+Button_Width &&
                y > Button_Y
        );
    }






}
