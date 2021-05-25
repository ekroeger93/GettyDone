package com.example.checkListApp.ui.main.EntryManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;

public class ButtonPanelToggle{


    Context context;
    private MainFragmentBinding binding;
    private boolean isDisabled = false;

    public ToggleButton toggleButton;



    public ButtonPanelToggle(Context context, MainFragmentBinding binding){

        toggleButton = new ToggleButton(context);

        this.binding = binding;

        toggleButton
                .setBinding(binding)
                .commitCreate();

        toggleButton.setVisibility(View.GONE);

    }


    public void setOnClickListener(OnClickListener onClickListener){
        toggleButton.setListener(onClickListener).setBinding(binding).commitCreate();
    }


    public void toggleDisableToButton(){

        this.isDisabled = !this.isDisabled;

        if(isDisabled){
            binding.editMoveBtn.setVisibility(View.GONE);
            binding.addDeleteBtn.setVisibility(View.GONE);
            binding.touchExpander.setVisibility(View.GONE);

            toggleButton.setVisibility(View.VISIBLE);

        }else{
            binding.editMoveBtn.setVisibility(View.VISIBLE);
            binding.addDeleteBtn.setVisibility(View.VISIBLE);
            binding.touchExpander.setVisibility(View.VISIBLE);

            toggleButton.setVisibility(View.GONE);

        }


    }

    public void toggleDisable(){

        this.isDisabled = !this.isDisabled;

        if(isDisabled){
            binding.editMoveBtn.setVisibility(View.GONE);
            binding.addDeleteBtn.setVisibility(View.GONE);
            binding.touchExpander.setVisibility(View.GONE);

        }else{
            binding.editMoveBtn.setVisibility(View.VISIBLE);
            binding.addDeleteBtn.setVisibility(View.VISIBLE);
            binding.touchExpander.setVisibility(View.VISIBLE);


        }

    }

     static void createButton(AppCompatButton button,
                              MainFragmentBinding binding,
                              OnClickListener onClickListener){

         if(button.getParent() ==null) {
             button.setId(R.id.my_Id);
             button.setText("place");

             button.setWidth(200);
             button.setHeight(100);

             ConstraintSet set = new ConstraintSet();
             set.constrainHeight(button.getId(),
                     ConstraintSet.WRAP_CONTENT);
             set.constrainWidth(button.getId(),
                     ConstraintSet.WRAP_CONTENT);
             set.connect(button.getId(), ConstraintSet.START,
                     ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
             set.connect(button.getId(), ConstraintSet.END,
                     ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
             set.connect(button.getId(), ConstraintSet.TOP,
                     ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
             set.connect(button.getId(), ConstraintSet.BOTTOM,
                     ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);


             binding.buttonPanel.addView(button);


             set.applyTo(binding.buttonPanel);
         }


        button.setOnClickListener(onClickListener);

    }



    @SuppressLint("ViewConstructor")
    static
    class ToggleButton extends AppCompatButton{

        MainFragmentBinding binding;
        OnClickListener onClickListener;

        public  ToggleButton setBinding(MainFragmentBinding binding) {
             this.binding = binding;
            return this;
        }

        public ToggleButton setListener(OnClickListener clickListener){
            this.onClickListener = clickListener;
            return  this;
        }

        public void commitCreate(){
             createButton(this,binding,onClickListener);
        }


         ToggleButton(Context context){
            super(context);
        }


    }



}
