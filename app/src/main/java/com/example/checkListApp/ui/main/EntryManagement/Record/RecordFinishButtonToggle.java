package com.example.checkListApp.ui.main.EntryManagement.Record;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.ui.main.MainFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecordFinishButtonToggle {

    static FinishButton finishButton;


    static public void toggleHideButton( boolean hide){


        if(hide){
            finishButton.setVisibility(View.GONE);
        }else{
            finishButton.setVisibility(View.VISIBLE);
        }

    }

    static public void create(Context context, MainFragmentBinding binding) {

         finishButton = new FinishButton(context);

        if (finishButton.getParent() == null) {


            finishButton.setId(R.id.buttonPanelID);

            finishButton.setWidth(300);
            finishButton.setHeight(160);

            ConstraintSet set = new ConstraintSet();
            set.constrainHeight(finishButton.getId(),
                    ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(finishButton.getId(),
                    ConstraintSet.WRAP_CONTENT);
            set.connect(finishButton.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            set.connect(finishButton.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            set.connect(finishButton.getId(), ConstraintSet.TOP,
                    binding.guideline2.getId(), ConstraintSet.BOTTOM, 30);
            set.connect(finishButton.getId(), ConstraintSet.BOTTOM,
                    binding.buttonPanel.getId(), ConstraintSet.TOP, 0);


            binding.main.addView(finishButton);

            set.applyTo(binding.main);

            finishButton.setVisibility(View.GONE);


            finishButton.setOnClickListener( view -> {

                RecordHelper.recordArrayList.add(
                        new Record(RecordHelper.numOfEntries)
                );

                RecordHelper.buildRecordListJson();

                ProgressProvider.saveProgress(RecordHelper.recordListJson,context);


            });

        }

    }



    static class FinishButton extends AppCompatButton{


        public FinishButton(@NonNull @NotNull Context context) {
            super(context);
        }



    }

}
