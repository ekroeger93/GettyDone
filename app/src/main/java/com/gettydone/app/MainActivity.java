package com.gettydone.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.WindowManager;

import com.gettydone.app.fragments.donation.DonationFragmentDirections;
import com.gettydone.app.fragments.file_management.FileListFragment;
import com.gettydone.app.databinding.MainActivityBinding;
import com.gettydone.app.fragments.progress.ProgressFragmentDirections;
import com.gettydone.app.fragments.settings.SettingsFragmentDirections;
import com.gettydone.app.set_timer.SetTimerFragmentDirections;
import com.gettydone.app.ui.main.MainFragment;
import com.gettydone.app.fragments.settings.PreferenceHelper;
import com.gettydone.app.ui.main.MainFragmentDirections;
import com.gettydone.app.ui.main.data_management.JsonService;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FileListFragment.OnFragmentInteractionListener {

    public MainActivityBinding activityBinding;

    static public TabLayout tabLayout;
    Integer navPosition = 0;
    Integer prevNavPosition = 0;


    public static PreferenceHelper preferenceHelper;
    public static boolean isLoadingData =false;
    public static boolean transitionFromSetTime=false;

    NavHostFragment navHostFragment;

    MainFragment mainFragment;

    NavController navController;
    RequestHandler handler;

    @Override
    public Resources.Theme getTheme() {

//        Resources.Theme theme = super.getTheme();
//        if(useAlternativeTheme){
//            theme.applyStyle(R.style., true);
//        }
//        // you could also use a switch if you have many themes that could apply
//        return theme;
//
        return super.getTheme();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.main_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        preferenceHelper = new PreferenceHelper(getApplicationContext());

        activityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        activityBinding.setMMainActivity(this);


        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.entryListFragment);

        mainFragment = (MainFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);


        tabLayout = findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handler = new RequestHandler(activityBinding.getMMainActivity());


                prevNavPosition = navPosition;

                int destinationTab = tab.getPosition()+1;
                int currentTab = navPosition+1;
                int tabSize = 5;
                int transitionIndex = (destinationTab)+(tabSize*(currentTab));


                if (!MainFragment.isTimerRunning()) {

                        if(tab.getPosition() != navPosition)
                            handler.handleRequest(transitionIndex);

                    navPosition = tab.getPosition();
                }

                Log.d("navigationTest","prev: "+prevNavPosition+" current: "+navPosition);

            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });


//        OnBackPressedDispatcher onBackPressedDispatcher = new OnBackPressedDispatcher();
//
//        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//
//                int destinationTab = prevNavPosition+1;
//                int currentTab = navPosition+1;
//                int tabSize = 5;
//                int transitionIndex = (destinationTab)+(tabSize*(currentTab));
//
//
//                handler.handleRequest(transitionIndex);
//
//            }
//        });


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        Log.d("schemeTest",intent.getScheme());

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

         navController = Navigation.findNavController(this, R.id.entryListFragment);




    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);



    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    @Override
    public void onBackPressed() {

        int destinationTab = prevNavPosition+1;
        int currentTab = navPosition+1;
        int tabSize = 5;
        int transitionIndex = (destinationTab)+(tabSize*(currentTab));

        handler = new RequestHandler(activityBinding.getMMainActivity());


        if(!prevNavPosition.equals(navPosition) &&
        !MainActivity.transitionFromSetTime
        ) {
            handler.handleRequest(transitionIndex);

            navPosition = prevNavPosition;
            tabLayout.getTabAt(navPosition).select();

        }

        if(MainActivity.transitionFromSetTime){
            handler.handleRequest(30);
        }

//       super.onBackPressed();

    }



     static class RequestHandler {

        Activity activity;
        NavController navController;

        RequestHandler(Activity activity){
            this.activity = activity;
            this.navController = Navigation.findNavController(activity, R.id.entryListFragment);
        }

        Map<Integer, Integer> commandMap = new HashMap<>();


        {
            /*
            variable algorithm

            tSize = total number of tabs
            clickedTabPosition --> destination
            currentTabPosition --> current position

            int transitionIndex = (clickedTabPosition+1)+([tSize]*(currentTabPosition+1));

             */

            //from MainFragment
            commandMap.put(7, R.id.action_mainFragment_to_fileListFragment);
            commandMap.put(8, R.id.action_mainFragment_to_progressFragment);
            commandMap.put(9, R.id.action_mainFragment_to_donationFragment);
            commandMap.put(10, R.id.action_mainFragment_to_settingsFragment);

            //From FileFragment
            commandMap.put(11, R.id.action_fileListFragment_to_mainFragment);
            commandMap.put(13, R.id.action_fileListFragment_to_progressFragment);
            commandMap.put(14, R.id.action_fileListFragment_to_donationFragment);
            commandMap.put(15, R.id.action_fileListFragment_to_settingsFragment);

            //From ProgressFragment
            commandMap.put(16, R.id.action_progressFragment_to_mainFragment);
            commandMap.put(17, R.id.action_progressFragment_to_fileListFragment);
            commandMap.put(19, R.id.action_progressFragment_to_donationFragment);
            commandMap.put(20, R.id.action_progressFragment_to_settingsFragment);

            //From DonationFragment
            commandMap.put(21, R.id.action_donationFragment_to_mainFragment);
            commandMap.put(22, R.id.action_donationFragment_to_fileListFragment);
            commandMap.put(23, R.id.action_donationFragment_to_progressFragment);
            commandMap.put(25, R.id.action_donationFragment_to_settingsFragment);

            //From SettingsFragment
            commandMap.put(26, R.id.action_settingsFragment_to_mainFragment);
            commandMap.put(27, R.id.action_settingsFragment_to_fileListFragment);
            commandMap.put(28, R.id.action_settingsFragment_to_progressFragment);
            commandMap.put(29, R.id.action_settingsFragment_to_donationFragment);

            commandMap.put(30,R.id.action_setTimerFragment_to_mainFragment);


        }

        @SuppressLint("NonConstantResourceId")
        public void handleRequest( int action_id) {

            NavDirections navAction ;

            Log.d("navigationTest",""+action_id);

            int command = commandMap.get(action_id);

            switch (command){

                case  R.id.action_mainFragment_to_fileListFragment :{
                    navAction  = MainFragmentDirections.actionMainFragmentToFileListFragment(JsonService.getJsonCheckArrayList());
                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(navAction);
                }break;
                case  R.id.action_progressFragment_to_fileListFragment :{
                    navAction  = ProgressFragmentDirections.actionProgressFragmentToFileListFragment(JsonService.getJsonCheckArrayList());
                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(navAction);
                }break;
                case  R.id.action_donationFragment_to_fileListFragment :{
                    navAction  = DonationFragmentDirections.actionDonationFragmentToFileListFragment(JsonService.getJsonCheckArrayList());
                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(navAction);
                }break;
                case  R.id.action_settingsFragment_to_fileListFragment :{
                    navAction  = SettingsFragmentDirections.actionSettingsFragmentToFileListFragment(JsonService.getJsonCheckArrayList());
                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(navAction);
                }break;

                case R.id.action_fileListFragment_to_mainFragment:{
//                    navAction = FileListFragmentDirections.actionFileListFragmentToMainFragment(JsonService.getJsonCheckArrayList());
//                    Navigation.findNavController(activity,R.id.entryListFragment).navigate(navAction);

                    if(!isLoadingData)
                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(command);
//do nothing cus its already do something
                }break;

                case R.id.action_setTimerFragment_to_mainFragment:{

                    SetTimerFragmentDirections.ActionSetTimerFragmentToMainFragment action =
                            SetTimerFragmentDirections.actionSetTimerFragmentToMainFragment(JsonService.getJsonCheckArrayList());

                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(action);

                    MainActivity.transitionFromSetTime= false;
                }break;

                default:{

                    Log.d("navigationTest",""+action_id);
//                    SetTimerFragmentDirections.ActionSetTimerFragmentToMainFragment action =
//                            SetTimerFragmentDirections.actionSetTimerFragmentToMainFragment(JsonService.getJsonCheckArrayList());

                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(command);
                }break;

            }




        }

    }



     }

