package com.example.checkListApp.ui.main.entry_management;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.time_management.TimerViewModel;
import com.example.checkListApp.time_management.utilities.ListTimerUtility;
import com.example.checkListApp.timer.CountDownTimerAsync;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public class MainListTimeProcessHandler {

    private final MainFragment mainFragment;

    public final static TimerViewModel timerViewModel = new TimerViewModel();
    public final static ListTimerUtility timerUtility = new ListTimerUtility();

    //have one process handler swap out timerViewModel, listUtility, checklist
    //have two process handler switch between them


    /*
    the entry on Main list should have a summation of its sublist, which is
    added to the timerViewModel of the main list.

    the SubListProcessHandler will initiate when the MainList arrives at that
    entry, it will be simply called and expire accordingly (may need to terminate thread)

    could use the mainTimer instead (timerViewModel) and check the accumulation of
    the sub Entries

     */

    public int getActiveProcessTimeIndex(){
        return timerUtility.activeProcessTimeIndex;
    }

    MainFragmentBinding binding;
    ArrayList<Entry> checkList;
    Context context;

    public Context getContext() { return context; }

    public MainListTimeProcessHandler(MainFragment mainFragment){
        this.mainFragment = mainFragment;

        checkList = mainFragment.getCheckList();
        binding = mainFragment.binding;
        context = mainFragment.getContext();


    }

    public void configureMainTimer(){




        //update text timer based on current scroll selected position

        CountDownTimerAsync.CountDownTask countDownTask = time -> {
            {

                int elapsedTime = timerUtility.getSummationTime(checkList) - time;

                if(timerUtility.currentActiveTime == null) timerUtility.currentActiveTime = checkList.get(1);

                processTimerTask(elapsedTime);


            };

        };


        binding.timerExecuteBtn.setOnClickListener(view -> {

            setTimer();
            Log.d("subListingTest","begin: "+timerUtility.currentActiveTime.textTemp);

            if(timerViewModel.isToggled()) {
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

            if( timerUtility.getSummationTime(checkList)  > 0 ) {

                mainFragment.startService();

                if(binding.repeatTimer.getText().toString().isEmpty()){
                    binding.repeatTimer.setText("0");
                }

                int repeater = Integer.parseInt(binding.repeatTimer.getText().toString());
                timerViewModel.setRepeaterTime(repeater);


                mainFragment.getTimerRunning().postValue(true);

                timerViewModel.setTaskCustom(countDownTask);

                timerViewModel.toggleTime();

            }

        });

        binding.timerExecuteBtn.setOnLongClickListener(view -> {

            timerViewModel.resetAbsolutely();
            mainFragment.getTimerRunning().postValue(false);

//        if(isMyServiceRunning(TimerService.class))
            mainFragment.getActivity().stopService(mainFragment.getServiceIntent());

            return  true;
        });

        //update time of both View and ViewModel
//        setObserverForMainTextTime(binding.timeTextMain,mainFragment.getViewLifecycleOwner());

        Observer<String> observer = new Observer() {
            @Override
            public void onChanged(Object o) {
                binding.timeTextMain.setText(timerViewModel.getValueTime());
            }
        };

        timerViewModel.setObserver(observer, mainFragment.getViewLifecycleOwner());



        //set a post execution after timer expires, proceeds to next Entry
        timerViewModel.setPostExecute(() -> {

            displayEntryToast();

            if (timerViewModel.getRepeaterTime() <= -1) {
                endOfTimerTask(countDownTask);

                timerViewModel.resetTimeState();
                mainFragment.getTimerRunning().postValue(false);

            }
            else {

                //go back to top of list
                endOfTimerTask(countDownTask);

            }



        });


    }

    public void setTimer(){

        checkList = mainFragment.getCheckList();

        if(timerViewModel.getNumberValueTime() == 0) {
            int summationTime = timerUtility.getSummationTime(checkList);
            String setTime = new TimeState(summationTime).getTimeFormat();
            timerViewModel.setCountDownTimer(setTime);

            timerUtility.accumulation(checkList);

            timerUtility.revertTimeIndex();
            timerUtility.revertSubTimeIndex();

           timerUtility.currentActiveTime = checkList.get(1);

            for(Entry entry: checkList){ entry.checked.postValue(false); }

        }else{

            timerUtility.accumulation(checkList);
            timerUtility.getSummationTime(checkList);

        }
    }

    public void displayEntryToast(){

        if (mainFragment.getActivity() != null && getContext() !=null) {
            mainFragment.getActivity().runOnUiThread(() -> {

                String message = timerUtility.currentActiveTime.textEntry.getValue();

                mainFragment.scrollPosition(timerUtility.activeProcessTimeIndex);

                Toast toast = Toast.makeText(getContext(), message + " done!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();


            });
        }

    }

    public void processTimerTask(int elapsedTime){

        Log.d("subListingTest",
                timerUtility.currentActiveTime.textTemp+
                        " acc:" + timerUtility.currentActiveTime.timeAccumulated +
                        " index: "+ timerUtility.activeProcessTimeIndex+
                        " subIndex: "+timerUtility.subActiveProcessTimeIndex +
                        " isSub: " + timerUtility.currentActiveTime.isSubEntry);

        if(!timerUtility.currentActiveTime.onTogglePrimerTemp){

            if (timerUtility.currentActiveTime.timeElapsed(elapsedTime)) {

                mainFragment.playAudio(timerUtility.currentActiveTime.getSelectAudio());
                displayEntryToast();

//                Log.d("subListingTest",""+timerUtility.currentActiveTime.getViewHolder());
//                Log.d("subListingTest",""+checkList.get(timerUtility.activeProcessTimeIndex).getViewHolder());
//                Log.d("subListingTest",""+timerUtility.activeProcessTimeIndex);

                if (timerUtility.currentActiveTime.numberValueTime != 0) {

                    //if repeater time is 0 check off

                    try {//POS
                        if (timerViewModel.getRepeaterTime() <= 0)
                            timerUtility.currentActiveTime.getViewHolder().checkOff();
                    }catch (NullPointerException e){
//                        timerUtility.currentActiveTime = checkList.get(getActiveProcessTimeIndex());
//                        timerUtility.currentActiveTime.getViewHolder().checkOff();
                    }

                    timerUtility.currentActiveTime = timerUtility.getNextActiveProcessTime(checkList);

                    Log.d("subListingTest",""+timerUtility.currentActiveTime.textTemp);

                }


            }
        }
        else{
            timerViewModel.toggleTime();

            if (timerViewModel.getRepeaterTime() <= 0) {
                timerUtility.currentActiveTime.getViewHolder().checkOff();
            }

            timerUtility.currentActiveTime = timerUtility.getNextActiveProcessTime(checkList);

            Log.d("subListingTest","T: "+timerUtility.currentActiveTime.textTemp);

        }

    }

    public void endOfTimerTask( CountDownTimerAsync.CountDownTask countDownTask){

//        selectedAudio[checkList.get(listUtility.activeProcessTimeIndex).getSelectAudio()].start();

        mainFragment.playAudio(timerUtility.currentActiveTime.getSelectAudio());

        Log.d("subListingTest", " finished: "+timerUtility.currentActiveTime.textTemp);
        try {
            timerUtility.currentActiveTime.getViewHolder().checkOff();
        }catch (NullPointerException e){

        }

        timerUtility.revertTimeIndex();
        timerUtility.revertSubTimeIndex();

        timerUtility.currentActiveTime = checkList.get(1);
        timerViewModel.setTaskCustom(countDownTask);


    }






}
