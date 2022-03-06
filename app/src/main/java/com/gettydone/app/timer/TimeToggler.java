package com.gettydone.app.timer;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class TimeToggler {

    private boolean toggleTime = false;
    public TimeToggler(){}

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
