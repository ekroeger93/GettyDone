package com.example.checkListApp.timemanagement;

import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.checkListApp.timer.CountDownTimerAsync;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public final class MainTimerView {

    public MainTimerViewModel mainTimerViewModel = new MainTimerViewModel();

    MutableLiveData<Boolean> toggled = new MutableLiveData<>(false);


    public MainTimerViewModel getGlobalTimeViewModel() {
        return mainTimerViewModel;
    }

    public Observer<Integer> getObserver(ArrayList<Entry> checkList) {

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(checkList.size()!=0)
                mainTimerViewModel.setCountDownTimer(checkList.get(integer).countDownTimer.getValue());
            }
        };

        return observer;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setObserverForMainTextTime(TextView mainTimer, LifecycleOwner owner) {

        Observer<String> observer = new Observer() {
            @Override
            public void onChanged(Object o) {
                mainTimer.setText(mainTimerViewModel.getValueTime());
            }
        };

        mainTimerViewModel.setObserver(observer, owner);

    }

    public void setObserverForToggledLiveData( LifecycleOwner owner, Observer<Boolean> observer){
        toggled.observe(owner,observer);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setListener(Button executeTime) {

        executeTime.setOnClickListener(view -> {
            toggled.postValue(mainTimerViewModel.isToggled());
            mainTimerViewModel.toggleTime();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setListenerWithTask(Button executeTime, CountDownTimerAsync.CountDownTask task){

        executeTime.setOnClickListener( view ->{
            toggled.postValue(mainTimerViewModel.isToggled());
            mainTimerViewModel.toggleTimeWithCustomTask(task);
        });

    }

    public void setPostExecute( CountDownTimerAsync.PostExecute postExecute) {
        mainTimerViewModel.setPostExecute(postExecute);
    }







    }




