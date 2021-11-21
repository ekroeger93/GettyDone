package com.example.checkListApp.settimer;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.checkListApp.timer.TimeState;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SetTimerViewModel extends ViewModel {

    private final MutableLiveData<String> _timerText = new MutableLiveData<>("00:00:00");
    private String timerText = "000000";
    private final StringBuilder caretText = new StringBuilder();
    private TimeState timeState = new TimeState(0);


    public void inputText(String input){
        //add shift characters to left
        try {
            caretText.append(input);
            int TIME_LENGTH = 6;
            timerText = timerText.substring(0, TIME_LENGTH - caretText.length() ) + caretText;
            postValue(timerText);

        } catch (StringIndexOutOfBoundsException ignored){

        }
    }

    public void removeText(){
        //remove from caret position, shift to right
    try {
        timerText = "0" + timerText.substring(0, timerText.length() - 1);
        caretText.deleteCharAt(caretText.length() - 1);
        postValue(timerText);

    }catch (StringIndexOutOfBoundsException ignored){
    }

    }

    private void postValue(String parseValue){

        int tm = Integer.parseInt(parseValue);
        timeState = new TimeState(tm);
        _timerText.postValue(timeState.getTimeFormat());

      //  countTimer.setTimer(timeState);

    }


    public MutableLiveData<String> getTimerText(){
        return _timerText;
    }

    public int getTimerValue(){return Integer.parseInt(timeState.getTimeUnformatted());}



}