package com.example.checkListApp.time_management;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.checkListApp.time_management.utilities.KeyHelperClass;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entry_management.MainListTimeProcessHandler;

import java.util.Timer;
import java.util.TimerTask;

public class TimerBroadcastReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

       if (intent.getAction().equals(KeyHelperClass.BROADCAST_ACTION_TOGGLE_TIMER)){
           MainListTimeProcessHandler.timerViewModel.toggleTime();


           //TODO: because response time sucks, user needs a indication of timer being toggled
           Vibrator vibrator = (Vibrator) context.getSystemService(context.getSystemServiceName(Vibrator.class));
           vibrator.vibrate(  VibrationEffect.createOneShot(100,1));

           //TODO: THIS IS A CRAP SOLUTION BUT I'VE TRIED BOUT EVERYTHING ELSE SO FAR
//           if(MainFragment.isTimerRunning())
//           new Timer().schedule(new TimerTask() {
//               @Override
//               public void run() {
//
//                   MainListTimeProcessHandler.timerViewModel.executeServiceTask();
//               }
//           },700);


       }

       if (intent.getAction().equals(KeyHelperClass.BROADCAST_ACTION_RESET_TIMER)){
           MainFragment.resetTime();
           TimerService.reset.postValue(true);
        }

       if(intent.getAction().equals(KeyHelperClass.BROADCAST_ACTION_DISMISS)){
           TimerService.reset.postValue(true);
       }




    }


}
