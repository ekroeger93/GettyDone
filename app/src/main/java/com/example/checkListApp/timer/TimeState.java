package com.example.checkListApp.timer;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;

public class TimeState {

     protected int hours;
     protected int minutes;
     protected int seconds;
     private final int rawValue;

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

        public int getTimeNumberValue(){ return Integer.parseInt(getTimeFormat().replace(":","").trim());}


        @RequiresApi(api = Build.VERSION_CODES.O)
        public int getTimeNumberValueDecimalTruncated(){

            //so example:
            // 01:03 would be 63 (or total seconds)

            Duration duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            return (int) duration.getSeconds();
        }




}

