package com.example.checkListApp.timer;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Time;
import java.time.DateTimeException;
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

        private ServiceTask serviceTask = (elapsedTimeVolatile, countTime, elapsedTime) -> {

        };

        private PostExecute postExecute = () -> {

        };

        private PostExecute servicePostExecute= () -> {

        };

        private String runTime;

        private int countTime;


        //the set time updates itself to new set time on pause
        private int setTimeVolatile;
        //this resets when timer is toggled
        //which is necessary to retain time when resumed from pause
        private int elapsedTimeVolatile;

        private int setTime;
        private int elapsedTime;

        private int countDownTime;

        //Volatile
        private int repeater = -1 ;
        private TimeState holdingState;

        public void setRepeater(int repeater) {
            if(this.repeater == -1)
            this.repeater = repeater;
        }
        public int getRepeater() {
            return repeater;
        }

        public CountDownTimerAsync() { }

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

        public void setServicePostExecute(PostExecute postExecute) { this.servicePostExecute = postExecute;}

        public void setTimer(TimeState timeState){
            setTime = timeState.getTimeNumberValueDecimalTruncated();
        }

        public void setTimerVolatile(TimeState timeState){
            futureTime = LocalDateTime.now()
                    .plusHours(timeState.hours)
                    .plusMinutes(timeState.minutes)
                    .plusSeconds(timeState.seconds)
                    .atZone(ZoneId.systemDefault()).toInstant();

            setTimeVolatile = timeState.getTimeNumberValueDecimalTruncated();

            if (holdingState == null) holdingState = timeState;

        }


        public void execute() {
            executor.execute(new Runnable() {

                @Override
                public void run() {

                    Duration countDownTime;
                    Locale locale = Locale.getDefault();
//                    countDownTime = Duration.between(Instant.now(), Instant.from(futureTime));

                    while (timeToggler.isToggleTimeON()) {

                        try {

                            //TODO: BUG HERE futureTime TemporalAccessor
                            countDownTime = Duration.between(Instant.now(), Instant.from(futureTime));
                        }catch (DateTimeException e){
                            Log.d("BUG_",".."+e.getCause().getMessage());
                            break;
                        }

                        long HH = countDownTime.toHours();
                        long MM = countDownTime.toMinutes()%60;
                        long SS = (countDownTime.toMillis()/1000)%60;

                        countTime = Integer.parseInt( String.format(locale,"%02d%02d%02d", HH, MM, SS));
                        runTime = String.format(locale,"%02d:%02d:%02d", HH, MM, SS);
//                        CountDownTimerAsync.this.countDownTime = (int) countDownTime.getSeconds();


                        elapsedTimeVolatile = (int) (setTimeVolatile - (countDownTime.getSeconds()));
                        elapsedTime = (int) (setTime -countDownTime.getSeconds());

                        if(countDownTime.getSeconds() <= 0) {

                            if(repeater <= 0) {

                                countDownTask.execute(0);
                                serviceTask.execute(0,0,0);
                                repeater = -1;
                                holdingState = null;
                                postTimeExpire();
                                break;
                            }else {
                                repeater--;
                                setTimerVolatile(holdingState);
//                                countDownTask.execute(elapsedTime);
//                                serviceTask.execute(elapsedTime);
                                postExecute.execute();
                                servicePostExecute.execute();
                            }



                        }


                        //TODO will have to pass int the countDownTime.getSeconds
                        //every time this pauses elaspedTime will start from 0, which is meant to
                        //but need a consistant elapsed

//                        Log.d("serviceTest", "el "+elapsedTime +
//                                " elV "+elapsedTimeVolatile+
//                                " setV "+ setTimeVolatile + " set "+setTime);



                        countDownTask.execute(elapsedTimeVolatile);
                        serviceTask.execute(elapsedTimeVolatile, countTime, elapsedTime);

                    }
                }


            });

        }

        public void postTimeExpire(){
            postExecute.execute();
            servicePostExecute.execute();
            timeToggler.shutDown();
        }

        public void shutdown(){

            timeToggler.shutDown();
            executor.shutdownNow();

        }


        public int getElapsedTimeVolatile(){return elapsedTimeVolatile;}

        public int getElapsedTime(){return elapsedTime;}

        public int getCountTime(){ return countTime; }

        public String getRunTime(){ return runTime; }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void resetAll(){
            futureTime =  LocalDateTime.now();
            setTimeVolatile = 0;
            elapsedTimeVolatile = 0;
            countDownTime = 0;
            countTime = 0;
            runTime = "00:00:00";

        }

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
            void execute(int timeElapsedValue, int countTimeValue, int timeElapsedNonVolatile);
        }
//int timeElapsedValue, int countTimeValue, int timeElapsedNonVolatile



    }
