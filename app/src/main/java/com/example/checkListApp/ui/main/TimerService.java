package com.example.checkListApp.ui.main;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.ui.main.entries.Entry;

import java.util.ArrayList;

public class TimerService extends Service {

    public final static String SERVICE_KEY = "SERVICE_KEY";
    private final static String CHANNEL_ID = "TADONE_TIMER";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ParcelCountDownTimer<Entry> input = intent.getParcelableExtra(SERVICE_KEY);
        ArrayList<Entry> checkList = input.checkList;


        Intent notificationIntent =  new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);



       Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("time status: ")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(checkList.get(1).textTemp)
                .setContentIntent(pendingIntent).build();

       startForeground(1,notification);

       return START_NOT_STICKY;


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
