package com.example.checkListApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.checkListApp.management.FileListFragment;
import com.example.checkListApp.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity implements FileListFragment.OnFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}