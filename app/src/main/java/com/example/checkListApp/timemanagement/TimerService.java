package com.example.checkListApp.timemanagement;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.timemanagement.parcel.ListTimersParcel;
import com.example.checkListApp.timemanagement.utilities.KeyHelperClass;
import com.example.checkListApp.timemanagement.utilities.ListTimerUtility;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class TimerService extends LifecycleService {

   public static int activeTimeIndex=0;
   private Intent serviceIntent;
   public static MutableLiveData<Boolean> reset = new MutableLiveData<>(false);

    @Override
    public void onCreate() {
        super.onCreate();

        Observer<Boolean> onReset = aBoolean -> {

            if(aBoolean) {
                stopSelf();
                stopService(serviceIntent);
                onDestroy();
            }
        };

        reset.observe(this,onReset);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        serviceIntent = intent;

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        ListTimersParcel parcelableList = intent.getParcelableExtra(KeyHelperClass.TIME_PARCEL_DATA);

        activeTimeIndex = parcelableList.getActiveTimeIndex();


        ForegroundTimerService foregroundTimerService =
                new ForegroundTimerService(this, parcelableList, pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int FOREGROUND_SERVICE_ID = 111;

//      if(!parcelableList.globalSetTimer.equals("00:00:00"))
        startForeground(FOREGROUND_SERVICE_ID, foregroundTimerService.createTimer(notificationManager));


        return (START_NOT_STICKY);
    }

//    elapsedTime
//                            , countTime
//                            , elapsedTimeVolatile
//                            , elapsedTimeN

    public Notification makeNotification(int countTime, int elapsedTimeNV, MainTimerViewModel timeViewModel, Entry entry, PendingIntent pendingIntent) {
        String channel;

        channel = createChannel();


        Intent toggleTimeIntent = new Intent(this, TimerBroadcastReceiver.class);
            toggleTimeIntent.setAction(KeyHelperClass.BROADCAST_ACTION_TOGGLE_TIMER);

        PendingIntent toggleTimePendingIntent =
                PendingIntent.getBroadcast(this, 0, toggleTimeIntent, 0);

        Intent resetTimeIntent = new Intent(this, TimerBroadcastReceiver.class);
            resetTimeIntent.setAction(KeyHelperClass.BROADCAST_ACTION_RESET_TIMER);

         PendingIntent resetTimePendingIntent =
                PendingIntent.getBroadcast(this,0, resetTimeIntent,0);

        //https://developer.android.com/guide/components/broadcasts
        //https://developer.android.com/training/notify-user/build-notification.html#java

        TimeState expireTime = new TimeState( Math.abs(entry.timeAccumulated));

        int decimalEntrySetTime = new TimeState(expireTime.getTimeNumberValue()).getTimeNumberValueDecimalTruncated();

        int timeRemainder = new TimeState().getValueAsTimeTruncated(decimalEntrySetTime - elapsedTimeNV);

//        Log.d("serviceTest",
//        "r: "+ new TimeState().getValueAsTimeTruncated(decimalEntrySetTime - elapsedTimeNV) +
//            "ra: "+ (decimalEntrySetTime- new TimeState().getValueAsTimeTruncated(elapsedTimeNV))+
////                " eT: "+elapsedTime+
//                        " cT: " +countTime+
////                        " elapsedTimeVolatile: " +elapsedTimeVolatile+
//                        " ex: " +expireTime.getTimeNumberValue()+
//                        " eTNV: "+elapsedTimeNV+
////                        " TR: " +timeRemainder+
////
////                        " TRX: "+timeRemainderX+
////                        " TTRX: "+(expireTime.getTimeNumberValue() -elapsedTimeNV)
//                        " elasped: "+ ( decimalEntrySetTime - elapsedTimeNV)+
//                ""
//        );

   //     new TimeState(entry.getTimeAccumulated()).timeTruncated();

        if(countTime <= 0 && timeViewModel.getRepeaterTime() <= 0 ) {
            Log.d("serviceTest", "END!");
            return makeNotificationDismiss(pendingIntent);

        }else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.outline_timer_black_48)
                            .setOnlyAlertOnce(true)
                            .setContentTitle("Countdown Timer")
                            .addAction(R.drawable.outline_add_circle_black_48, "R",
                                    resetTimePendingIntent)
                            .addAction(android.R.drawable.btn_star, "Toggle",
                                    toggleTimePendingIntent)
                            .setProgress(entry.numberValueTime, Math.abs( decimalEntrySetTime - elapsedTimeNV) , false)
                            .setAutoCancel(true)
                            .setOngoing(true)
                            .setColor(Color.BLUE)
                            .setSubText(timeViewModel.getRepeaterTime() + "  " + new TimeState(countTime).getTimeFormat())
                            .setContentText(entry.textEntry.getValue() + "  " + new TimeState(timeRemainder).getTimeFormat());

//            Log.d("serviceTest",decimalEntrySetTime+"  "+Math.abs( decimalEntrySetTime - elapsedTimeNV) + " "+entry.numberValueTime);

            return mBuilder
                    .setPriority(2)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build(); // foregroundTimerService.createTimer(notification);
        }
    }


    public Notification makeNotificationDismiss(PendingIntent pendingIntent){

        String channel;
        channel = createChannel();

        Intent resetTimeIntent = new Intent(this, TimerBroadcastReceiver.class);
        resetTimeIntent.setAction(KeyHelperClass.BROADCAST_ACTION_RESET_TIMER);

        PendingIntent resetTimePendingIntent =
                PendingIntent.getBroadcast(this,0, resetTimeIntent,0);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, channel)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.star_on)
                        .setOnlyAlertOnce(true)
                        .setContentTitle("Countdown Timer")
                        .setAutoCancel(true)
                        .setColor(Color.BLUE)
                        .setSubText("completed")
                        .setContentText("");


