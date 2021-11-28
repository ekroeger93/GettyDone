package com.example.checkListApp.timer;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class TimeToggler {
    private static final TimeToggler TogglerInstance = new TimeToggler();

    private boolean toggleTime = false;
    private TimeToggler(){}

    static public TimeToggler getTimeToggler(){
        return TogglerInstance;
    }

    public boolean isToggleTimeON() { return toggleTime; }

    public void shutDown(){
        toggleTime = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void toggleTime(CountDownTimerAsync countDownTimerAsync,
                           TimeToggleTask postTimerOff
            ){
        if (!toggleTime) {
            postTimerOff.execute(true);
            countDownTimerAsync.execute();
        }else{
            postTimerOff.execute(false);
        }

        toggleTime = !toggleTime;

    }

    @FunctionalInterface
    public interface TimeToggleTask{
        void execute(boolean flag);
    }


}
