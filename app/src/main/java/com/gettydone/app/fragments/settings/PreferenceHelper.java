package com.gettydone.app.fragments.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferenceHelper {

    final private SharedPreferences preferences;

    public PreferenceHelper(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

     }

    public boolean hintTimerMessageIsActive(){
        return preferences.getBoolean("hintMessagesResetTimer", true);
    }

    public boolean hintDuplicateMessageIsActive(){
        return preferences.getBoolean("hintDuplicateEntry",true);
    }

    public int shakeToggleTimerMode(){
        //dumb as hell
        return Integer.parseInt(preferences.getString("shakeToToggleTimerMode","2"));
//        return preferences.getInt("shakeToToggleTimerMode",2);
    }

    public void resetProgress(){


    }


}