//        if(timer <= 0 && timeViewModel.getRepeaterTime() <= 0 ) {
//            stopService(serviceIntent);
//        }

        return mBuilder
                .setPriority(2)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build(); // foregroundTimerService.createTimer(notification);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //synchronized
    private String createChannel() {

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_LOW;

        String CHANNEL_ID = "com.example.countdowntimer";
        String channelName = "My Background Service";

        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }

        return CHANNEL_ID;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;

    }




    static class ForegroundTimerService extends ListTimerUtility {

        private final TimerService timerService;
        private final PendingIntent pendingIntent;
//        private final MainTimerViewModel timeViewModel = MainActivity.timerViewModel;
        private final MainTimerViewModel timeViewModel = MainTimerView.mainTimerViewModel;
        private final ArrayList<Entry> timerViewModelList;

        private final int FOREGROUND_SERVICE_ID = 111;

        private int elapsedTime;


        protected ForegroundTimerService(TimerService timerService, ListTimersParcel parcel, PendingIntent pendingIntent) {
            this.timerService = timerService;

            this.pendingIntent = pendingIntent;

            timerViewModelList = (ArrayList<Entry>) generateEntryList(parcel);

//            timeViewModel.setTimeState(new TimeState(getSummationTime(timerViewModelList)));


            for(Entry n : timerViewModelList){
                Log.d("serviceTest",""+n.numberValueTime);
            }


        }


        public Notification preSetNotification() {
            return timerService.makeNotification(1, 0, timeViewModel, new Entry(), pendingIntent);
        }


        public Notification createTimer(NotificationManager mgr) {

            AtomicReference<Notification> notification = new AtomicReference<>(preSetNotification());

            AtomicInteger setTime = new AtomicInteger(setTimer(timeViewModel));// getSummationTime(timerViewModelList);

            currentActiveTime = timerViewModelList.get(activeProcessTimeIndex);

            timeViewModel.setServicePostExecute(() -> {
//                setTimer(timeViewModel);

//                Log.d("serviceTest","repeater: "+MainTimerView.mainTimerViewModel.getRepeaterTime());

                if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= -1) {

                    notification.set(timerService.makeNotificationDismiss(pendingIntent));
                    mgr.notify(FOREGROUND_SERVICE_ID, notification.get());

                    timerService.makeNotificationDismiss(pendingIntent);

                }else {
                    setTime.set(setTimer(timeViewModel));
                    currentActiveTime = timerViewModelList.get(activeProcessTimeIndex);
                    
//                    Log.d("serviceTest",""+activeProcessTimeIndex);
//                  currentActiveTime = timerViewModelList.get(activeProcessTimeIndex);




                }




            });

            //TODO: BUG HERE RAPIDLY RESETING TIME
                timeViewModel.setServiceTask(((elapsedTimeVolatile, countTime, elapsedTimeN) -> {

                    int setTimeDecimalTruncated = new TimeState(timeViewModel.getNumberValueTime()).getTimeNumberValueDecimalTruncated();

//                elapsedTime = timeViewModel.getNumberValueTime() - countTime;
                    elapsedTime = setTime.get() - countTime;

//                Log.d("serviceTest")

//              Log.d("serviceTest","time: "+ currentActiveTime.getTimeAccumulated() + " :: "+time);
                     if (currentActiveTime.timeElapsed(elapsedTime)// || elapsedTime == setTime
                     ) {
                         currentActiveTime = getNextActiveProcessTime(timerViewModelList);
//                         Log.d("serviceTest","time: "+ currentActiveTime.getTimeAccumulated() + " :: ");

                     }

//                    currentActiveTime = getCurrentActiveTime();

//                    Log.d("serviceTest", timeViewModel.getNumberValueTime()+"  " +countTime+"   "+ elapsedTime + " ST: "+setTime.get());

                    //rebuild notification here
                    notification.set(timerService.makeNotification(
                            countTime,
                            elapsedTimeN
                            , timeViewModel
                            , currentActiveTime
                            , pendingIntent));
                    mgr.notify(FOREGROUND_SERVICE_ID, notification.get());
            }));

            return notification.get();
        }


        public int setTimer(MainTimerViewModel mainTimerViewModel){

            if(mainTimerViewModel.getNumberValueTime() == 0) {
                int summationTime = getSummationTime(timerViewModelList);
                String setTime = new TimeState(summationTime).getTimeFormat();
                mainTimerViewModel.setCountDownTimer(setTime);

                accumulation(timerViewModelList);

                revertTimeIndex();
                currentActiveTime = timerViewModelList.get(1);

                return summationTime;

            }else{
                accumulation(timerViewModelList);
                activeProcessTimeIndex = activeTimeIndex;

                return getSummationTime(timerViewModelList);
            }
        }



    }
}
