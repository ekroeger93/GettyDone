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

   public static MutableLiveData<Boolean> timerToggled = new MutableLiveData<>(false);


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


        startForeground(FOREGROUND_SERVICE_ID,
                foregroundTimerService.createTimer(notificationManager));


        return (START_NOT_STICKY);
    }


    //https://developer.android.com/guide/components/broadcasts
    //https://developer.android.com/training/notify-user/build-notification.html#java

    public Notification makeNotification(int countTime, int elapsedTimeNV, MainTimerViewModel timeViewModel, Entry entry, PendingIntent pendingIntent) {

        String channel = createChannel();
        AtomicReference<NotificationCompat.Builder> mBuilder = new AtomicReference<>(new NotificationCompat.Builder(this, channel));
        BuilderDataHelper dataHelper = new BuilderDataHelper(mBuilder.get(), pendingIntent, timeViewModel);


        Log.d("serviceTest",""+countTime + " "+ entry);

        if(
                countTime <= 1 &&
                        timeViewModel.getRepeaterTime() <= 0

        ) {
            mBuilder.set(builderDismissive(dataHelper, countTime));
        }
        else {



//               mBuilder = builderToggleEntry(dataHelper,entry, countTime);


                mBuilder.set(builderNormal(dataHelper, entry, elapsedTimeNV, countTime));

        }


        return mBuilder.get()
                .setPriority(2)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build(); // foregroundTimerService.createTimer(notification);
    }


    private static class BuilderDataHelper{

        NotificationCompat.Builder builder;
        PendingIntent pendingIntent;
        MainTimerViewModel mainTimerViewModel;

        public BuilderDataHelper(NotificationCompat.Builder builder, PendingIntent pendingIntent, MainTimerViewModel mainTimerViewModel) {
            this.builder = builder;
            this.pendingIntent = pendingIntent;
            this.mainTimerViewModel = mainTimerViewModel;
        }

    }

    private NotificationCompat.Builder builderNormal(
            BuilderDataHelper dataHelper,
            Entry entry, int elapsedTimeNV,  int countTime){

        Intent toggleTimeIntent = new Intent(this, TimerBroadcastReceiver.class);
        toggleTimeIntent.setAction(KeyHelperClass.BROADCAST_ACTION_TOGGLE_TIMER);

        PendingIntent toggleTimePendingIntent =
                PendingIntent.getBroadcast(this, 0, toggleTimeIntent, 0);

        Intent resetTimeIntent = new Intent(this, TimerBroadcastReceiver.class);
        resetTimeIntent.setAction(KeyHelperClass.BROADCAST_ACTION_RESET_TIMER);
        PendingIntent resetTimePendingIntent =
                PendingIntent.getBroadcast(this,0, resetTimeIntent,0);

        TimeState expireTime = new TimeState( Math.abs(entry.timeAccumulated));

        int decimalEntrySetTime = new TimeState(expireTime.getTimeNumberValue()).getTimeNumberValueDecimalTruncated();

        int timeRemainder = new TimeState().getValueAsTimeTruncated(decimalEntrySetTime - elapsedTimeNV);


        String text = (entry.onTogglePrimerTemp) ? "paused" :  new TimeState(timeRemainder).getTimeFormat() ;

        String toggleButtonText = (dataHelper.mainTimerViewModel.isToggled()) ? "Pause" : "Resume";



        return dataHelper.builder  .setContentIntent(dataHelper.pendingIntent)
                .setSmallIcon(R.drawable.outline_timer_black_48)
                .setOnlyAlertOnce(true)
                .setContentTitle("Countdown Timer")
                .addAction(R.drawable.outline_add_circle_black_48, "Reset",
                        resetTimePendingIntent)
                .addAction(android.R.drawable.btn_star, toggleButtonText,
                        toggleTimePendingIntent)
                .setProgress(entry.numberValueTime, Math.abs( decimalEntrySetTime - elapsedTimeNV) , false)
                .setAutoCancel(true)
                .setOngoing(true)
                .setColor(Color.BLUE)
                .setSubText(dataHelper.mainTimerViewModel.getRepeaterTime() + " " + new TimeState(countTime).getTimeFormat())
                .setContentText(entry.textEntry.getValue() + " " + text);

    }


    private NotificationCompat.Builder builderDismissive(BuilderDataHelper dataHelper,int countTime){

        return dataHelper.builder.setContentIntent(dataHelper.pendingIntent)
                .setSmallIcon(R.drawable.outline_timer_black_48)
                .setOnlyAlertOnce(true)
                .setContentTitle("Countdown Timer")
                .setAutoCancel(true)
                .setOngoing(true)
                .setColor(Color.BLUE)
                .setSubText(dataHelper.mainTimerViewModel.getRepeaterTime() + "  " + new TimeState(countTime).getTimeFormat())
                .setContentText("completed");

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
        private final MainTimerViewModel timeViewModel = MainTimerView.mainTimerViewModel;
        private static  ArrayList<Entry> timerViewModelList;

        private final int FOREGROUND_SERVICE_ID = 111;

        private int elapsedTime;


        protected ForegroundTimerService(TimerService timerService, ListTimersParcel parcel, PendingIntent pendingIntent) {
            this.timerService = timerService;

            this.pendingIntent = pendingIntent;

            timerViewModelList = (ArrayList<Entry>) generateEntryList(parcel);

        }

        public ArrayList<Entry> getTimerViewModelList() {
            return timerViewModelList;
        }

        public Notification preSetNotification(int setTime, Entry entry) {
            return timerService.makeNotification(setTime, 0, timeViewModel, entry, pendingIntent);
        }


        public Notification createTimer(NotificationManager mgr) {

            AtomicInteger _setTime = new AtomicInteger(setTimer(timeViewModel));
            int setTime = _setTime.get();
            currentActiveTime = timerViewModelList.get(activeProcessTimeIndex);

            AtomicReference<Notification> notification =
                    new AtomicReference<>(
                            preSetNotification(setTimer(timeViewModel), currentActiveTime));


            timeViewModel.setServicePostExecute(() -> {

                Log.d("serviceTest","here");

                if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= -1) {
//                    notification.set(timerService.makeNotificationDismiss(pendingIntent));
//                    mgr.notify(FOREGROUND_SERVICE_ID, notification.get());

                }else {
                    _setTime.set(setTimer(timeViewModel));
                    currentActiveTime = timerViewModelList.get(activeProcessTimeIndex);
                }




            });

            //TODO: BUG HERE RAPIDLY RESETING TIME
            //TODO: Repeater doesn't work well with single entry
            //TODO  FIRST ENTRY IS TOGGLE AND THIS IS NOT GETTING NEXT INDEX
            //ON SECOND GO AROUND



                timeViewModel.setServiceTask(((elapsedTimeVolatile, countTime, elapsedTimeN) -> {

                    elapsedTime = _setTime.get() - countTime;


                     if (currentActiveTime.timeElapsed(elapsedTime))
                         currentActiveTime = getNextActiveProcessTime(timerViewModelList);


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
