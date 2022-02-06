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


    public int getActiveProcessTimeIndex(){
        return timerUtility.activeProcessTimeIndex;
    }

    public void subAccumulation(ArrayList<Entry> list){ timerUtility.accumulation(list);}

    public void subAccumulation(){timerUtility.accumulation(mainFragment.getCheckList());
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

                if(timerUtility.currentActiveTime == null) timerUtility.currentActiveTime = checkList.get(1);

                updateSecondaryTimer(time);
                processTimerTask(time);

            };

        };


        binding.timerExecuteBtn.setOnClickListener(view -> {

            //TODO: dont forget the move sublisting json is screwed up
            //this is a temp fix
//            mainFragment.sanityCheckSubList();

            updateEntryUI();


            setTimer();




//            if(timerUtility.currentActiveTime.isSubEntry){
////                setSubTextToParent();
//            }


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

            mainFragment.getActivity().stopService(mainFragment.getServiceIntent());

            return  true;
        });


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
            timerUtility.accumulation(checkList);

            int summationTime = timerUtility.getSummationTime(checkList);

            String setTime = new TimeState(summationTime).getTimeFormat();
            timerViewModel.setCountDownTimer(setTime);


            timerUtility.revertTimeIndex();
            timerUtility.revertSubTimeIndex();

           timerUtility.currentActiveTime = checkList.get(1);

            for(Entry entry: checkList){ entry.checked.postValue(false); }

        }else{

            timerUtility.accumulation(checkList);

            timerUtility.getSummationTime(checkList);

        }
    }

    public void updateSecondaryTimer(int elapsedTime){

        if (mainFragment.getActivity() != null && getContext() !=null) {
            mainFragment.getActivity().runOnUiThread(() -> {
                binding.timeTextSecondary.setText(
                        timerUtility.currentActiveTime.getActiveTimeLabel(elapsedTime));
            });
        }
    }

    public void displayEntryToast(){

        if (mainFragment.getActivity() != null && getContext() !=null) {
            mainFragment.getActivity().runOnUiThread(() -> {

                String message = timerUtility.currentActiveTime.textEntry.getValue();

                Toast toast = Toast.makeText(getContext(), message + " done!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();


            });
        }

    }

    public void scrollToPosition(int scroll){


        mainFragment.getActivity().runOnUiThread(() -> {

            MainFragment.scrollPosition(scroll);

        });
    }



    public void setSubTextToParent(){

        if(timerUtility.currentActiveTime.isSubEntry){

            String subText = timerUtility.currentActiveTime.textTemp;

            checkList.get(timerUtility.activeProcessTimeIndex)
                    .getViewHolder().textView.setText(
                    subText);
        }

    }


    public void processTimerTask(int elapsedTime) {

        Log.d("subListingTest",
                timerUtility.currentActiveTime.textTemp +
                        " elapse: " + elapsedTime +
                        " acc:" + timerUtility.currentActiveTime.timeAccumulated +
                        " index: " + timerUtility.activeProcessTimeIndex +
                        " subIndex: " + timerUtility.subActiveProcessTimeIndex +
                        " isSub: " + timerUtility.currentActiveTime.isSubEntry);



        if (!timerUtility.currentActiveTime.onTogglePrimer.getValue()) {


            if (timerUtility.currentActiveTime.timeElapsed(elapsedTime)) {

                updateEntryUI();

                mainFragment.playAudio(timerUtility.currentActiveTime.getSelectAudio());



                if (timerUtility.currentActiveTime.numberValueTime != 0) {


                    timerUtility.currentActiveTime = timerUtility.getNextActiveProcessTime(checkList);

                    scrollToPosition(timerUtility.activeProcessTimeIndex);

                }


            }


        }


        else {

            updateEntryUI();
            scrollToPosition(timerUtility.activeProcessTimeIndex);

            timerViewModel.toggleTime();

            mainFragment.playAudio(timerUtility.currentActiveTime.getSelectAudio());

            timerUtility.currentActiveTime = timerUtility.getNextActiveProcessTime(checkList);





        }




    }

    public void endOfTimerTask( CountDownTimerAsync.CountDownTask countDownTask){

        mainFragment.playAudio(timerUtility.currentActiveTime.getSelectAudio());

        try {
            timerUtility.currentActiveTime.getViewHolder().checkOff();
        }catch (NullPointerException e){
         e.printStackTrace();
        }
        finally {

            timerUtility.revertTimeIndex();
            timerUtility.revertSubTimeIndex();

            timerUtility.currentActiveTime = checkList.get(1);
            timerViewModel.setTaskCustom(countDownTask);
        }

    }


    public void updateEntryUI(){

        try {
            if (timerUtility.parentEntry != null) {

                timerUtility.parentEntry.getViewHolder().textView
                        .setText(timerUtility.currentActiveTime.textEntry.getValue());

            } else {

                timerUtility.currentActiveTime.getViewHolder().textView.setText(timerUtility.currentActiveTime.textEntry.getValue());

            }
        }catch (NullPointerException e){


        }

    }





}
