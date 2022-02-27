package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.fragments.file_management.FileManager;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.time_management.TimerService;
import com.example.checkListApp.time_management.parcel.ListTimersParcel;
import com.example.checkListApp.time_management.parcel.ListTimersParcelBuilder;
import com.example.checkListApp.time_management.utilities.KeyHelperClass;
import com.example.checkListApp.ui.main.entry_management.SubListManager;
import com.example.checkListApp.ui.main.entry_management.button_panel.ButtonPanel;
import com.example.checkListApp.ui.main.entry_management.button_panel.ButtonPanelToggle;
import com.example.checkListApp.ui.main.entry_management.EntryItemManager;
import com.example.checkListApp.ui.main.entry_management.button_panel.LeafButton;
import com.example.checkListApp.ui.main.entry_management.MainListTimeProcessHandler;
import com.example.checkListApp.ui.main.entry_management.list_component.CustomLayoutManager;
import com.example.checkListApp.ui.main.entry_management.list_component.ListItemClickListener;
import com.example.checkListApp.ui.main.entry_management.list_component.RecyclerAdapter;
import com.example.checkListApp.ui.main.entry_management.list_component.item_touch_helper.ItemTouchCallback;
import com.example.checkListApp.ui.main.entry_management.Operator;
import com.example.checkListApp.ui.main.entry_management.record.RecordHelper;
import com.example.checkListApp.ui.main.data_management.AuxiliaryData;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.example.checkListApp.ui.main.data_management.ListUtility;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/*

--------------------------------------------------------------
-------------------------------------------------------------

 Context.startForegroundService() did not then call Service.startForeground()
 https://medium.com/geekculture/context-startforegroundservice-did-not-then-call-service-startforeground-solved-7640d5ba394a

icon
a running rabbit inside a timer dial


//TODO: TESTING

    -sorting and moving entries
    -progress
    -database
    -service
    -subListing

//TODO: CODE OVERHAUL

- entry needs a companion class to
decouple data types (entry_data_complex)
bridge pattern? complications with DAO and Gson

- defined enum state implementation:
    -execution phase
    -is timer paused
    -is sorting
    -moving items

- test units

- audio list to recycler view

//TODO: BUGS

  -repeater time needs persistence
    -still not in database, cant add statics?

    (sorta fixed left for future reference)
    -moving items to the last entry, selection does not proceed to it
        -this has something to do with dimension of recycler scroll

    -moving duplicated items copies the previous entry (moving Index position)

    -subList not persisting when moving to different fragment once assgined
    -losing onToggle when reassigning setTimer,

    -unknown crash from long term usage, no error presented


//TODO: FEATURES

https://developer.android.com/guide/topics/ui/settings
https://www.geeksforgeeks.org/how-to-implement-preferences-settings-screen-in-android/
https://developer.android.com/training/data-storage/shared-preferences
https://stackoverflow.com/questions/11562051/change-activitys-theme-programmatically

https://stackoverflow.com/questions/16108609/android-creating-custom-preference/33842737
https://medium.com/@JakobUlbrich/building-a-settings-screen-for-android-part-3-ae9793fd31ec


//TODO: DESIGN

-once we get selected themes set up
can also change the app icon colors along with it!


//TODO: Post production ideas:
-theme color
-? save to google drive, share data
-? save as pdf/rich text file -> print appMobilityPrint
-? schedule on calender, notification
-? make timelabel editText instead, /w custom keyboard
-? pictures and gifs, thumbnails

-tag files as sublists to cleanup file list fragment


https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf
i can get rid of touch expander completely
-double tap could be used as edit
-swipe is delete
-drag is move
-add button at the end of list to add


@RequiresApi(api = Build.VERSION_CODES.O)

https://stackoverflow.com/questions/43650201/how-to-make-an-android-app-run-in-background-when-the-screen-sleeps
https://developer.android.com/guide/components/foreground-services
https://github.com/PhilJay/MPAndroidChart

 */


public class MainFragment extends Fragment {

    public MainFragmentBinding binding;

    public static float recyclerScrollCompute,itemHeightPx;

    private ArrayList<Entry> checkList = new ArrayList<>();
    private final MutableLiveData<ArrayList<Entry>> _checkList = new MutableLiveData<>();
    public ArrayList<Entry> getCheckList(){ return checkList;}

    private MainViewModel mViewModel;
    public MainViewModel    getmViewModel(){ return mViewModel;}

    //// RECYCLER VIEW
    private RecyclerView recyclerView;
    public RecyclerView     getRecyclerView(){ return recyclerView;}

    private RecyclerAdapter adapter;
    public RecyclerAdapter  getAdapter() {return adapter;}

