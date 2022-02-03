package com.example.checkListApp.ui.main.entry_management.record;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecordFinishButtonToggle {

//    static FinishButton finishButton;
     Button finishButton;


    public void setFinishButton(Button finishButton, Context context) {
        this.finishButton = finishButton;

        finishButton.setVisibility(View.GONE);

        finishButton.setOnClickListener( view -> {


            RecordHelper.recordArrayList =
                    RecordHelper.getJsonRecordGeneratedArray(
                            ProgressProvider.loadProgress(context));

                RecordHelper.recordArrayList.add(
                        new Record(RecordHelper.numOfEntries)
                );

                RecordHelper.buildRecordListJson();

                ProgressProvider.saveProgress(RecordHelper.recordListJson,context);

                Toast toast = Toast.makeText(context,"Progress Recorded!",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();


            });

    }


    public void toggleHideButton(boolean hide){


        Animation animation =
                new ScaleAnimation(
                        1f, .9f,
                        1f,.9f,
                        35,35);
        animation.setDuration(200);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(5);

        finishButton.setAnimation(animation);

        if(hide){

            finishButton.getAnimation().cancel();

            finishButton.setVisibility(View.GONE);
        }else{

            finishButton.setVisibility(View.VISIBLE);

            finishButton.getAnimation().start();



        }

    }

//    static public void create(Context context, MainFragmentBinding binding) {
//
//         finishButton = new FinishButton(context);
//
//        if (finishButton.getParent() == null) {
//
//
//            finishButton.setId(R.id.buttonPanelID);
//
//            finishButton.setWidth(300);
//            finishButton.setHeight(160);
//
//            ConstraintSet set = new ConstraintSet();
//            set.constrainHeight(finishButton.getId(),
//                    ConstraintSet.WRAP_CONTENT);
//            set.constrainWidth(finishButton.getId(),
//                    ConstraintSet.WRAP_CONTENT);
//            set.connect(finishButton.getId(), ConstraintSet.START,
//                    ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
//            set.connect(finishButton.getId(), ConstraintSet.END,
//                    ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
//            set.connect(finishButton.getId(), ConstraintSet.TOP,
//                    binding.guideline2.getId(), ConstraintSet.BOTTOM, 30);
//            set.connect(finishButton.getId(), ConstraintSet.BOTTOM,
//                    binding.buttonPanel.getId(), ConstraintSet.TOP, 0);
//
//
//            binding.main.addView(finishButton);
//
//            set.applyTo(binding.main);
//
//            finishButton.setVisibility(View.GONE);
//
//
//            finishButton.setOnClickListener( view -> {
//
//                RecordHelper.recordArrayList.add(
//                        new Record(RecordHelper.numOfEntries)
//                );
//
//                RecordHelper.buildRecordListJson();
//
//                ProgressProvider.saveProgress(RecordHelper.recordListJson,context);
//
//
//            });
//
//        }
//
//    }



    static class FinishButton extends AppCompatButton{


        public FinishButton(@NonNull @NotNull Context context) {
            super(context);
        }



    }

}
