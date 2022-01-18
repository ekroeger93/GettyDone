package com.example.checkListApp.ui.main.entry_management.button_panel;

import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;

public class LeafButton implements iButtonComponent {

    float Button_X, Button_Y;
    float Button_Width, Button_Height;

    boolean inBound = false;

    Button button;

    View.OnClickListener listener;
    MainFragmentBinding binding;

    public LeafButton(MainFragmentBinding binding, View attachedView) {

//        this.button = new Button(context);

        if(attachedView == binding.editMoveBtn){
            button = binding.leafButtonLeft;
        }
        if(attachedView == binding.addDeleteBtn){
            button = binding.leafButtonRight;
        }

        toggleLeaf(false);
        this.binding = binding;
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


        button.setBackground(ContextCompat.getDrawable(
                binding.addDeleteBtn.getContext(),
                R.drawable.outline_format_list_numbered_black_48));


        return  this;
    }

    public void setInitialization(){

        int[] location = new int[2];
        button.getLocationOnScreen(location);
        button.getLocationOnScreen(location);

        this.Button_X = button.getX();
        this.Button_Y = location[1];


        this.Button_Width = button.getWidth();
        this.Button_Height = button.getHeight();

        toggleLeaf(true);

    }

//    public void setConstraint(Button leaf){
//
//
//        ConstraintSet set = new ConstraintSet();
//
//
//        if (attachedView.getId() == binding.editMoveBtn.getId()){
//            leaf.setId(R.id.leafOneButtonID);
//
//            set.constrainHeight(leaf.getId(),
//                    ConstraintSet.WRAP_CONTENT);
//            set.constrainWidth(leaf.getId(),
//                    160);
//
//            set.connect(leaf.getId(), ConstraintSet.START,
//                    ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
//            set.connect(leaf.getId(), ConstraintSet.END,
//                    binding.guidelineVerticalCenter.getId(), ConstraintSet.END, 40);
//            set.connect(leaf.getId(), ConstraintSet.TOP,
//                    ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
//            set.connect(leaf.getId(), ConstraintSet.BOTTOM,
//                    binding.editMoveBtn.getId(), ConstraintSet.BOTTOM, 0);
//
////            binding.buttonPanel.addView(leaf);
//
//        }
//
//        if(attachedView.getId() == binding.addDeleteBtn.getId()){
//            leaf.setId(R.id.leafTwoButtonID);
//
//            set.constrainHeight(leaf.getId(),
//                    ConstraintSet.WRAP_CONTENT);
//            set.constrainWidth(leaf.getId(),
//                    160);
//
//            set.connect(leaf.getId(), ConstraintSet.END,
//                    ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
//            set.connect(leaf.getId(), ConstraintSet.START,
//                    binding.guidelineVerticalCenter.getId(), ConstraintSet.START, 40);
//            set.connect(leaf.getId(), ConstraintSet.TOP,
//                    ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
//            set.connect(leaf.getId(), ConstraintSet.BOTTOM,
//                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
//
////            binding.buttonPanel.addView(leaf);
//        }
//
//
//
//
////       set.applyTo(binding.main);
//        set.applyTo(binding.main);
//
//    }



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
