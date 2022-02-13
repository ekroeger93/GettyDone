package com.example.checkListApp.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.navigation.NavDirections;

public interface FragmentTransitionManager {

     void transitionTo(Activity activity, int id, NavDirections navDirections);

}
