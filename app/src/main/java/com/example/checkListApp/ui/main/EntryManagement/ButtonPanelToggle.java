package com.example.checkListApp.ui.main.EntryManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.ui.main.MainFragment;

public class ButtonPanelToggle {


    Context context;
    private final EntryItemManager entryItemManager;
    private final MainFragmentBinding binding;
    private boolean isDisabled = false;

    public ToggleButton toggleButton;


    public ButtonPanelToggle(MainFragmentBinding binding, Context context, EntryItemManager entryItemManager){

        this.binding =binding;
        this.context =context;
        this.entryItemManager = entryItemManager;

        toggleButton = new ToggleButton(context);

        toggleButton
                .setEntryItemManager(entryItemManager)
                .setBinding(binding)
                .commitCreate();//Im sure im breaking some principle here but I like!!!

        toggleButton.setVisibility(View.GONE);

    }



    public void toggleDisableWithPlace(){


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

     static void createButton(AppCompatButton button, MainFragmentBinding binding, EntryItemManager entryItemManager){

       // AppCompatButton button = ToggleButton.getToggleButton(context);

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


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entryItemManager.move();
            }
        });


    }

    @SuppressLint("ViewConstructor")
    static
    class ToggleButton extends AppCompatButton{

        MainFragmentBinding binding;
        EntryItemManager entryItemManager;

        private boolean created = false;

        public ToggleButton setEntryItemManager(EntryItemManager entryItemManager) {
            if(!created) this.entryItemManager = entryItemManager;

        return this;
        }

        public  ToggleButton setBinding(MainFragmentBinding binding) {
            if(!created) this.binding = binding;

            return this;
        }

        public ToggleButton commitCreate(){
         if(!created) createButton(this,binding,entryItemManager);
         created = true;

            return this;
        }


         ToggleButton(Context context){
            super(context);
        }


    }



}
