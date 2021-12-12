package com.example.checkListApp.timer;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CountDownTimerAsync {
    //TODO: this maybe causing a small memory leak

    /*

    Singletons cannot be gc, this is very likely a memory leak however,

    I used singleton here because I really don't need more than one thread with a running timer
    and I figure the app would be more in sync when other classes used it.

    As a design choice it doesn't make sense (to me) to have multiple running timers; I
    really can't think of a use case scenario

    from canary: 624 B - 10 objects is negligible

     */



        private static final CountDownTimerAsync instance = new CountDownTimerAsync();

        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        private TimeToggler timeToggler;

        private TemporalAccessor futureTime;

        private CountDownTask countDownTask = timeValue -> {

        };

        private ServiceTask serviceTask = timeValue -> {

        };

        private PostExecute postExecute = () -> {

        };

        private String runTime;

        private int numberTime;
        private int setTime;
        private int elapsedTime;
        private int countDownTime;



        public CountDownTimerAsync() {
        }


        static public CountDownTimerAsync getInstance() {
         return instance; }

         static public CountDownTimerAsync getInstanceToToggle(TimeToggler timeToggler){
            instance.timeToggler = timeToggler;
            return instance;
         }


        public void setCountDownTask(CountDownTask countDownTask) { this.countDownTask = countDownTask; }

        public void setServiceTask(ServiceTask serviceTask){ this.serviceTask = serviceTask; }

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

            setTime = timeState.getTimeNumberValueDecimalTruncated();

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
                        countDownTime = (int) countingDown.getSeconds();

                        elapsedTime = (int) (setTime - (countingDown.getSeconds()));

                        if(countingDown.getSeconds() <= 0) {

                            countDownTask.execute(0);
                            serviceTask.execute(0);

                            postTimeExpire();
                            break;
                        }

                        countDownTask.execute(elapsedTime);
                        serviceTask.execute(elapsedTime);

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


        public int getElapsedTime(){return  elapsedTime;}

        public int getNumberTime(){ return numberTime; }

        public String getRunTime(){ return runTime; }

        public int getCountDownTime() { return countDownTime; }

    @FunctionalInterface
        public interface PostExecute{
            void execute();
        }

        @FunctionalInterface
        public interface CountDownTask{
            void execute(int timeValue);
        }

        @FunctionalInterface
        public interface ServiceTask{
            void execute(int timeValue);
        }




    }
