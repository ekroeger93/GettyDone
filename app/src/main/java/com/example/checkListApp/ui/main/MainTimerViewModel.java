package com.example.checkListApp.ui.main;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.checkListApp.timer.CountDownTimerAsync;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.timer.TimeToggler;


public class MainTimerViewModel extends ViewModel {

    private final CountDownTimerAsync countTimer = CountDownTimerAsync.getInstance();
    private final TimeToggler timeToggler = TimeToggler.getTimeToggler();

    private TimeState timeState = new TimeState(0);
    private CountDownTimerAsync.PostExecute postExecute;

    public void setPostExecute(CountDownTimerAsync.PostExecute postExecute) {
        countTimer.setPostExecute(postExecute);
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

    public MutableLiveData<String> get_countDownTimer() {
        return _countDownTimer;
    }

    public void setTask(){
        countTimer.setCountDownTask(() -> {
            _countDownTimer.postValue(countTimer.getRunTime());
        });
    }

    public void setTaskCustom(CountDownTimerAsync.CountDownTask countDownTask){
        _countDownTimer.postValue(countTimer.getRunTime());
        countDownTask.execute();
    }

    public void setObserver(Observer<String> observer, LifecycleOwner owner){
        _countDownTimer.observe(owner, observer);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void toggleTime(){


        timeToggler.toggleTime(countTimer,
                (toggle) -> {

                    if(toggle){
                        countTimer.setTimer(timeState);
                        setTask();
                    }else {
                        timeState = new TimeState(countTimer.getNumberTime());
                        _countDownTimer.postValue(timeState.getTimeFormat());
                    }

                }
        );



    }

}
