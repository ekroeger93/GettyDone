package com.example.checkListApp.ui.main.entry_management.Record;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

public class Record {

    private final int numberOfGoals ;
    private final String currentDate;
    private final int currentWeekOfYear;




    Record(int number){
        numberOfGoals = number;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        currentDate = sdf.format(new Date());
        currentWeekOfYear = getCurrentWeek(getLocalDate());//sdf.getCalendar().week
    }


    public LocalDate getLocalDate(){

        String[] holdDate;
        holdDate = currentDate
                .replace("\"","")
                .replace("\\","")//quit fucking my shit up you curly curtain cunt!
                .split("\\.");

        int year = Integer.parseInt(holdDate[0]);
        int month = Integer.parseInt(holdDate[1]);
        int day = Integer.parseInt(holdDate[2]);

       return LocalDate.of(year,month,day);

    }

    private int getCurrentWeek(LocalDate localDate) {

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return localDate.get(weekFields.weekOfWeekBasedYear());
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
