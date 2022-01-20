package com.example.checkListApp.ui.main.entry_management;

import com.example.checkListApp.time_management.TimerViewModel;
import com.example.checkListApp.time_management.utilities.ListTimerUtility;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public class SubListProcessHandler{

    ArrayList<Entry> subList = new ArrayList<>();
    TimerViewModel subTimerModel = new TimerViewModel();
    SubListTimerUtility listTimerUtility = new SubListTimerUtility();






    static class SubListTimerUtility extends ListTimerUtility {

        int activeProcessTimeIndex = 0;
        Entry currentActiveTime;

        @Override
        public void accumulation(ArrayList<Entry> list) {

            for (int i = 0; i <= list.size() - 1; i++) {

                if (i != 0) {

                    list.get(i).setTimeAcclimated(list.get(i - 1).timeAccumulated);
                }

            }

        }

        @Override
        public int getSummationTime(ArrayList<Entry> list) {

            int sum = 0;

            for (Entry entry : list) {

                entry.setNumberValueTime(entry.countDownTimer.getValue());

                int value = entry.getNumberValueTime();

                sum += value;

            }

            return sum;

        }

        @Override
        public Entry getCurrentActiveTime() {
            return currentActiveTime;
        }

        @Override
        public Entry getNextActiveProcessTime(ArrayList<Entry> list) {


            int size = list.size() - 1;

            if (activeProcessTimeIndex < size) {
                activeProcessTimeIndex++;

                return list.get(activeProcessTimeIndex);
            } else {
                activeProcessTimeIndex = 0;
            }

            return list.get(size - 1);


        }

        @Override
        public void revertTimeIndex() {
            activeProcessTimeIndex = 0;
        }

        @Override
        public int getActiveProcessTimeIndex() {
            return activeProcessTimeIndex;
        }


    }


}
