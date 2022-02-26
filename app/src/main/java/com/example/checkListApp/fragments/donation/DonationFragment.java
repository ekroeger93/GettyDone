package com.example.checkListApp.fragments.donation;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.checkListApp.R;
import com.example.checkListApp.ui.main.data_management.JsonService;

import java.util.regex.Pattern;

public class DonationFragment extends Fragment {


    public DonationFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.donation_page_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button linkButton = view.findViewById(R.id.link_to_donation);
        linkButton.setMovementMethod(LinkMovementMethod.getInstance());


//        String supportUkraine = getResources().getString(R.string.support_ukraine);

        Button linkButton2 = view.findViewById(R.id.Ukraine);
        linkButton2.setMovementMethod(LinkMovementMethod.getInstance());

//        linkButton2.setText(supportUkraine);
//
//        Pattern linkSupportUkraine = Pattern.compile(supportUkraine);
//        Linkify.addLinks(linkButton2, linkSupportUkraine, "ukrainelink");

    }


}

//
//    public static void transitionFromDonationToMain(Activity activity){
//        Navigation.findNavController(activity, R.id.entryListFragment).navigate(R.id.action_donationFragment_to_mainFragment);
//    }
//
//    public static void transitionFromDonationToProgress(Activity activity){
//        Navigation.findNavController(activity,R.id.entryListFragment)
//                .navigate(DonationFragmentDirections.actionDonationFragmentToProgressFragment());
//
//    }
//
//
//    public static void transitionFromDonationToFile(Activity activity){
//
//        Navigation.findNavController(activity,R.id.entryListFragment)
//                .navigate(DonationFragmentDirections.actionDonationFragmentToFileListFragment(JsonService.getJsonCheckArrayList()));
//
//    }
//
//    public static void transitionFromDonationToSettings(Activity activity){
//        Navigation.findNavController(activity,R.id.entryListFragment)
//                .navigate(DonationFragmentDirections.actionDonationFragmentToSettingsFragment());
//    }