//                            switch (tab.getPosition()) {
//
//                                case 0: { //CLICKS LIST
//
//                                    if (navPosition == 1)//IF IN FILE
//                                        FileListFragment.transitionFromFileToMain(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 2) //IF IN PROGRESS
//                                        ProgressFragment.transitionFromProgressToMain(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 3)// IF IN DONATION
//                                        DonationFragment.transitionFromDonationToMain(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 4)// IF IN SETTINGS
//                                        SettingsFragment.transitionFromSettingsToMain(activityBinding.getMMainActivity());
//
//
//                                }
//                                break;
//
//                                case 1: { //CLICKS FILE
//
//                                    if (navPosition == 0)//IF In LIST
//                                        MainFragment.transitionFromMainToFile(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 2)//IF in PROGRESS
//                                        ProgressFragment.transitionFromProgressToFile(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 3)// IF IN DONATION
//                                        DonationFragment.transitionFromDonationToFile(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 4)// IF IN SETTINGS
//                                        SettingsFragment.transitionFromSettingsToFile(activityBinding.getMMainActivity());
//
//
//                                }
//                                break;
//
//                                case 2: {//CLICKS PROGRESS
//
//                                    if (navPosition == 0)//IF IN LIST
//                                        MainFragment.transitionFromMainToProgress(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 1)//IF IN FILE
//                                        FileListFragment.transitionFromFileToProgress(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 3)//IF IN DONATION
//                                        DonationFragment.transitionFromDonationToProgress(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 4)// IF IN SETTINGS
//                                        SettingsFragment.transitionFromSettingsToProgress(activityBinding.getMMainActivity());
//
//
//                                }
//                                break;
//
//                                case 3: {//CLICKS DONATION
//
//
//                                    if (navPosition == 0)//IF IN LIST
//                                        MainFragment.transitionFromMainToDonation(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 1)//IF IN FILE
//                                        FileListFragment.transitionFromFileToDonation(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 2)//IF in PROGRESS
//                                        ProgressFragment.transitionFromProgressToDonation(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 4)// IF IN SETTINGS
//                                        SettingsFragment.transitionFromSettingsToDonation(activityBinding.getMMainActivity());
//
//                                }
//                                break;
//
//                                case 4: {//CLICKS SETTINGS
//
//                                    if (navPosition == 0)//IF IN LIST
//                                        MainFragment.transitionFromMainToSettings(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 1)//IF IN FILE
//                                        FileListFragment.transitionFromFileToSettings(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 2)//IF in PROGRESS
//                                        ProgressFragment.transitionFromProgressToSetting(activityBinding.getMMainActivity());
//
//                                    if (navPosition == 3)//IF IN DONATION
//                                        DonationFragment.transitionFromDonationToSettings(activityBinding.getMMainActivity());
//
//
//                                }
//                                break;
//
//                            }

