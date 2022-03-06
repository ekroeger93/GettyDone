package com.gettydone.app.time_management;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.gettydone.app.R;
import com.gettydone.app.databinding.MainFragmentBinding;
import com.gettydone.app.timer.CountDownTimerAsync;
import com.gettydone.app.timer.TimeState;
import com.gettydone.app.timer.TimeToggler;


public class TimerViewModel {

    private final TimeToggler timeToggler = new TimeToggler();//TimeToggler.getTimeToggler();
//    private final CountDownTimerAsync countTimer = CountDownTimerAsync.getInstanceToToggle(timeToggler);
    private  CountDownTimerAsync countTimer = new CountDownTimerAsync(timeToggler);
    private TimeState timeState = new TimeState(0);


    private final MutableLiveData<Boolean> toggled = new MutableLiveData<>(false);


    public void setObserverToggle(LifecycleOwner owner, MainFragmentBinding binding, Context context) {


        Observer<Boolean> observer = aBoolean -> {

            if(!aBoolean){

                binding.timerExecuteBtn.setBackground(
                        ContextCompat.getDrawable(
                                context,
                                R.drawable.outline_play_circle_filled_black_48
                        ));

            }else{

                binding.timerExecuteBtn.setBackground(
                        ContextCompat.getDrawable(
                                context,
                                R.drawable.outline_pause_circle_filled_black_48
                        ));
            }

        };


        toggled.observe(owner,observer);

    }


    public boolean isToggled () {

            try {//dont give me this bs just go!
                return toggled.getValue();
            } catch (NullPointerException e) {
                return false;
            }

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


    public void setTaskCustom(CountDownTimerAsync.CountDownTask countDownTask){

        countTimer.setCountDownTask((n) -> {
            _countDownTimer.postValue(countTimer.getRunTime());
            countDownTask.execute(countTimer.getElapsedTime());
        });

    }

    public void setObserver(Observer<String> observer, LifecycleOwner owner){
        _countDownTimer.observe(owner, observer);
    }


    public void setServiceTask(CountDownTimerAsync.ServiceTask serviceTask){
        countTimer.setServiceTask((n , nt, ntVn) ->{
            serviceTask.execute(countTimer.getElapsedTimeVolatile(), countTimer.getCountTime(), countTimer.getElapsedTime());
        });

    }



    public void toggleTime(){

        timeToggler.toggleTime(countTimer,
                (toggle) -> { toggled.postValue(toggle);
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
        countTimer.shutdown();

        countTimer = new CountDownTimerAsync(timeToggler);


    }


}
