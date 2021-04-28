package com.example.checkListApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.management.FileListFragment;
import com.example.checkListApp.databinding.MainActivityBinding;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.MainFragmentDirections;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements FileListFragment.OnFragmentInteractionListener{

    static public MainActivityBinding activityBinding;

    TabLayout tabLayout;

    int navPosition = 0;
   public static boolean visualSelect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.main_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);



        activityBinding = DataBindingUtil.setContentView(this,R.layout.main_activity);
                ///DataBindingUtil.inflate(inflater,R.layout.fragment_file_list, (ViewGroup) container, false);

        activityBinding.setMMainActivity(this);

        Log.d("testT",""+activityBinding.getMMainActivity());


        tabLayout = findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(!visualSelect) {
                    if (tab.getPosition() == 0
                            && tab.getPosition() != navPosition
                    ) {


                        FileListFragment.transitionToList(activityBinding.getMMainActivity());

                    }


                    if (tab.getPosition() == 1
                            && tab.getPosition() != navPosition
                    ) {

                        MainFragment.transitionToFile(activityBinding.getMMainActivity());

                    }


                }else {
                    visualSelect = false;
                }

                navPosition = tab.getPosition();

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
    public void onFragmentInteraction(Uri uri) {

    }





}