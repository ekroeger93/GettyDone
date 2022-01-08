package com.example.checkListApp.ui.main.entry_management.ButtonPanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;

public class ButtonPanelToggle{


    private MainFragmentBinding binding;
    private boolean isDisabled = false;

    public ToggleButton submitButton;

    public ToggleButton moveUpButton;
    public ToggleButton moveDownButton;

    public CreateButtonManifest submitManifest;
    public CreateButtonManifest moveUpButtonManifest;
    public CreateButtonManifest moveDownButtonManifest;

    private final int buttonSize = 160;



    public ButtonPanelToggle(Context context, MainFragmentBinding binding){

        submitButton = new ToggleButton(context);
        moveUpButton = new ToggleButton(context);
        moveDownButton = new ToggleButton(context);

        this.binding = binding;

        moveUpButtonManifest = getMoveUpButtonManifest(submitButton);
        moveDownButtonManifest = getMoveDownButtonManifest(submitButton);

        submitManifest = getSubmitManifest();

        submitButton
                .setBinding(binding)
                .commitCreate(submitManifest);

        moveUpButton
                .setBinding(binding)
                .commitCreate(moveUpButtonManifest);

        moveDownButton
                .setBinding(binding)
                .commitCreate(moveDownButtonManifest);


        submitButton.setVisibility(View.GONE);
        moveUpButton.setVisibility(View.GONE);
        moveDownButton.setVisibility(View.GONE);



    }


    private CreateButtonManifest getSubmitManifest(){

        return (button, binding, onClickListener) -> {
            if(button.getParent() ==null) {
                button.setWidth(buttonSize);
                button.setHeight(buttonSize);

                button.setId(R.id.submitButtonId);

                button.setBackground(ContextCompat.getDrawable(
                        binding.addDeleteBtn.getContext(),
                        R.drawable.outline_done_black_48));


                ConstraintSet set = new ConstraintSet();
                set.constrainHeight(button.getId(),
                        ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(button.getId(),
                        buttonSize);
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
        };


    }

    private CreateButtonManifest getMoveDownButtonManifest(ToggleButton submitButton){

        return (button, binding, onClickListener) -> {

            if(button.getParent() ==null) {
                button.setWidth(buttonSize);
                button.setHeight(buttonSize);

                button.setId(R.id.moveItemDownId);


                button.setBackground(ContextCompat.getDrawable(
                        binding.addDeleteBtn.getContext(),
                        R.drawable.outline_arrow_circle_down_black_48));



                ConstraintSet set = new ConstraintSet();
                set.constrainHeight(button.getId(),
                        ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(button.getId(),
                        buttonSize);
                set.connect(button.getId(), ConstraintSet.END,
                        submitButton.getId(), ConstraintSet.START, 0);
                set.connect(button.getId(), ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                set.connect(button.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                set.connect(button.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);


                binding.buttonPanel.addView(button);


                set.applyTo(binding.buttonPanel);
            }


            button.setOnClickListener(onClickListener);
        };

    }

    private CreateButtonManifest getMoveUpButtonManifest(ToggleButton submitButton){
        return (button, binding, onClickListener) -> {

            if(button.getParent() ==null) {
                button.setWidth(buttonSize);
                button.setHeight(buttonSize);

                button.setId(R.id.moveItemUpId);

                button.setBackground(ContextCompat.getDrawable(
                        binding.addDeleteBtn.getContext(),
                        R.drawable.outline_arrow_circle_up_black_48));


                ConstraintSet set = new ConstraintSet();
                set.constrainHeight(button.getId(),
                        ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(button.getId(),
                        buttonSize);
                set.connect(button.getId(), ConstraintSet.START,
                        submitButton.getId(), ConstraintSet.END, 0);
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
        };

    }

    public void setSubmitBtnOnClickListener(OnClickListener onClickListener){
        submitButton.setListener(onClickListener).setBinding(binding).commitCreate(submitManifest);
    }

    public void setMoveUpButtonOnClickListener(OnClickListener onClickListener){
        moveUpButton.setListener(onClickListener).setBinding(binding).commitCreate(moveUpButtonManifest);

    }

    public void setMoveDownButtonOnClickListener(OnClickListener onClickListener){
        moveDownButton.setListener(onClickListener).setBinding(binding).commitCreate(moveDownButtonManifest);
    }



    public void toggleDisableToButton(){

        this.isDisabled = !this.isDisabled;

        if(isDisabled){
            binding.editMoveBtn.setVisibility(View.GONE);
            binding.addDeleteBtn.setVisibility(View.GONE);
            binding.touchExpander.setVisibility(View.GONE);

            submitButton.setVisibility(View.VISIBLE);

        }else{
            binding.editMoveBtn.setVisibility(View.VISIBLE);
            binding.addDeleteBtn.setVisibility(View.VISIBLE);
            binding.touchExpander.setVisibility(View.VISIBLE);

            submitButton.setVisibility(View.GONE);

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

    public void toggleDisableMoveButtons(){

        this.isDisabled = !this.isDisabled;

        if(isDisabled){
            binding.editMoveBtn.setVisibility(View.GONE);
            binding.addDeleteBtn.setVisibility(View.GONE);
            binding.touchExpander.setVisibility(View.GONE);

            submitButton.setVisibility(View.VISIBLE);
            moveUpButton.setVisibility(View.VISIBLE);
            moveDownButton.setVisibility(View.VISIBLE);

        }else{
            binding.editMoveBtn.setVisibility(View.VISIBLE);
            binding.addDeleteBtn.setVisibility(View.VISIBLE);
            binding.touchExpander.setVisibility(View.VISIBLE);

            submitButton.setVisibility(View.GONE);
            moveUpButton.setVisibility(View.GONE);
            moveDownButton.setVisibility(View.GONE);

        }


    }

     static void createButton(AppCompatButton button,
                              MainFragmentBinding binding,
                              OnClickListener onClickListener){

         if(button.getParent() ==null) {
             button.setId(R.id.buttonPanelID);
//             button.setText("place");


             button.setBackground(ContextCompat.getDrawable(
                     binding.addDeleteBtn.getContext(),
                     R.drawable.outline_done_black_48));

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
    class ToggleButton extends AppCompatButton implements CreateButtonImpl{

        MainFragmentBinding binding;
        OnClickListener onClickListener;

//
//        public  ToggleButton setBinding(MainFragmentBinding binding) {
//             this.binding = binding;
//            return this;
//        }
//
//        public ToggleButton setListener(OnClickListener clickListener){
//            this.onClickListener = clickListener;
//            return  this;
//        }
//
//        public void commitCreate(){
//
//             createButton(this,binding,onClickListener);
//
//        }
//

         ToggleButton(Context context){
            super(context);
        }


        @Override
        public ToggleButton setBinding(MainFragmentBinding binding) {
            this.binding = binding;
            return this;
        }

        @Override
        public ToggleButton setListener(OnClickListener clickListener) {
            this.onClickListener = clickListener;
            return  this;
        }

        @Override
        public void commitCreate(CreateButtonManifest createButtonManifest) {


             createButtonManifest.create(this,binding, onClickListener);

        }


    }

    static class ToggleUpMoveButton extends AppCompatButton{


        public ToggleUpMoveButton(@NonNull Context context) {
            super(context);
        }


    }



}
