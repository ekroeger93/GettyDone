package com.example.checkListApp.ui.main.EntryManagement.Record;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

public class Record {

    private  int numberOfGoals ;
    private  String currentDate;

    Record(int number){
        numberOfGoals = number;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        currentDate = sdf.format(new Date());
    }

    Record(int number, String date){
        numberOfGoals = number;
        currentDate =  date;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public int getNumberOfGoals() {
        return numberOfGoals;
    }
}
