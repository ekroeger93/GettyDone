package com.example.checkListApp.settimer;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import com.example.checkListApp.R;
import com.example.checkListApp.timemanagement.parcel.TimeParcel;
import com.example.checkListApp.timemanagement.parcel.TimeParcelBuilder;
import com.example.checkListApp.databinding.SettimerFragmentBinding;
import com.example.checkListApp.ui.main.data_management.JsonService;

public class SetTimerFragment extends Fragment {

    private com.example.checkListApp.settimer.SetTimerViewModel mViewModel;
    SettimerFragmentBinding binding ;
    LifecycleOwner lifecycleOwner;

    TextView setTimerText;
    Button submitTimeButton;
    Button onToggleOnlySubmit;

    private static Integer timeIndexPosition;

    TimeParcel parcelable;
    String jsonData;


    public SetTimerFragment(){



    }

    public SetTimerFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        lifecycleOwner = this;
        mViewModel = new ViewModelProvider(this).get(SetTimerViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.settimer_fragment,container,false);
        binding.setViewModel(mViewModel);
//FileListFragmentArgs args = FileListFragmentArgs.fromBundle(getArguments());
        SetTimerFragmentArgs args = SetTimerFragmentArgs.fromBundle(getArguments());
        parcelable = args.getTimeParcel();



        //parcelable = getActivity().getIntent().getExtras().getParcelable(KeyHelperClass.TIME_PARCEL_DATA);;
        timeIndexPosition = parcelable.getTimeIndex();
//        jsonData = parcelable.getRetainedJsonData();


        return binding.getRoot();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTimerText = binding.setTimeText;
        submitTimeButton = binding.submitTime;
        onToggleOnlySubmit = binding.toggleOnlyBtn;


        submitTimeButton.setOnClickListener( v->{

            /*
                                MainFragmentDirections.ActionMainFragmentToSetTimerFragment action =
                            MainFragmentDirections.actionMainFragmentToSetTimerFragment(
                                    new TimeParcelBuilder().setTimeIndexValue(getBindingAdapterPosition()).build());

                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(action);

             */



            SetTimerFragmentDirections.ActionSetTimerFragmentToMainFragment action =
                    SetTimerFragmentDirections.actionSetTimerFragmentToMainFragment(JsonService.getJsonCheckArrayList());

//            SetTimerFragmentDirections.ActionSetTimerFragmentToMainFragment action =
//                    SetTimerFragmentDirections.actionSetTimerFragmentToMainFragment("");


            Log.d("timerTest",":: " +action.getJsonData());

            action.setTimeParcel(
                    new TimeParcelBuilder()
                            .setTimeIndexValue(timeIndexPosition)
                            .setTimeStringValue(setTimerText.getText().toString())
                            .setTimeNumberValue(mViewModel.getTimerValue())
                            .setTogglePrimer(false)
//                            .setRetainJsonData(jsonData)
                            .build()
            );

            Navigation.findNavController(view).navigate(action);


//            Intent intent = new Intent(getContext(), MainActivity.class);
//
//            intent.putExtra(KeyHelperClass.TIME_PARCEL_DATA,
//                     new TimeParcelBuilder()
//                    .setTimeIndexValue(timeIndexPosition)
//                    .setTimeStringValue(setTimerText.getText().toString())
//                    .setTimeNumberValue(mViewModel.getTimerValue())
//                    .build()
//            );
//
//            startActivity(intent);
        });

        onToggleOnlySubmit.setOnClickListener( view1 -> {

            SetTimerFragmentDirections.ActionSetTimerFragmentToMainFragment action =
                    SetTimerFragmentDirections.actionSetTimerFragmentToMainFragment(JsonService.getJsonCheckArrayList());

            action.setTimeParcel(
                    new TimeParcelBuilder()
                            .setTimeIndexValue(timeIndexPosition)
                            .setTimeStringValue("00:00:00")
                            .setTimeNumberValue(mViewModel.getTimerValue())
                            .setTogglePrimer(true)
//                            .setRetainJsonData(jsonData)
                            .build()
            );

            Navigation.findNavController(view).navigate(action);

        });

        observerInit();


    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void observerInit(){

        Observer<String> changedTimeText = new Observer() {
            @Override
            public void onChanged(Object o) {
                setTimerText.setText(mViewModel.getTimerText().getValue());
            }
        };
        mViewModel.getTimerText().observe(getViewLifecycleOwner(),changedTimeText);
    }


}