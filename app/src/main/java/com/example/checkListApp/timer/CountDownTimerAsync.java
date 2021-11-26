package com.example.checkListApp.timer;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownTimerAsync {

        private static final CountDownTimerAsync instance = new CountDownTimerAsync();

        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final TimeToggler timeToggler = TimeToggler.getTimeToggler();

        private TemporalAccessor futureTime;
        private CountDownTask countDownTask;

        private String runTime;
        private int numberTime;

        PostExecute postExecute;

        private CountDownTimerAsync() {
        }

        static public CountDownTimerAsync getInstance() {
         return instance; }

        public void setCountDownTask(CountDownTask countDownTask) { this.countDownTask = countDownTask; }

        public void setPostExecute(PostExecute postExecute){
           this.postExecute = postExecute;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setTimer(TimeState timeState){
            futureTime = LocalDateTime.now()
                    .plusHours(timeState.hours)
                    .plusMinutes(timeState.minutes)
                    .plusSeconds(timeState.seconds)
                    .atZone(ZoneId.systemDefault()).toInstant();
        }

        public void execute() {
            executor.execute(new Runnable() {

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {

                    Duration countingDown;
                    Locale locale = Locale.getDefault();

                    while (timeToggler.isToggleTimeON()) {

                        countingDown = Duration.between(Instant.now(), Instant.from(futureTime));

                        long HH = countingDown.toHours();
                        long MM = countingDown.toMinutes()%60;
                        long SS = (countingDown.toMillis()/1000)%60;

                        numberTime = Integer.parseInt( String.format(locale,"%02d%02d%02d", HH, MM, SS));
                        runTime = String.format(locale,"%02d:%02d:%02d", HH, MM, SS);

                        if(countingDown.getSeconds() < 0) {
                            postTimeExpire();
                            break;
                        }

                        countDownTask.execute();

                    }
                }


            });

        }

        public void postTimeExpire(){
            postExecute.execute();
            timeToggler.shutDown();
        }

        public void shutdown(){

            timeToggler.shutDown();
            executor.shutdownNow();

        }

        public int getNumberTime(){ return numberTime; }

        public String getRunTime(){ return runTime; }


        @FunctionalInterface
        public interface PostExecute{

            void execute();
        }

        @FunctionalInterface
        public interface CountDownTask{
            void execute();
        }


    }
