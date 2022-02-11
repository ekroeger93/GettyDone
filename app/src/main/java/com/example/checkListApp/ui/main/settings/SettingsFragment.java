package com.example.checkListApp.ui.main.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.checkListApp.R;
import com.example.checkListApp.ui.main.MainFragmentDirections;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.example.checkListApp.ui.main.donation.DonationFragmentDirections;
import com.example.checkListApp.ui.main.entry_management.record.ProgressProvider;

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

//        addPreferencesFromResource(R.xml.settings_fragment);
        setPreferencesFromResource(R.xml.settings_fragment, rootKey);

//       Preference p = getContext().getSharedPreferences()


    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //        LayoutInflater li = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        return li.inflate( R.layout.seekbar_preference, parent, false);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(
                (LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.dialog_reset_progress,
                                container,
                                false))
        );
        AlertDialog alertDialog = dialogBuilder.create();


        Preference dialogPreference = getPreferenceScreen().findPreference("resetProgress");
        dialogPreference.setOnPreferenceClickListener(preference -> {
            // dialog code here

            alertDialog.show();

            Button resetCancel = alertDialog.findViewById(R.id.reset_progress_cancel_button);
            Button resetConfirm = alertDialog.findViewById(R.id.reset_progress_confirm_button);

            resetCancel.setOnClickListener(v ->{
                alertDialog.dismiss();
            });

            resetConfirm.setOnClickListener(v ->{
                ProgressProvider.resetProgress(getContext());
                alertDialog.dismiss();
            });



            return true;
        });


        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
