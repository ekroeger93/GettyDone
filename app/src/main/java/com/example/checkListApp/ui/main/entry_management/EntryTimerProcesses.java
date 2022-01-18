package com.example.checkListApp.ui.main.entry_management;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.timemanagement.MainTimerView;
import com.example.checkListApp.timer.CountDownTimerAsync;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.data_management.ListUtility;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public class EntryTimerProcesses {

    MainFragment mainFragment;

    ListUtility listUtility;
    ArrayList<Entry> checkList;
    MainTimerView mainTimerView;
    MainFragmentBinding binding;

    Context context;

    public Context getContext() { return context; }

    public EntryTimerProcesses(MainFragment mainFragment){
        this.mainFragment = mainFragment;

        mainTimerView = mainFragment.getMainTimerView();
        listUtility = mainFragment.getListUtility();
        checkList = mainFragment.getCheckList();
        binding = mainFragment.binding;
        context = mainFragment.getContext();


    }

    public void configureMainTimer(){


        //update text timer based on current scroll selected position

        CountDownTimerAsync.CountDownTask countDownTask = time -> {
            {

                int elapsedTime = listUtility.getSummationTime(checkList) - time;

                if(listUtility.currentActiveTime == null) listUtility.currentActiveTime = checkList.get(1);

                processTimerTask(elapsedTime);


            };

        };


        binding.timerExecuteBtn.setOnClickListener(view -> {


            setTimer();

            if(MainTimerView.mainTimerViewModel.isToggled()) {
                binding.timerExecuteBtn.setBackground(
                        ContextCompat.getDrawable(
                                getContext(),
                                R.drawable.outline_play_circle_filled_black_48
                        ));
            }else{
                binding.timerExecuteBtn.setBackground(
                        ContextCompat.getDrawable(
                                getContext(),
                                R.drawable.outline_pause_circle_filled_black_48
                        ));

            }

            if( listUtility.getSummationTime(checkList)  > 0 ) {

                mainFragment.startService();

                if(binding.repeatTimer.getText().toString().isEmpty()){
                    binding.repeatTimer.setText("0");
                }

                int repeater = Integer.parseInt(binding.repeatTimer.getText().toString());
                MainTimerView.mainTimerViewModel.setRepeaterTime(repeater);


                mainFragment.getTimerRunning().postValue(true);

                MainTimerView.mainTimerViewModel.setTaskCustom(countDownTask);

                MainTimerView.mainTimerViewModel.toggleTime();

            }

        });

        binding.timerExecuteBtn.setOnLongClickListener(view -> {

            //hideButtons(false);


            MainTimerView.mainTimerViewModel.resetAbsolutely();
            mainFragment.getTimerRunning().postValue(false);

//        if(isMyServiceRunning(TimerService.class))
            mainFragment.getActivity().stopService(mainFragment.getServiceIntent());

            return  true;
        });

        //update time of both View and ViewModel
        mainTimerView.setObserverForMainTextTime(binding.timeTextMain,mainFragment.getViewLifecycleOwner());

        //set a post execution after timer expires, proceeds to next Entry
        mainTimerView.setPostExecute(() -> {

            displayEntryToast();

            if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= -1) {

                //go back to top of list
                MainTimerView.mainTimerViewModel.resetTimeState();
                mainFragment.getTimerRunning().postValue(false);

            }
            else {

                endOfTimerTask(countDownTask);

            }



        });


    }

    public int setTimer(){

        checkList = mainFragment.getCheckList();

        if(MainTimerView.mainTimerViewModel.getNumberValueTime() == 0) {
            int summationTime = listUtility.getSummationTime(checkList);
            String setTime = new TimeState(summationTime).getTimeFormat();
            MainTimerView.mainTimerViewModel.setCountDownTimer(setTime);

            listUtility.accumulation(checkList);

            listUtility.revertTimeIndex();
            listUtility.currentActiveTime = checkList.get(1);

            for(Entry entry: checkList){ entry.checked.postValue(false); }

            return summationTime;

        }else{
            listUtility.accumulation(checkList);

            return listUtility.getSummationTime(checkList);
        }
    }

    public void displayEntryToast(){

        if (mainFragment.getActivity() != null && getContext() !=null) {
            mainFragment.getActivity().runOnUiThread(() -> {

                String message = checkList.get(listUtility.activeProcessTimeIndex-1).textEntry.getValue();

                mainFragment.scrollPosition(listUtility.activeProcessTimeIndex);

                Toast toast = Toast.makeText(getContext(), message + " done!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();


            });
        }

    }

    public void processTimerTask(int elapsedTime){

        if(!listUtility.currentActiveTime.onTogglePrimerTemp){
            if (listUtility.currentActiveTime.timeElapsed(elapsedTime)) {

                //selectedAudio[checkList.get(listUtility.activeProcessTimeIndex).getSelectAudio()].start();

                mainFragment.playAudio(listUtility.currentActiveTime.getSelectAudio());


                displayEntryToast();

                if (listUtility.currentActiveTime.numberValueTime != 0) {

                    //if repeater time is 0 check off
                    if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= 0)
                        listUtility.currentActiveTime.getViewHolder().checkOff();

                    listUtility.currentActiveTime = listUtility.getNextActiveProcessTime(checkList);
                }


            }
        }
        else{
            MainTimerView.mainTimerViewModel.toggleTime();

            if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= 0) {
                listUtility.currentActiveTime.getViewHolder().checkOff();
            }

            listUtility.currentActiveTime = listUtility.getNextActiveProcessTime(checkList);

        }

    }

    public void endOfTimerTask( CountDownTimerAsync.CountDownTask countDownTask){

//        selectedAudio[checkList.get(listUtility.activeProcessTimeIndex).getSelectAudio()].start();

        mainFragment.playAudio(listUtility.currentActiveTime.getSelectAudio());

        listUtility.currentActiveTime.getViewHolder().checkOff();

        listUtility.revertTimeIndex();
        listUtility.currentActiveTime = checkList.get(1);
        MainTimerView.mainTimerViewModel.setTaskCustom(countDownTask);


    }




}
