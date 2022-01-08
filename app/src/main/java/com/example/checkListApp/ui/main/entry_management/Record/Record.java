package com.example.checkListApp.ui.main.entry_management.Record;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {

    private final int numberOfGoals ;
    private final String currentDate;
    private final int currentWeekOfYear;

    Record(int number){
        numberOfGoals = number;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        currentDate = sdf.format(new Date());
        currentWeekOfYear = sdf.getCalendar().getWeekYear();
    }

    Record(int number, String date, int currentWeekOfYear){
        numberOfGoals = number;
        currentDate =  date;
        this.currentWeekOfYear = currentWeekOfYear;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public int getNumberOfGoals() {
        return numberOfGoals;
    }

    public int getCurrentWeekOfYear() {
        return currentWeekOfYear;
    }
}
