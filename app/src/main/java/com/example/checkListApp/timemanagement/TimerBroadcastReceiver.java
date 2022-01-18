package com.example.checkListApp.timemanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.timemanagement.utilities.KeyHelperClass;
import com.example.checkListApp.timer.TimeToggler;
import com.example.checkListApp.ui.main.MainFragment;

public class TimerBroadcastReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

       if (intent.getAction().equals(KeyHelperClass.BROADCAST_ACTION_TOGGLE_TIMER)){
           MainFragment.getMainTimerViewModel().toggleTime();
       }

       if (intent.getAction().equals(KeyHelperClass.BROADCAST_ACTION_RESET_TIMER)){
           MainFragment.resetTime();
           TimerService.reset.postValue(false);
        }





    }


}
