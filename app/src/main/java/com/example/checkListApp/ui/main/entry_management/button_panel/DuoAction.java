package com.example.checkListApp.ui.main.entry_management.button_panel;

import android.view.View.OnClickListener;
import android.widget.Button;

public class DuoAction implements iButtonComponent {

    public boolean hasLeaf;
    public LeafButton leaf;

    public Button button;
    public boolean toggle = false;
    public boolean inBound = false;

    int Button_X, Button_Y;
    int Button_Width, Button_Height;
    OnClickListener primary, alternative;

    public DuoAction(Button button, OnClickListener primary , OnClickListener alternative){
        this.button = button;
        this.primary = primary;
        this.alternative = alternative;
        setListener();
    }

    public DuoAction(Button button, OnClickListener primary, OnClickListener alternative, LeafButton leaf){

        this.button = button;
        this.primary = primary;
        this.alternative = alternative;
        setListener();

        this.hasLeaf = true;
        this.leaf = leaf;


    }

    public void setInitialization(){

        int[] location = new int[2];
        button.getLocationInWindow(location);
        Button_X = location[0];
        Button_Y = location[1];
        Button_Width = button.getWidth();
        Button_Height = button.getHeight();

        if(hasLeaf){

//            leaf.button.setX(Button_X);
//            leaf.button.setY(Button_Y - (Button_Height*2.6f));
//
//            leaf.button.setScaleX(.75f);
//            leaf.button.setTranslationX(Button_X - 30);

            leaf.setInitialization();
//            leaf.setConstraint(leaf.button);
        }

    }

    public void setListener(){

        if (inBound) {
            button.setOnClickListener(alternative);
        }else{
            button.setOnClickListener(primary);
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
