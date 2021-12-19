package com.example.checkListApp.timemanagement;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.timemanagement.parcel.ListTimersParcel;
import com.example.checkListApp.timemanagement.utilities.KeyHelperClass;
import com.example.checkListApp.timemanagement.utilities.ListTimerUtility;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.data_management.ListUtility;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class TimerService extends Service {

   public static int activeTimeIndex=0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);

        ListTimersParcel parcelableList = intent.getParcelableExtra(KeyHelperClass.TIME_PARCEL_DATA);

        activeTimeIndex = parcelableList.getActiveTimeIndex();


        ForegroundTimerService foregroundTimerService =
                new ForegroundTimerService(this, parcelableList , pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int FOREGROUND_SERVICE_ID = 111;
        startForeground(FOREGROUND_SERVICE_ID, foregroundTimerService.createTimer(notificationManager));


        return (START_NOT_STICKY);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification makeNotification(String data, Entry entry, PendingIntent pendingIntent) {
        String channel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }

        TimeState countTime = new TimeState(data);
        TimeState expireTime = new TimeState( Math.abs(entry.timeAccumulated));

        int timeRemainder =  expireTime.getTimeNumberValue() - countTime.getTimeNumberValue();


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, channel)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.star_on)
                        .setOnlyAlertOnce(true)
                        .setContentTitle("CountDownTime")
                        .setContentText(
                               // "time: " +data+ " expires: " +expireTime.getTimeFormat() +
                                " expires: "+ new TimeState(timeRemainder).getTimeFormat() +
                                        " || "+ entry.textEntry.getValue()

                        );


        return mBuilder
                .setPriority(1)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build(); // foregroundTimerService.createTimer(notification);

    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {

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
        return null;
    }


    static class ForegroundTimerService extends ListTimerUtility {

        private final TimerService timerService;
        private final PendingIntent pendingIntent;
        private final MainTimerViewModel timeViewModel = MainActivity.timerViewModel;
        private final ArrayList<Entry> timerViewModelList;

        private final int FOREGROUND_SERVICE_ID = 111;

        private int elapsedTime;


        @RequiresApi(api = Build.VERSION_CODES.O)
        protected ForegroundTimerService(TimerService timerService, ListTimersParcel parcel, PendingIntent pendingIntent) {
            this.timerService = timerService;

            this.pendingIntent = pendingIntent;

            timerViewModelList = (ArrayList<Entry>) generateEntryList(parcel);

            timeViewModel.setTimeState(new TimeState(getSummationTime(timerViewModelList)));




        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Notification preSetNotification() {
            return timerService.makeNotification("00:00:00", new Entry(), pendingIntent);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Notification createTimer(NotificationManager mgr) {

            AtomicReference<Notification> notification = new AtomicReference<>(preSetNotification());

            int setTime = setTimer(timeViewModel);// getSummationTime(timerViewModelList);

            currentActiveTime = timerViewModelList.get(activeProcessTimeIndex);

            timeViewModel.setServiceTask((time -> {
                elapsedTime = setTime - time;

                if (currentActiveTime.timeElapsed(elapsedTime)// || elapsedTime == setTime
                ) {
                    currentActiveTime =  getNextActiveProcessTime(timerViewModelList);
               }

                //rebuild notification here
                notification.set(timerService.makeNotification(
                        new TimeState(elapsedTime).getTimeFormat()
                        , currentActiveTime
                        , pendingIntent));
                mgr.notify(FOREGROUND_SERVICE_ID, notification.get());

            }));


            return notification.get();
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
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
