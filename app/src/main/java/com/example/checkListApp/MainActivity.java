package com.example.checkListApp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.WindowManager;

import com.example.checkListApp.file_management.FileListFragment;
import com.example.checkListApp.databinding.MainActivityBinding;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.donation.DonationFragment;
import com.example.checkListApp.ui.main.progress.ProgressFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements FileListFragment.OnFragmentInteractionListener {

    public MainActivityBinding activityBinding;

    static public TabLayout tabLayout;
    int navPosition = 0;

    public static boolean visualSelect = false;

    NavHostFragment navHostFragment;

    MainFragment mainFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.main_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        activityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        ///DataBindingUtil.inflate(inflater,R.layout.fragment_file_list, (ViewGroup) container, false);

        activityBinding.setMMainActivity(this);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.entryListFragment);
        mainFragment = (MainFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);


        tabLayout = findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                //THIS WAS TOTALLY NOT CONFUSING AND MADE COMPLETE SENSE!

                //refactor this to switch for gods sake man

                if (!MainFragment.isTimerRunning()) {

                    if (!visualSelect) {

                        if (tab.getPosition() == 0 //CLICKS LIST
                                && tab.getPosition() != navPosition
                        ) {

                            if (navPosition == 1)//IF IN FILE
                                FileListFragment.transitionFromFileToMain(activityBinding.getMMainActivity());

                            if (navPosition == 2) //IF IN PROGRESS
                                ProgressFragment.transitionFromProgressToMain(activityBinding.getMMainActivity());

                            if(navPosition == 3)// IF IN DONATION
                                DonationFragment.transitionFromDonationToMain(activityBinding.getMMainActivity());
                        }


                        if (tab.getPosition() == 1 //CLICKS FILE
                                && tab.getPosition() != navPosition
                        ) {

                            if (navPosition == 0)//IF In LIST
                                MainFragment.transitionFromMainToFile(activityBinding.getMMainActivity());

                            if (navPosition == 2)//IF in PROGRESS
                                ProgressFragment.transitionFromProgressToFile(activityBinding.getMMainActivity());
                            //  FileListFragment.transitionToProgressFromFile(activityBinding.getMMainActivity());

                            if(navPosition ==3)// IF IN DONATION
                                DonationFragment.transitionFromDonationToFile(activityBinding.getMMainActivity());

                        }


                        if (tab.getPosition() == 2 //CLICKS PROGRESS
                                && tab.getPosition() != navPosition) {

                            if (navPosition == 0)//IF IN LIST
                                MainFragment.transitionFromMainToProgress(activityBinding.getMMainActivity());

                            if (navPosition == 1)//IF IN FILE
                                FileListFragment.transitionFromFileToProgress(activityBinding.getMMainActivity());
                            //  ProgressFragment.transitionFromProgressToFile(activityBinding.getMMainActivity());

                            if (navPosition == 3)//IF IN DONATION
                                DonationFragment.transitionFromDonationToProgress(activityBinding.getMMainActivity());

                        }


                        if(tab.getPosition() == 3//CLICKS DONATION
                        && tab.getPosition() != navPosition){

                            if (navPosition == 0)//IF IN LIST
                                MainFragment.transitionFromMainToDonation(activityBinding.getMMainActivity());

                            if (navPosition == 1)//IF IN FILE
                                FileListFragment.transitionFromFileToDonation(activityBinding.getMMainActivity());

                            if (navPosition == 2)//IF in PROGRESS
                                ProgressFragment.transitionFromProgressToDonation(activityBinding.getMMainActivity());

                        }




                    } else {
                        visualSelect = false;
                    }

                    navPosition = tab.getPosition();

                }

            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });



    }


    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);


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

//        super.onBackPressed();

    }

}