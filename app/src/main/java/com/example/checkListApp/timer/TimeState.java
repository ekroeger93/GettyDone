package com.example.checkListApp.timer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;

public class TimeState {

     protected int hours;
     protected int minutes;
     protected int seconds;
     private final int rawValue;

     public TimeState(){
         this.rawValue = 0;
     }

     public TimeState(int value) {//assuming 6 length

            rawValue = value;

            hours = (value / 10000) % 100;
            minutes = (value / 100) % 100;
            seconds = (value) % 100;

            //carry over
            if(seconds >= 60){
                seconds = Math.abs(60 - seconds);
                minutes +=1;
            }

            if(minutes >= 60){
                minutes = Math.abs(60 - minutes);
                hours +=1;
            }

        }



        public TimeState(String time) throws NumberFormatException{
                this((Integer.parseInt(time.replace(":", "").trim())));
        }


        public int timeTruncated(){

            double nanos = rawValue * Duration.ofSeconds(1).toNanos();
            Duration dur = Duration.ofNanos(Math.round(nanos));

            long hh = dur.toHours();
            int mm = (int) dur.toMinutes()%60;
            int ss = (int) (dur.toMillis()/1000)%60;

            try {
                return Integer.parseInt(String.format(Locale.getDefault(), "%02d%02d%02d", hh, mm, ss));
            }
            catch (NumberFormatException e){
                return 0;
            }

        }

    public TimeState addTimeState(TimeState timeState){

            TimeState newTime = this;

            newTime.hours +=timeState.hours;
            newTime.minutes +=timeState.minutes;
            newTime.seconds +=timeState.seconds;

            if(newTime.seconds >= 60){
                newTime.seconds = Math.abs(60 - newTime.seconds);
                newTime.minutes +=1;
            }

            if(newTime.minutes >= 60){
                newTime.minutes = Math.abs(60 - newTime.minutes);
                newTime.hours +=1;
            }

            return newTime;

        }



        @SuppressLint("DefaultLocale")
        public String getTimeFormat() { return String.format("%02d:%02d:%02d", hours, minutes, seconds); }

        public String getTimeUnformatted(){ return ""+hours+minutes+seconds; }

        public int getValueAsTimeTruncated(int value){

            double nanos = value * Duration.ofSeconds(1).toNanos();
            Duration dur = Duration.ofNanos(Math.round(nanos));

            long hh = dur.toHours();
            int mm = (int) dur.toMinutes()%60;
            int ss = (int) (dur.toMillis()/1000)%60;

            try {
                return Integer.parseInt(String.format(Locale.getDefault(), "%02d%02d%02d", hh, mm, ss));
            }
            catch (NumberFormatException e){
                return 0;
            }


        }

        public int getTimeNumberValue(){ return Integer.parseInt(getTimeFormat().replace(":","").trim());}



        public int getTimeNumberValueDecimalTruncated(){

            //so example:
            // 01:03 would be 63 (or total seconds)

            Duration duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            return (int) duration.getSeconds();
        }




}

