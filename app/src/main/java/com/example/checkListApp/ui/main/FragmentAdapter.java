package com.example.checkListApp.ui.main;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavDirections;

import com.example.checkListApp.fragments.FragmentTransitionManager;

public class FragmentAdapter extends Fragment implements FragmentTransitionManager {

   Fragment fragment;

   FragmentAdapter(Fragment fragment){
       this.fragment = fragment;
   }

    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public void transitionTo(Activity activity, int id, NavDirections navDirections) {

    }

}
