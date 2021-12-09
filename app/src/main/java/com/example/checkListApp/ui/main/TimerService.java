package com.example.checkListApp.ui.main;

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
import com.example.checkListApp.timemanagement.ListTimersParcel;
import com.example.checkListApp.timemanagement.utilities.KeyHelperClass;
import com.example.checkListApp.timemanagement.utilities.ListTimerUtility;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.data_management.ListUtility;
import com.example.checkListApp.ui.main.entries.Entry;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class TimerService extends Service {


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);

        ListTimersParcel parcelableList = intent.getParcelableExtra(KeyHelperClass.TIME_PARCEL_DATA);



            Log.d("serviceTest", ". "+ Arrays.toString(parcelableList.listOfCountDownTimers));


        ForegroundTimerService foregroundTimerService = new ForegroundTimerService(parcelableList , pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

        Log.d("timerTest",data);
        TimeState expireTime = new TimeState( Math.abs(entry.numberValueTime));




        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, channel)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.star_on)
                        .setOnlyAlertOnce(true)
                        .setContentTitle("CountDownTime")
                        .setContentText("time: "+data+" expires: "+expireTime.getTimeFormat() );


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

    private class ForegroundTimerService extends ListTimerUtility {

        private final PendingIntent pendingIntent;
        private final MainTimerViewModel timeViewModel = MainActivity.timerViewModel;
        private final ArrayList<Entry> timerViewModelList;

        private final int FOREGROUND_SERVICE_ID = 111;
     //   private final int setTime;

        private int elapsedTime;


        @RequiresApi(api = Build.VERSION_CODES.O)
        protected ForegroundTimerService(ListTimersParcel parcel, PendingIntent pendingIntent){

            this.pendingIntent = pendingIntent;

            timerViewModelList = (ArrayList<Entry>) generateEntryList(parcel);

            timeViewModel.setTimeState( new TimeState(getSummationTime(timerViewModelList)));


            accumulation(timerViewModelList);
            currentActiveTime = timerViewModelList.get(1);

                        for(Entry n : timerViewModelList){
                Log.d("serviceTest", "e. "+n.timeAccumulated);
            }



        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public Notification preSetNotification(){
            return makeNotification("Q", new Entry(), pendingIntent);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public synchronized Notification createTimer(NotificationManager mgr){

            AtomicReference<Notification> notification = new AtomicReference<>(preSetNotification());

            int setTime  = getSummationTime(timerViewModelList);

                timeViewModel.setServiceTask( (time -> {

                    int elapsedTime = setTime - time;

                    if(currentActiveTime.timeElapsed(elapsedTime) || elapsedTime == setTime){
                   currentActiveTime = getNextActiveProcessTime(timerViewModelList);

                    Log.d("serviceTest","here!");
                }
                     //rebuild notification here
                            notification.set(makeNotification(new TimeState(time).getTimeFormat(), currentActiveTime,pendingIntent));
                            mgr.notify(FOREGROUND_SERVICE_ID, notification.get());

            }));



            return notification.get();
        }



    }





}
