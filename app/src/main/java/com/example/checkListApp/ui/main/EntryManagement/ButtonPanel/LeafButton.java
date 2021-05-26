package com.example.checkListApp.ui.main.EntryManagement.ButtonPanel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

public class LeafButton implements iButtonComponent {

    float Button_X, Button_Y;
    float Button_Width, Button_Height;

    boolean inBound = false;

    Button button;

    View.OnClickListener listener;

    ViewGroup viewGroup;

    public LeafButton(@NonNull Context context) {
        this.button = new Button(context);
        toggleLeaf(false);
    }

    public LeafButton setViewGroup(ViewGroup viewGroup){
        this.viewGroup = viewGroup;
        return  this;
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
        button.setText("multiple");;
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