    private SelectionTracker<Long> selectionTracker;
    public SelectionTracker<Long> getSelectionTracker() {
        return selectionTracker;
    }
    ///


/// UTILITY
    private MainListTimeProcessHandler mainListTimeProcessHandler;
    public MainListTimeProcessHandler getMainListTimeProcessHandler() { return mainListTimeProcessHandler; }

    private FileManager fileManager;
    public FileManager getFileManager() { return fileManager; }

    private SubListManager subListManager;
    public SubListManager getSubListManager() { return subListManager; }

    private final RecordHelper recordHelper = new RecordHelper();
    public RecordHelper     getRecordHelper() {return recordHelper;}

    private final ListUtility listUtility = new ListUtility();
    public ListUtility      getListUtility() { return listUtility;}

    private MainUIDynamics mainUIDynamics;
    public MainUIDynamics getMainUIDynamics() {
        return mainUIDynamics;
    }

    ///


//// EVENT

    public boolean isSorting = false;

    //Has executed and not initialized disregarding pause or resume
    private static final MutableLiveData<Boolean> timerRunning = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> getTimerRunning() { return timerRunning; }
    public static Boolean isTimerRunning()   { return timerRunning.getValue();}


/////

    private Intent serviceIntent;
    public Intent  getServiceIntent() { return serviceIntent; }


    public MediaPlayer[] selectedAudio;

    private View fragmentView;
    public View getFragmentView(){ return fragmentView; }

    public static CustomLayoutManager customLayoutManager;
    ListTimersParcel listTimersParcel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MediaPlayer shortBell = MediaPlayer.create(getContext(), R.raw.short_bell);
        MediaPlayer longBell = MediaPlayer.create(getContext(), R.raw.long_bell);
        MediaPlayer doubleClap = MediaPlayer.create(getContext(), R.raw.double_clap);
        MediaPlayer blowWhistle = MediaPlayer.create(getContext(), R.raw.blow_whistle);

    selectedAudio = new MediaPlayer[4];

    selectedAudio[0] = shortBell;
    selectedAudio[1] = longBell;
    selectedAudio[2] = blowWhistle;
    selectedAudio[3] = doubleClap;




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        binding = DataBindingUtil.inflate(inflater,R.layout.main_fragment,container,false);
        binding.setLifecycleOwner(this);



        return binding.getRoot();

    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        fileManager = new FileManager(view.getContext());

        fragmentView = view;

        setUpAdapter();

        mainUIDynamics = new MainUIDynamics(this);

        adapter.setListItemClickListener(mainUIDynamics);

        initialize();

        //TODO: selectionTracker interference with onClick!!!
        mainUIDynamics.assignButtonListeners();

        assignObservers();

        mainListTimeProcessHandler = new MainListTimeProcessHandler(this);

        mainListTimeProcessHandler.configureMainTimer();

        subListManager = new SubListManager(this);

        recordHelper.createButton(getContext(),binding);

        MainListTimeProcessHandler.timerViewModel.setRepeaterTime(Entry.globalCycle);

        binding.repeatTimer.setText(String.valueOf(Entry.globalCycle));

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


        MainActivity.isLoadingData=false;

