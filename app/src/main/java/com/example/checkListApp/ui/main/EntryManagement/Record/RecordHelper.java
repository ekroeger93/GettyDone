package com.example.checkListApp.ui.main.EntryManagement.Record;

import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.checkListApp.databinding.MainActivityBinding;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.Spacer;

import org.jetbrains.annotations.NotNull;

public final class RecordHelper {

    static int numOfEntries;
    static int numChecked;
    static RecordFinishButtonToggle recordFinishButtonToggle;

    public static void createButton(Context context, MainFragmentBinding binding){

        recordFinishButtonToggle = new RecordFinishButtonToggle();
        RecordFinishButtonToggle.create(context,binding);

    }

static public void update(){

        numOfEntries = MainFragment.getCheckList().size()-2;
        numChecked = 0;

        for(Entry entry : MainFragment.getCheckList()){

            if(entry instanceof Spacer){} else {

                try {
                    if (entry.checked.getValue()) numChecked++;
                }catch (NullPointerException e){
                    continue;
                }

            }

        }

        if(numOfEntries > 2 && numChecked !=0){
            RecordFinishButtonToggle.toggleHideButton(numOfEntries != numChecked);

        }


        Log.d("mRecord",""+numOfEntries+" | "+numChecked);


    }


}
