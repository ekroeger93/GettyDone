package com.example.checkListApp.timer;

import android.annotation.SuppressLint;

public class TimeState {

       final int hours;
       final int minutes;
       final int seconds;
       final int rawValue;

        public TimeState(int value) {//assuming 6 length

            rawValue = value;

            hours = (value / 10000) % 100;
            minutes = (value / 100) % 100;
            seconds = (value) % 100;

        }

        @SuppressLint("DefaultLocale")
        public String getTimeFormat() { return String.format("%02d:%02d:%02d", hours, minutes, seconds); }

        public String getTimeUnformatted(){ return ""+rawValue; }


}

