package com.example.checkListApp.time_management;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.Preference;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.time_management.parcel.ListTimersParcel;
import com.example.checkListApp.time_management.utilities.KeyHelperClass;
import com.example.checkListApp.time_management.utilities.ListTimerUtility;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entry_management.MainListTimeProcessHandler;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.settings.PreferenceHelper;
import com.example.checkListApp.ui.main.shake_detector.ShakeDetector;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class TimerService extends LifecycleService implements SensorEventListener {

   public static int activeTimeIndex=0;
   private Intent serviceIntent;
   public static MutableLiveData<Boolean> reset = new MutableLiveData<>(false);

   NotificationManager notificationManager;
   private final int FOREGROUND_SERVICE_ID = 111;

    private final static ListTimerUtility timerUtility = MainListTimeProcessHandler.timerUtility;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravit
    private ShakeDetector mShakeDetector;

    private PreferenceHelper preferenceHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        Observer<Boolean> onReset = aBoolean -> {

            if(aBoolean) {
//                stopSelf();

                stopService(serviceIntent);
                notificationManager.cancel(FOREGROUND_SERVICE_ID);
                reset.postValue(false);
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


        preferenceHelper = MainActivity.preferenceHelper;

        ListTimersParcel parcelableList = intent.getParcelableExtra(KeyHelperClass.TIME_PARCEL_DATA);

        activeTimeIndex = parcelableList.getActiveTimeIndex();


        ForegroundTimerService foregroundTimerService =
                new ForegroundTimerService(this, parcelableList, pendingIntent);

         notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);




//      if(!parcelableList.globalSetTimer.equals("00:00:00"))

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        mShakeDetector = new ShakeDetector();

        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

                Log.d("shakeTest","shake");

                int MODE = preferenceHelper.shakeToggleTimerMode();

                switch(MODE){

                    case 0 :{
                        if(!MainListTimeProcessHandler.timerViewModel.isToggled())
                            MainListTimeProcessHandler.timerViewModel.toggleTime();
                    }break;

                    case 1 : {
                        MainListTimeProcessHandler.timerViewModel.toggleTime();
                    }break;

                    case 2 : {

                    }break;

                    default:break;

                }


            }

        });

        mSensorManager.registerListener(mShakeDetector, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());



        startForeground(FOREGROUND_SERVICE_ID,
                foregroundTimerService.createTimer(notificationManager));


        return (START_NOT_STICKY);
    }


    //https://developer.android.com/guide/components/broadcasts
    //https://developer.android.com/training/notify-user/build-notification.html#java

    public Notification makeNotification(int countTime, int elapsedTimeNV, TimerViewModel timeViewModel, Entry entry, PendingIntent pendingIntent) {

        String channel = createChannel();
        AtomicReference<NotificationCompat.Builder> mBuilder = new AtomicReference<>(new NotificationCompat.Builder(this, channel));
        BuilderDataHelper dataHelper = new BuilderDataHelper(mBuilder.get(), pendingIntent, timeViewModel);


        Log.d("serviceTest",""+countTime + " "+ entry);

//        if(countTime <= 1 && timeViewModel.getRepeaterTime() <= 0
//        ) {
//                mBuilder.set(builderDismissive(dataHelper, countTime));
//        }
//        else {
                mBuilder.set(builderNormal(dataHelper, entry, elapsedTimeNV, countTime));
//        }


        return mBuilder.get()
                .setPriority(2)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build(); // foregroundTimerService.createTimer(notification);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

//        if (mAccel > 11) {
//
//
//        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private  class BuilderDataHelper{

        NotificationCompat.Builder builder;
        PendingIntent pendingIntent;
        TimerViewModel timerViewModel;

        public BuilderDataHelper(NotificationCompat.Builder builder, PendingIntent pendingIntent, TimerViewModel timerViewModel) {
            this.builder = builder;
            this.pendingIntent = pendingIntent;
            this.timerViewModel = timerViewModel;
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

//        int decimalEntrySetTime = new TimeState(expireTime.getTimeNumberValue()).getTimeNumberValueDecimalTruncated();

        int decimalEntrySetTime = entry.timeAccumulated;//expireTime.getTimeNumberValueDecimalTruncated();

        int timeRemainder = new TimeState().getValueAsTimeTruncated(decimalEntrySetTime - elapsedTimeNV);


        String textTimeRemainder = (entry.onTogglePrimer.getValue()) ? "paused" :  new TimeState(timeRemainder).getTimeFormat() ;

        String toggleButtonText = (dataHelper.timerViewModel.isToggled()) ? "Pause" : "Resume";


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
                .setSubText(dataHelper.timerViewModel.getRepeaterTime() + " " + new TimeState(countTime).getTimeFormat())
                .setContentText(entry.textEntry.getValue() + " " + textTimeRemainder);

    }


    private NotificationCompat.Builder builderDismissive(BuilderDataHelper dataHelper){

        Intent resetTimeIntent = new Intent(this, TimerBroadcastReceiver.class);
        resetTimeIntent.setAction(KeyHelperClass.BROADCAST_ACTION_DISMISS);

        PendingIntent resetTimePendingIntent =
                PendingIntent.getBroadcast(this,0, resetTimeIntent,0);


        return dataHelper.builder.setContentIntent(dataHelper.pendingIntent)
                .setSmallIcon(R.drawable.outline_timer_black_48)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.outline_add_circle_black_48, "Dismiss",
                        resetTimePendingIntent)
                .setContentTitle("Countdown Timer")
                .setAutoCancel(true)
                .setOngoing(false)
                .setColor(Color.BLUE)
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


    class ForegroundTimerService {

        private final TimerService timerService;
        private final PendingIntent pendingIntent;
        private final TimerViewModel timeViewModel = MainListTimeProcessHandler.timerViewModel;
        private   ArrayList<Entry> timerViewModelList;

        private final int FOREGROUND_SERVICE_ID = 111;

        private int elapsedTime;


        protected ForegroundTimerService(TimerService timerService, ListTimersParcel parcel, PendingIntent pendingIntent) {
            this.timerService = timerService;

            this.pendingIntent = pendingIntent;

            timerViewModelList = (ArrayList<Entry>) timerUtility.generateEntryList(parcel);

        }

        public ArrayList<Entry> getTimerViewModelList() {
            return timerViewModelList;
        }

        public Notification preSetNotification(int setTime, Entry entry) {
            return timerService.makeNotification(setTime, 0, timeViewModel, entry, pendingIntent);
        }


        public Notification createTimer(NotificationManager mgr) {

            AtomicInteger _setTime = new AtomicInteger(timeViewModel.getNumberValueTime());

            AtomicReference<Notification> notification =
                    new AtomicReference<>(
                            preSetNotification(_setTime.get() ,timerUtility.previousActiveTime));


            timeViewModel.setServicePostExecute(() -> {

                reset.postValue(true);
//                String channel = createChannel();
//                AtomicReference<NotificationCompat.Builder> mBuilder = new AtomicReference<>(new NotificationCompat.Builder(getBaseContext(), channel));
//                BuilderDataHelper dataHelper = new BuilderDataHelper(mBuilder.get(), pendingIntent, timeViewModel);
//
//                notification.set(builderDismissive(dataHelper).build());

            });

            //TODO: BUG HERE RAPIDLY RESETING TIME
            //TODO: Repeater doesn't work well with single entry
            //TODO  FIRST ENTRY IS TOGGLE AND IS NOT GETTING NEXT INDEX
            //ON SECOND GO AROUND



                timeViewModel.setServiceTask(((elapsedTimeVolatile, countTime, elapsedTimeN) -> {

                    elapsedTime = _setTime.get() - countTime;

                    //rebuild notification here
                    notification.set(timerService.makeNotification(
                            countTime,
                            elapsedTimeN
                            , timeViewModel
                            , timerUtility.previousActiveTime
                            , pendingIntent));

                    mgr.notify(FOREGROUND_SERVICE_ID, notification.get());
            }));

            return notification.get();
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
