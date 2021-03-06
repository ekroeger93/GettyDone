package com.gettydone.app.ui.main.entry_management;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.gettydone.app.MainActivity;
import com.gettydone.app.databinding.MainFragmentBinding;
import com.gettydone.app.time_management.TimerViewModel;
import com.gettydone.app.time_management.utilities.ListTimerUtility;
import com.gettydone.app.timer.CountDownTimerAsync;
import com.gettydone.app.timer.TimeState;
import com.gettydone.app.ui.main.MainFragment;
import com.gettydone.app.ui.main.MainUIDynamics;
import com.gettydone.app.ui.main.entry_management.entries.Entry;
import com.gettydone.app.ui.main.entry_management.entries.Spacer;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainListTimeProcessHandler {

    private final MainFragment mainFragment;

    public final static TimerViewModel timerViewModel = new TimerViewModel();
    public final static ListTimerUtility timerUtility = new ListTimerUtility();
    private   CountDownTimerAsync.CountDownTask countDownTask;


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

      countDownTask = time -> {
            {

                if(timerUtility.currentActiveTime == null) timerUtility.currentActiveTime = checkList.get(1);

                timerUtility.updatePreviousActiveProcess(checkList);


                 updateSecondaryTimer(time);
                 processTimerTask(time);

                 updateEntryUI();



            }


      };


        binding.timerExecuteBtn.setOnClickListener(view -> {

            //TODO: dont forget the move sublisting json is screwed up
            //this is a temp fix

            mainFragment.getSubListManager().sanityCheckSubList();

//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//            boolean hintMessageActive = preferences.getBoolean("hintMessagesResetTimer", false);
//            Log.d("prefTest",""+hintMessageActive);


            if(!MainFragment.isTimerRunning() && MainActivity.preferenceHelper.hintTimerMessageIsActive()) {

//                SharedPreferences sharedPref = context.getSharedPreferences(
//                        getString(R.string.hin), Context.MODE_PRIVATE);

                Snackbar snackbar = Snackbar.make(binding.buttonPanel, "hold button to reset timer",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAnchorView(view);

                snackbar.setAction(
                        "Got it!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("hintMessagesResetTimer", false);
                                editor.commit();


                                snackbar.dismiss();
                            }
                        });

                snackbar.show();

            }


            timerViewModel.setObserverToggle(binding.getLifecycleOwner(),binding,getContext());


            setTimer();
            scrollToPosition(timerUtility.activeProcessTimeIndex);

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

//            timerViewModel.resetAbsolutely();
//            mainFragment.getTimerRunning().postValue(false);

            if(MainFragment.isTimerRunning()) {
                MainFragment.resetTime();
                revertEntryUI();

                mainFragment.getActivity().stopService(mainFragment.getServiceIntent());

                return true;
            }else{
                return false;
            }
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


            if (timerViewModel.getRepeaterTime() <= -1) {

                //TODO:BUG TRACKED DOWN HERE
                // java.lang.NullPointerException CANVAS REDRAW!
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

                if(!timerUtility.currentActiveTime.onTogglePrimer.getValue()) {
                    binding.timeTextSecondary.setText(
                            timerUtility.previousActiveTime.getActiveTimeLabel(elapsedTime));
                }else{
                    binding.timeTextSecondary.setText("00:00:01");
                }

            });
        }
    }


    public void scrollToPosition(int scroll){


        try {
            mainFragment.getActivity().runOnUiThread(() -> {

                MainUIDynamics.scrollPosition(scroll);

            });
        }catch (NullPointerException e){
            //means main thread is not active, app has closed but
            //service is still running!
            e.printStackTrace();
            System.out.println("means main thread is not active, app has closed but\n" +
                    "service is still running!");

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

                mainFragment.getMainUIDynamics().playAudio(timerUtility.currentActiveTime.getSelectAudio());



                if (timerUtility.currentActiveTime.numberValueTime != 0) {


                    timerUtility.currentActiveTime = timerUtility.getNextActiveProcessTime(checkList);

                    scrollToPosition(timerUtility.activeProcessTimeIndex);

                }


            }


        }

        else {

            scrollToPosition(timerUtility.activeProcessTimeIndex);

            timerViewModel.toggleTime();

            timerUtility.currentActiveTime = timerUtility.getNextActiveProcessTime(checkList);

            mainFragment.getMainUIDynamics().playAudio(timerUtility.currentActiveTime.getSelectAudio());

        }




    }

    public void endOfTimerTask( CountDownTimerAsync.CountDownTask countDownTask){

        mainFragment.getMainUIDynamics().playAudio(timerUtility.currentActiveTime.getSelectAudio());

        try {
            timerUtility.currentActiveTime.getViewHolder().checkOff();
        }catch (NullPointerException e){
         e.printStackTrace();
        }
        finally {

            timerUtility.revertTimeIndex();
            timerUtility.revertSubTimeIndex();
            revertEntryUI();

            timerUtility.currentActiveTime = checkList.get(1);
            timerViewModel.setTaskCustom(countDownTask);
        }

    }

    public void revertEntryUI(){

        for(Entry n :checkList){

            if(!(n instanceof Spacer)) {

                try {//can't have nice things can we
                    n.getViewHolder().textView
                            .setText(n.textEntry.getValue());

                    n.getViewHolder().timerLabelText
                            .setText(n.getCountDownTimer().getValue());
                }catch (NullPointerException e){

                }

            }

        }

    }

    public void updateEntryUI(){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (timerUtility.parentEntry != null) {

                    Log.d("subListing",""+timerUtility.currentActiveTime.onTogglePrimer.getValue());

                    if(!timerUtility.currentActiveTime.onTogglePrimer.getValue()) {
                    timerUtility.parentEntry.getViewHolder().textView
                            .setText(timerUtility.previousActiveTime.textTemp);

                    timerUtility.parentEntry.getViewHolder().timerLabelText
                            .setText(timerUtility.previousActiveTime.getCountDownTimer().getValue());
                    }

                } else {


                        timerUtility.currentActiveTime.getViewHolder().textView
                                .setText(timerUtility.currentActiveTime.textTemp);

                        timerUtility.currentActiveTime.getViewHolder().timerLabelText
                                .setText(timerUtility.currentActiveTime.getCountDownTimer().getValue());

                }

            }
        });


    }





}
