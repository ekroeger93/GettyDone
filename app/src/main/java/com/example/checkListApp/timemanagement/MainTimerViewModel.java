package com.example.checkListApp.timemanagement;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.checkListApp.timer.CountDownTimerAsync;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.timer.TimeToggler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainTimerViewModel extends ViewModel {

    private final TimeToggler timeToggler = new TimeToggler();//TimeToggler.getTimeToggler();
    private final CountDownTimerAsync countTimer = CountDownTimerAsync.getInstanceToToggle(timeToggler);
    private TimeState timeState = new TimeState(0);


    private  boolean toggled = false;

    public boolean isToggled(){
        return toggled;
    }

    public void setRepeaterTime(int repeaterTime){
        countTimer.setRepeater(repeaterTime);
    }

    public int getRepeaterTime(){
        return countTimer.getRepeater();
    }

    public void setPostExecute(CountDownTimerAsync.PostExecute postExecute) {
        countTimer.setPostExecute(postExecute);
    }

    public void setServicePostExecute(CountDownTimerAsync.PostExecute postExecute){
        countTimer.setServicePostExecute(postExecute);
    }


    //junction link to activity text Time ON BUTTON SET
    private final MutableLiveData<String> _countDownTimer = new MutableLiveData<>("00:00:00");

    public void setCountDownTimer(String countDownTimer) {

        int tm = Integer.parseInt(countDownTimer.replace(":","").trim());
        timeState = new TimeState(tm);
        _countDownTimer.postValue(countDownTimer);
    }


    public String getValueTime(){
        return _countDownTimer.getValue();
    }

    public int getNumberValueTime() {return  new TimeState(_countDownTimer.getValue()).getTimeNumberValue();}

    public void setTimeState(TimeState timeState) {
        this.timeState = timeState;
    }

    public MutableLiveData<String> get_countDownTimer() {
        return _countDownTimer;
    }

    public void setTask(){
        countTimer.setCountDownTask((n) -> {
            _countDownTimer.postValue(countTimer.getRunTime());
        });
    }

    public void setTaskCustom(CountDownTimerAsync.CountDownTask countDownTask){

        countTimer.setCountDownTask((n) -> {
            _countDownTimer.postValue(countTimer.getRunTime());
            countDownTask.execute(countTimer.getCountTime());
        });

    }

    public void setObserver(Observer<String> observer, LifecycleOwner owner){
        _countDownTimer.observe(owner, observer);
    }

     class ExecuteToggleAsync {

        private final Executor executorService = Executors.newSingleThreadExecutor();

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void execute() {
            executorService.execute(MainTimerViewModel.this::toggleTime);
        }

    }

    public void setServiceTask(CountDownTimerAsync.ServiceTask serviceTask){
        countTimer.setServiceTask((n , nt, ntVn) ->{
            serviceTask.execute(countTimer.getElapsedTimeVolatile(), countTimer.getCountTime(), countTimer.getElapsedTime());
        });

    }

    public void toggleTime(){

        timeToggler.toggleTime(countTimer,
                (toggle) -> { toggled = toggle;
                    if(toggle){

                        if(countTimer.getCountTime() == 0){
                        countTimer.setTimerVolatile(timeState);
                        countTimer.setTimer(timeState);

                        }else{
                            countTimer.setTimerVolatile( new TimeState(countTimer.getCountTime()));
                        }

                    }else {
                        timeState = new TimeState(countTimer.getCountTime());
                       _countDownTimer.postValue(timeState.getTimeFormat());
                    }
                }
        );

    }


//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void toggleTimeWithCustomTask(){
//
//
//        timeToggler.toggleTime(countTimer,
//                (toggle) -> {
//                    toggled = toggle;
//                    if(toggle){
//
//                        if(countTimer.getNumberTime() == 0){
//                            countTimer.setTimer(timeState);
//                        }else{
//                            countTimer.setTimer( new TimeState(countTimer.getNumberTime()));
//                        }
//
//
//                    }else {
//                        timeState = new TimeState(countTimer.getNumberTime());
//                        _countDownTimer.postValue(timeState.getTimeFormat());
//                    }
//                }
//        );
//
//    }


    public void resetTimeState(){
        timeState = new TimeState(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void resetAbsolutely(){
        timeState = new TimeState(0);
        _countDownTimer.postValue(timeState.getTimeFormat());//
        setRepeaterTime(0);
        countTimer.setTimerVolatile(timeState);
        countTimer.setTimer(timeState);
        countTimer.resetAll();
        countTimer.postTimeExpire();
        timeToggler.shutDown();


    }


}