        if(!getArguments().isEmpty()) {

            if(
                      !MainFragmentArgs.fromBundle(getArguments()).getJsonData().isEmpty()
            ){

                mViewModel.deleteAllEntries(checkList);
                checkList = AuxiliaryData.loadFile(getArguments());
                _checkList.setValue(checkList);

                if(getCheckList() != null) {
                    for (Entry entry : getCheckList()) mViewModel.loadEntry(entry);
                    mainUIDynamics.operator.updateIndexes();

                    subListManager.sanityCheckSubList();
                    subListManager.loadSubLists();

                }


            }

            AuxiliaryData.receiveParcelTime(checkList, getArguments());

        }




    }

    public void setUpAdapter(){


        recyclerView = binding.ScrollView;

        adapter = new RecyclerAdapter(this);

        recyclerView.setAdapter(adapter);
        customLayoutManager = new CustomLayoutManager(getContext());

        recyclerView.setLayoutManager(customLayoutManager);
        recyclerView.setHasFixedSize(false);


        ArrayList<Entry> tempArray = new ArrayList<>();

        tempArray.add( new Spacer());
        tempArray.add( new Entry("a",false,"00:00:00"));

        adapter.setList(tempArray);

        adapter.setTrackerHelper(recyclerView);

        recyclerView.setLongClickable(false);


        ///////////
        selectionTracker = new SelectionTracker.Builder<>(
                "selection",
                recyclerView,
                adapter.trackerHelper().keyProvider,
                adapter.trackerHelper().itemDetailsLookup,
                StorageStrategy.createLongStorage())
                .withSelectionPredicate( adapter.trackerHelper.predicate)
                .build();


        adapter.setTracker(selectionTracker);
        adapter.trackerOn(false);
        ///////


    }


    public void initialize() {

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {

                    float ratioOffset = 1.75f;
//                    ratioOffset = 1.85f;

                    //adjusted so that selection is in middle of recyclerList
                    recyclerScrollCompute = recyclerView.getHeight() / ratioOffset;

                    itemHeightPx = 100;

                    // Converts dip into its equivalent px
                    float dip = itemHeightPx;
                    Resources r = getResources();
                    itemHeightPx = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            dip,
                            r.getDisplayMetrics()); // converted to px 262.5

                });


    }

    public void startService(){

        serviceIntent = getForegroundTimerServiceIntent();

        try {
            getActivity().startForegroundService(getForegroundTimerServiceIntent());
//            activity.startService(serviceIntent);
        }catch ( Exception i){

            Log.d("_BUG",""+i.getLocalizedMessage());
        }
        //service.startService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {

        try{
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }}catch (NullPointerException e){
            return false;
        }

        return false;
    }

    public Intent getForegroundTimerServiceIntent(){


        Intent intent = new Intent(getActivity(), TimerService.class); // Build the intent for the service


        listTimersParcel = new ListTimersParcelBuilder(checkList)
                .setEntryViewModelList(checkList)
                .setGlobalTimer(MainListTimeProcessHandler.timerViewModel.getValueTime())
                .setIndexActive(

                        mainListTimeProcessHandler.getActiveProcessTimeIndex()
//                        listUtility.activeProcessTimeIndex

                ).build();


        int size = checkList.size();

        String[] listOfCount = new String[size];

        for(Entry m : checkList){
            int index = checkList.indexOf(m);
            listOfCount[index] = m.getCountDownTimer().getValue();

        }

        listTimersParcel.listOfCountDownTimers = listOfCount;

        intent.putExtra(KeyHelperClass.TIME_PARCEL_DATA, listTimersParcel);


        return intent;

    }

    public void assignObservers(){

        mViewModel.getAllEntries().observe(getViewLifecycleOwner(),new Observer<List<Entry>>() {

        @Override
        public void onChanged(@Nullable final List<Entry> entries) {

            //makes sure we keeps those spacers at the ends
            if(checkList == null || checkList.size()-2 != entries.size()
            ) {

                checkList = (ArrayList<Entry>) entries;
                Log.d("checkListTest","updated!");

                checkList.add(0, new Spacer());
                checkList.add(checkList.size(), new Spacer());


                mainUIDynamics.operator.updateIndexes();
                subListManager.sanityCheckSubList();

                subListManager.loadSubLists();

                adapter.setList(checkList);
                recordHelper.update(checkList);

                if(!isSorting){
                checkList = listUtility.updateToggleOrdering(checkList);
                }else{ listUtility.updateAllSelection(checkList);
                }



                if(EntryItemManager.isAddingEntry){

                    RecyclerView.SmoothScroller smoothScroller =
                            new LinearSmoothScroller(getContext()){

                                @Override
                                protected int getVerticalSnapPreference() {
                                    return SNAP_TO_END;
                                }

                                @Override
                                protected void onStop() {
                                    super.onStop();
                                    checkList.get(checkList.size()-2).getViewHolder().animatePopIn();
                                }
                            };

                    smoothScroller.setTargetPosition(checkList.size()-1);
                    customLayoutManager.startSmoothScroll(smoothScroller);



                EntryItemManager.isAddingEntry = false;
                }

                _checkList.setValue(checkList);


                JsonService.buildJson(checkList);

                mainUIDynamics.updateCheckList(checkList);

            }




        }


    });

        mainUIDynamics.selectionTrackerObserver();

        timerRunning.observe(getViewLifecycleOwner(), mainUIDynamics.getOnTimerRunningObs());


    }


    public void enforceUpdateCheckList(){

        checkList = (ArrayList<Entry>) mViewModel.getAllEntries().getValue();
        checkList.add(0, new Spacer());
        checkList.add(checkList.size(), new Spacer());

        mainUIDynamics.operator.updateIndexes();
        subListManager.sanityCheckSubList();

        subListManager.loadSubLists();
        adapter.setList(checkList);
        recordHelper.update(checkList);

        _checkList.setValue(checkList);
        JsonService.buildJson(checkList);
        mainUIDynamics.updateCheckList(checkList);

    }

    public static void resetTime(){
        MainListTimeProcessHandler.timerViewModel.resetAbsolutely();
        timerRunning.postValue(false);
    }

    public static void timerRunningAsFalse(){
        timerRunning.postValue(false);
    }



}
