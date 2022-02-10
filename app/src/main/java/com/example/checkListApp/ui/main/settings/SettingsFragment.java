package com.example.checkListApp.ui.main.settings;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;

import com.example.checkListApp.R;
import com.example.checkListApp.ui.main.MainFragmentDirections;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.example.checkListApp.ui.main.donation.DonationFragmentDirections;

public class SettingsFragment  extends PreferenceFragmentCompat {

    static public void transitionFromSettingsToMain(Activity activity){
        Navigation.findNavController(activity, R.id.entryListFragment).navigate(R.id.action_settingsFragment_to_mainFragment);
    }
    static public void transitionFromSettingsToFile(Activity activity){

        Navigation.findNavController(activity,R.id.entryListFragment)
                .navigate(SettingsFragmentDirections.actionSettingsFragmentToFileListFragment(JsonService.getJsonCheckArrayList()));

    }
    static public void transitionFromSettingsToProgress(Activity activity){
        Navigation.findNavController(activity, R.id.entryListFragment).navigate(R.id.action_settingsFragment_to_progressFragment);

    }
    static public void transitionFromSettingsToDonation(Activity activity){
        Navigation.findNavController(activity, R.id.entryListFragment).navigate(R.id.action_settingsFragment_to_donationFragment);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

        addPreferencesFromResource(R.xml.settings_fragment);

    }
}
