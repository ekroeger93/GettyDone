package com.gettydone.app.fragments.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gettydone.app.R;
import com.gettydone.app.ui.main.ColorHelper;
import com.gettydone.app.ui.main.entry_management.record.ProgressProvider;

public class SettingsFragment  extends PreferenceFragmentCompat  {



    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

//        addPreferencesFromResource(R.xml.settings_fragment);
        setPreferencesFromResource(R.xml.settings_fragment, rootKey);

//       Preference p = getContext().getSharedPreferences()


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         getListView().setBackgroundColor(new ColorHelper(view.getContext()).Entry_ItemView_Selected);


    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        Preference dialogPreferenceResetProgress = getPreferenceScreen().findPreference("resetProgress");
        Preference dialogPreferenceEmail = getPreferenceScreen().findPreference("feedback");

        setupResetProgress(dialogPreferenceResetProgress,container);
        setupEmail(dialogPreferenceEmail,container);




        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public AlertDialog getResetProgressDialog(ViewGroup container){

        AlertDialog.Builder dialogBuilderResetProgress = new AlertDialog.Builder(getContext());
        dialogBuilderResetProgress.setView(
                (LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.dialog_reset_progress,
                                container,
                                false))
        );
        return dialogBuilderResetProgress.create();

    }

    public AlertDialog getEmailDialog(ViewGroup container){

        AlertDialog.Builder dialogBuilderResetProgress = new AlertDialog.Builder(getContext());
        dialogBuilderResetProgress.setView(
                (LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.dialog_email,
                                container,
                                false))
        );
        return dialogBuilderResetProgress.create();


    }

    public void setupResetProgress(Preference preference, ViewGroup container){

        AlertDialog alertDialogResetProgress = getResetProgressDialog(container);

        preference.setOnPreferenceClickListener( preference1 -> {

            alertDialogResetProgress.show();

            Button resetCancel = alertDialogResetProgress.findViewById(R.id.reset_progress_cancel_button);
            Button resetConfirm = alertDialogResetProgress.findViewById(R.id.reset_progress_confirm_button);

            resetCancel.setOnClickListener(v ->{
                alertDialogResetProgress.dismiss();
            });

            resetConfirm.setOnClickListener(v ->{
                ProgressProvider.resetProgress(getContext());
                alertDialogResetProgress.dismiss();
            });

            return true;
        });

    }

    public void setupEmail(Preference preference, ViewGroup container){

        AlertDialog alertDialogEmail = getEmailDialog(container);

        preference.setOnPreferenceClickListener( preference1 -> {

            alertDialogEmail.show();

            Button emailCancel = alertDialogEmail.findViewById(R.id.email_cancel_button);
            Button emailSend = alertDialogEmail.findViewById(R.id.email_submit_button);

            EditText editText = alertDialogEmail.findViewById(R.id.email_message_text);

            emailCancel.setOnClickListener(v ->{
                alertDialogEmail.dismiss();
            });

            emailSend.setOnClickListener(v ->{

                String[] address = new String[1];
                address[0]="regoerkcire@gmail.com";

                composeEmail(address,editText.getText().toString());

                alertDialogEmail.dismiss();

            });

            return true;
        });

    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }



}


//    static public void transitionFromSettingsToMain(Activity activity){
//        Navigation.findNavController(activity, R.id.entryListFragment).navigate(R.id.action_settingsFragment_to_mainFragment);
//    }
//    static public void transitionFromSettingsToFile(Activity activity){
//
//        Navigation.findNavController(activity,R.id.entryListFragment)
//                .navigate(SettingsFragmentDirections.actionSettingsFragmentToFileListFragment(JsonService.getJsonCheckArrayList()));
//
//    }
//    static public void transitionFromSettingsToProgress(Activity activity){
//        Navigation.findNavController(activity, R.id.entryListFragment).navigate(R.id.action_settingsFragment_to_progressFragment);
//
//    }
//    static public void transitionFromSettingsToDonation(Activity activity){
//        Navigation.findNavController(activity, R.id.entryListFragment).navigate(R.id.action_settingsFragment_to_donationFragment);
//
//    }