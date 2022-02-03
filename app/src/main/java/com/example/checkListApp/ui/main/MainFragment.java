package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.file_management.FileListAdapter;
import com.example.checkListApp.file_management.FileManager;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.time_management.TimerService;
import com.example.checkListApp.time_management.parcel.ListTimersParcel;
import com.example.checkListApp.time_management.parcel.ListTimersParcelBuilder;
import com.example.checkListApp.time_management.utilities.KeyHelperClass;
import com.example.checkListApp.ui.main.entry_management.SubListFileRecyclerAdapter;
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

import java.util.ArrayList;
import java.util.List;

/*

TaDone Prototype
--------------------------------------------------------------
-------------------------------------------------------------

 Context.startForegroundService() did not then call Service.startForeground()
 https://medium.com/geekculture/context-startforegroundservice-did-not-then-call-service-startforeground-solved-7640d5ba394a



//TODO: TESTING

    -sorting and moving entries
    -progress
    -database
    -service
    -subListing

//TODO: BUGS

   -subListing moving Entry
  -repeater time needs persistence
  -progress overwritten last entries, after a week


//TODO: DESIGN

- entry time label redesign

- show swiping hand icon for hint

- file manager
    -prompt confirm
    -or click hold to delete

- color theme

- subListing, some indication of what list is being used
and that a subList IS being used
    -title = file name
    -collapsed icon
    -mini residing entries

//TODO: Post production ideas:
-? save to google drive, share data
-? save as pdf/rich text file -> print appMobilityPrint
-? schedule on calender, notification
-? make timelabel editText instead, /w custom keyboard

-add in pull down/up to extender, change the recycler view Y size
    -use anchor points when dragging
    or fullscreen mode

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


public class MainFragment extends Fragment implements ListItemClickListener {

    public MainFragmentBinding binding;

    public static float recyclerScrollCompute,itemHeightPx, ratioOffset;

    private EntryItemManager entryItemManager;
    private SelectionTracker<Long> selectionTracker;

    private Operator operator;
    private ButtonPanel buttonPanel;
    private ButtonPanelToggle buttonPanelToggle;

    private final RecordHelper recordHelper = new RecordHelper();

//    private static final TimerViewModel mainTimerViewModel = new TimerViewModel();
//    private static final TimerViewModel subTimerViewModel = new TimerViewModel();


    private final ListUtility listUtility = new ListUtility();

    private RecyclerAdapter adapter;
    private MainViewModel mViewModel;
    private RecyclerView recyclerView;

    private static CustomLayoutManager customLayoutManager;
    ListTimersParcel listTimersParcel;

    public MainListTimeProcessHandler mainListTimeProcessHandler;

    private ArrayList<Entry> checkList = new ArrayList<>();
    private final MutableLiveData< ArrayList<Entry>> _checkList = new MutableLiveData<>();


    private final MutableLiveData<Integer> selectedEntry = new MutableLiveData<>();

    public ArrayList<Entry> getCheckList(){ return checkList;}
    public Operator         getOperator (){ return operator;}
    public MainViewModel    getmViewModel(){ return mViewModel;}

    public RecyclerView     getRecyclerView(){ return recyclerView;}
    public RecyclerAdapter  getAdapter() {return adapter;}
    public CustomLayoutManager getCustomLayoutManager(){return customLayoutManager;}

    public RecordHelper     getRecordHelper() {return recordHelper;}
    public ListUtility      getListUtility() { return listUtility;}

    public Intent           getServiceIntent() { return serviceIntent; }
//    public static TimerViewModel getMainTimerViewModel() { return mainTimerViewModel; }

    private Intent serviceIntent;

    private boolean isSorting = false;

    private static final MutableLiveData<Boolean> timerRunning = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> getTimerRunning() { return timerRunning; }

    public static Boolean isTimerRunning()   { return timerRunning.getValue();}

    private MediaPlayer[] selectedAudio;

    private Activity activity;

    private ItemTouchCallback callback;


    private FileManager fileManager;
    private FileListAdapter fileListAdapter;

    private View fragmentView;


    public void showSubListSelection(View view, int index){

        buildSubListAdapter(buildSubListDialog(view) , index);

    }

    public AlertDialog buildSubListDialog(View view){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog subListDialog = dialogBuilder.create();


        subListDialog.setMessage("load a sub list");
        subListDialog.setView(LayoutInflater.from(getContext())
                .inflate(
                        R.layout.dialog_sublist_selection,
                        (ViewGroup) view,
                        false));

        subListDialog.show();

        return subListDialog;

    }

    public void buildSubListAdapter(AlertDialog alertDialog, int index){


        RecyclerView subListRecyclerView = alertDialog.findViewById(R.id.subListSelection);
        SubListFileRecyclerAdapter subListAdapter = new SubListFileRecyclerAdapter(fileManager.getListOfFiles());

        subListRecyclerView.setAdapter(subListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(alertDialog.getContext());

        subListRecyclerView.setLayoutManager(linearLayoutManager);
        subListRecyclerView.setHasFixedSize(true);

        Button loadSub = alertDialog.findViewById(R.id.loadSubListFileBtn);

        Button unLoadSub = alertDialog.findViewById(R.id.unsetSubListBtn);

        loadSub.setOnClickListener(view -> {
            setSubList(index, subListAdapter.getFileSelection());
            alertDialog.dismiss();

        });

        unLoadSub.setOnClickListener(view -> {
            checkList.get(index).unSetSubList();
            alertDialog.dismiss();
        });

    }

    public void setSubList(int checkListIndex , int fileListIndex){

        Entry entry = checkList.get(checkListIndex);

        ArrayList<Entry> subList = AuxiliaryData.loadFile(
                fileManager.loadFile(fileListIndex));

        entry.subListJson.setValue(fileManager.loadFile(fileListIndex));

        Log.d("subListingTest",""+fileManager.loadFile(fileListIndex));

        checkList.get(checkListIndex).isSubEntry = true;

        for(Entry n: subList) {
            n.isSubEntry = true;
            n.setViewHolder(entry.getViewHolder());
        }

        entry.setSubCheckList(subList);

        mainListTimeProcessHandler.subAccumulation(checkList);

    }

    public void setSubList(int checkListIndex, String jsonData){


        Entry entry = checkList.get(checkListIndex);

        ArrayList<Entry> subList = AuxiliaryData.loadFile(jsonData);

        entry.subListJson.setValue(jsonData);

        checkList.get(checkListIndex).isSubEntry = true;

        for(Entry n: subList) {
            n.isSubEntry = true;
            n.setViewHolder(entry.getViewHolder());
        }

        entry.setSubCheckList(subList);

        mainListTimeProcessHandler.subAccumulation(checkList);

    }

    public void loadSubLists(ArrayList<Entry> checkList){

        for(Entry entry: getCheckList()){


            if(!entry.subListJson.getValue().isEmpty() && !entry.subListJson.getValue().equals("\"\"")){

                Log.d("jsonTest", ":"+entry.subListJson.getValue());

                ArrayList<Entry> subList = AuxiliaryData.loadFile(entry.subListJson.getValue());

                entry.isSubEntry = true;

                for(Entry n: subList) {
                    n.isSubEntry = true;
                    n.setViewHolder(entry.getViewHolder());
                }

                entry.setSubCheckList(subList);
                mainListTimeProcessHandler.subAccumulation(checkList);

            }


        }




    }


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


    activity = getActivity();

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

        initialize();

        //TODO: selectionTracker interference with onClick!!!

        assignButtonListeners();

        //TODO: investigate here too
        assignObservers();


        mainListTimeProcessHandler = new MainListTimeProcessHandler(this);

        mainListTimeProcessHandler.configureMainTimer();


        recordHelper.createButton(getContext(),binding);

        MainListTimeProcessHandler.timerViewModel.setRepeaterTime(Entry.globalCycle);

        binding.repeatTimer.setText(""+Entry.globalCycle);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


        if(!getArguments().isEmpty()) {

            if(
                    //AuxiliaryData.loadFile(getArguments()) != null
                    !MainFragmentArgs.fromBundle(getArguments()).getJsonData().isEmpty()
            ){

                mViewModel.deleteAllEntries(checkList);
                checkList = AuxiliaryData.loadFile(getArguments());
                _checkList.setValue(checkList);
                for(Entry entry : getCheckList()) mViewModel.loadEntry(entry);
                updateIndexes();


                loadSubLists(checkList);



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
        tempArray.add( new Entry("a",false,"00:00:00"));

        adapter.setList(tempArray);

        adapter.setTrackerHelper(recyclerView);

        recyclerView.setLongClickable(false);

//        //TODO: DISABLE SELECTION TRACKER
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

        operator = new Operator(this);
        entryItemManager = new EntryItemManager( this);
        buttonPanel = new ButtonPanel(getContext(), binding);
        buttonPanelToggle  = buttonPanel.buttonPanelToggle;

        callback = new ItemTouchCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);


//        adapter.registerAdapterDataObserver();

        entryItemManager.setButtonPanelToggle(buttonPanelToggle);

    }

    public void initialize() {

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {

                    ratioOffset = 1.75f;

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
//            activity.startForegroundService(getForegroundTimerServiceIntent());
            activity.startService(serviceIntent);
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


    public static void resetTime(){
        MainListTimeProcessHandler.timerViewModel.resetAbsolutely();
        timerRunning.postValue(false);
    }

    public void playAudio(int audio){
        selectedAudio[audio].start();
    }

    public void updateIndexes(){

        for(Entry n :checkList) {

            if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
                n.orderIndex.setValue(checkList.indexOf(n));
            }else{
                n.orderIndex.postValue(checkList.indexOf(n));
            }
            mViewModel.updateIndex(n, checkList.indexOf(n));
//            Log.d("orderingTest", ""+n.orderIndex.getValue());
        }


        mViewModel.sortIndexes();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void assignButtonListeners(){


        binding.repeatTimer.setOnClickListener(view -> {
            binding.repeatTimer.setSelection(0);
        });

//        binding.repeatTimer.onKeyPreIme(KeyEvent.KEYCODE_ENTER,new KeyEvent(KeyEvent.KEYCODE_ENTER,KeyEvent.ACTION_DOWN));

        binding.repeatTimer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.repeatTimer.setSelection(0);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.repeatTimer.setSelection(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(binding.repeatTimer.getText().length()>1){
                    String text = binding.repeatTimer.getText().toString();
                    binding.repeatTimer.setText(text.substring(0,text.length()-1));


                }
                int value = 0;

                if(!binding.repeatTimer.getText().toString().isEmpty())
                    value = Integer.parseInt(binding.repeatTimer.getText().toString());

                Entry.globalCycle = value;
                JsonService.buildJson(checkList);

            }
        });


        buttonPanel.addButtonWithLeaf(
            binding.addDeleteBtn
            , view -> entryItemManager.add()
            , view -> entryItemManager.delete(),

            new LeafButton(binding, binding.addDeleteBtn)
                     //assign a onClickListener for leaf button
                    .assignListener(view -> {

                        //listener for the button panel
                        buttonPanelToggle.setSubmitBtnOnClickListener(
                                view1 -> { //deletes selected Entries
                            entryItemManager.deleteSelected(selectionTracker);
                            buttonPanelToggle.toggleDisableToButton();
                            adapter.trackerOn(false);
                        });

                        updateIndexes();
                        adapter.trackerOn(true);
                        buttonPanelToggle.toggleDisableToButton();

                    }).create()

    );

                buttonPanel.addButtonWithLeaf(
                        binding.editMoveBtn,
                        view -> entryItemManager.edit(),
                        view -> entryItemManager.move(),
                        new LeafButton(binding,binding.editMoveBtn)
                         .assignListener(view ->{

                            //I'm having a tough time with the recyclerView
                            //for some reason the object references (viewHolders) are disorganized
                            //until you scroll the entirety of the list so
                            //I'm starting it on 0 and setting setHasFixedSize here

                            recyclerView.scrollToPosition(0);
                            recyclerView.setHasFixedSize(true);

                            buttonPanelToggle.setSubmitBtnOnClickListener(view1 -> {

                                isSorting = true;
                                entryItemManager.sortSelected(selectionTracker);
                                buttonPanelToggle.toggleDisableToButton();
                                adapter.notifyDataSetChanged();

                                selectionTracker.clearSelection();
                                listUtility.reInitializeAllSelection(checkList);
                                adapter.trackerOn(false);

                            });

                            //thread may still be running!!!
                            selectionTracker.clearSelection();
                            listUtility.reInitializeAllSelection(checkList);
                            updateIndexes();

                            isSorting = false;

                            adapter.trackerOn(true);
                            buttonPanelToggle.toggleDisableToButton();


                        }).create()
                );




           binding.ScrollView.setOnScrollChangeListener((v, i, i1, i2, i3) -> {

               try {
                   selectedEntry.postValue(operator.getSelection());


//                   TODO: TEMP FIX FOR BUG, ANIMATION STUCK SCALE 0 WHEN VIEW IS CREATED
                   if (checkList.get(selectedEntry.getValue()).getViewHolder().itemView.getScaleX() < 1){

                       checkList.get(selectedEntry.getValue())
                               .getViewHolder()
                               .animatePopIn();
                   }

               } catch (NullPointerException e) {
                   e.printStackTrace();
               }

//               if (operator.isMovingItem) {
////                   operator.moveItem(operator.movingItem);
//               }


           });

           binding.windowBox.setOnTouchListener((view, motionEvent) -> {
               float touch_X = motionEvent.getRawX();
               float touch_Y = motionEvent.getRawY();
               buttonPanel.checkWithinButtonBoundary(touch_X,touch_Y);

               return true;
           });





    binding.touchExpander.setOnTouchListener((view, motionEvent) -> {

        float touch_X = motionEvent.getRawX();
        float touch_Y = motionEvent.getRawY();

        buttonPanel.setButtons();
        boolean onButton= buttonPanel.checkWithinButtonBoundary(touch_X,touch_Y);


        VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(motionEvent);
        velocityTracker.computeCurrentVelocity(1);
        operator.refreshSelection(false);

        float xMove = operator.currentViewHolder.itemView.getX() + (velocityTracker.getXVelocity()*40);

        float width = (float) operator.currentViewHolder.itemView.getWidth();
        float alpha = 1.0f - Math.abs(xMove) / width;
        operator.currentViewHolder.itemView.setAlpha(alpha);



      if(touch_X > binding.touchExpander.getX()+ (binding.touchExpander.getWidth()/1.5) ) {
          operator.currentViewHolder.itemView.setTranslationX(xMove);
      }else{
          operator.currentViewHolder.itemView.setTranslationX(0);
      }


        checkList.get(operator.getSelection())
                .getViewHolder()
                .isGonnaShakeCauseImMovingIt
                .postValue(
                        (touch_X < binding.touchExpander.getX() -
                (binding.touchExpander.getWidth()/3f)
              && onButton)
                );




        velocityTracker.recycle();


        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL
        ){

            checkList.get(operator.getSelection()).getViewHolder().hasAnimateMove = false;

            if(!onButton){

           // operator.currentViewHolder.itemView.setTranslationX(0);
            operator.currentViewHolder.itemView.animate()
                    .translationX(0)
                    .setDuration(500)
                    .setInterpolator( new OvershootInterpolator())
                    .start();

        }}

//------------------------------
        //We only need to check this once so lets do it on UP

        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            buttonPanel.executeAlternative();
        }

        buttonPanel.animationHandler(motionEvent.getAction(),touch_X,binding);

        return  true;

    });

}

    public void assignObservers(){

        Observer<Boolean> onTimerRunning = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                callback.setEnableSwipe(!aBoolean);

                if(!aBoolean){//timer not running

                    MainActivity.tabLayout.setVisibility(View.VISIBLE);
                    hideButtons(false);
                }else{

                    MainActivity.tabLayout.setVisibility(View.GONE);
                    hideButtons(true);
                }

            }
        };

        timerRunning.observe(getViewLifecycleOwner(), onTimerRunning);


        mViewModel.getAllEntries().observe(getViewLifecycleOwner(),new Observer<List<Entry>>() {

        @Override
        public void onChanged(@Nullable final List<Entry> entries) {

            //makes sure we keeps those spacers at the ends
            if(checkList == null || checkList.size()-2 != entries.size()
            ) {

                checkList = (ArrayList<Entry>) entries;

//                for(Entry n :checkList)
//                {
////                    if(n.orderIndex.getValue() == -1)
//                        n.orderIndex.setValue( checkList.indexOf(n));
//
//                }

                checkList.add(0, new Spacer());
                checkList.add(checkList.size(), new Spacer());


                updateIndexes();

                loadSubLists(checkList);

                adapter.setList(checkList);
                recordHelper.update(checkList);

                if(!isSorting){
                checkList = listUtility.updateToggleOrdering(checkList);
                }else{ listUtility.updateAllSelection(checkList);
                }


//                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext());
//                smoothScroller.setTargetPosition(checkList.size()-1);
//                customLayoutManager.startSmoothScroll(smoothScroller);

                if(EntryItemManager.isAddingEntry){
//                scrollPosition(checkList.size()-2);


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

                //maintenance code
//                for(Entry entry : checkList) {
//                    Log.d("checkListTest", "..." + entry);
//                }


                JsonService.buildJson(checkList);


            }




        }

    });




    selectionTracker.addObserver( new SelectionTracker.SelectionObserver<Long>(){

        @Override
        public void onSelectionRefresh() {
            super.onSelectionRefresh();
        }

        @Override
        public void onItemStateChanged(@NonNull Long key, boolean selected) {
            super.onItemStateChanged(key, selected);

            if(adapter.toggleTracker && !isSorting) {
                RecyclerAdapter.ViewHolder entryCurrent;
                int index;

            for (Entry entry : checkList) {
                try {
                    if (entry instanceof Spacer) {} else {

                        if (key.equals(entry.getViewHolder().getKey())) {

                            entryCurrent = entry.getViewHolder();
                            index = checkList.indexOf(entry) - 1;

                            //I need the adapter to re-realize the list size
                            //due to the adapter erroneous size prior to cleaning
                            adapter.getItemCount();//this is why its called

                           // entryCurrent.isSelected.setValue(!entryCurrent.isSelected.getValue());

                            /*
                            ultimately the holder.orderInt should reflect the toggleSwitchOrdering.list
                            * */

                            listUtility.toggleSwitchOrdering.toggleNum(index);

                            entryCurrent.isSelected.setValue(listUtility.toggleSwitchOrdering.listToOrder.get(index).toggle);
                            entryCurrent.orderInt.setValue(listUtility.toggleSwitchOrdering.listToOrder.get(index).number);

                            entryCurrent.selectOrder = listUtility.toggleSwitchOrdering.listToOrder.get(index).number;

                            listUtility.updateAllSelection(checkList);

                           break;
                                }

                            }

                        } catch (NullPointerException  | IndexOutOfBoundsException a) {
                        }
                    }





            try {

                    for(Entry entry : checkList){
                        //   entry.getViewHolder().selectionUpdate();
                        RecyclerAdapter.ViewHolder viewHolder = entry.getViewHolder();
                        if(entry instanceof Spacer){}else{
                            if(viewHolder.isSelected.getValue()) {
                                System.out.print("{" + viewHolder.orderInt.getValue() + "}");
                            }else{
                                System.out.print("{'" + viewHolder.orderInt.getValue() + "'}");
                            }}
                    }

                    }catch(NullPointerException e){ }




                }

        }

        @Override
        public void onSelectionChanged() {
            super.onSelectionChanged();

        }

    });


}

    
    @Override
    public void clickPosition(RecyclerAdapter.ViewHolder viewHolder,View view, int position) {


            if (view.getId() == R.id.checkBtn) {

                viewHolder.getEntry().checked.postValue(!viewHolder.getEntry().checked.getValue());

            }

        if(!isTimerRunning()) {
            if (view.getId() == R.id.setEntryTimeBtn) {

                viewHolder.transitionToSetTimer();

            }


            if (view.getId() == R.id.entryText) {

                View itemView = viewHolder.itemView;
                Entry entry = checkList.get(position);

                scrollPosition(position);

                CustomEditText editHolderText = itemView.findViewById(R.id.entryEditTxt);

                new DetectKeyboardBack(
                        itemView.getContext(),
                        editHolderText,
                        viewHolder.textView,
                        entry
                );

                selectionTracker.clearSelection();


            }


            if(view.getId() == R.id.subListBtn){

                showSubListSelection(fragmentView, position);

            }

        }

    }

    //UI dynamics

    public static void transitionToFileFromMain(Activity activity){

        MainFragmentDirections.ActionMainFragmentToFileListFragment action =
                MainFragmentDirections.actionMainFragmentToFileListFragment(JsonService.getJsonCheckArrayList());

        Navigation.findNavController(activity, R.id.entryListFragment).navigate(action);

    }

    public static void transitionToProgressFromMain(Activity activity){

        Navigation.findNavController(activity, R.id.entryListFragment).navigate(  MainFragmentDirections.actionMainFragmentToProgressFragment());

    }

    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);

    }

    public static void scrollPosition(int position, int offset){
        customLayoutManager.scrollToPositionWithOffset(position,offset);
    }

    public static void scrollPosition(int position){
        customLayoutManager.scrollToPositionWithOffset(position,100);
    }

    public void hideButtons(boolean hide){

        if(hide){
            binding.buttonPanel.setVisibility(View.GONE);
        }else {
            binding.buttonPanel.setVisibility(View.VISIBLE);
        }

    }

    public void hideTimeExecuteBtn(boolean hide){

        if(hide){
            binding.timerExecuteBtn.setVisibility(View.GONE);
        }else{
            binding.timerExecuteBtn.setVisibility(View.VISIBLE);
        }
    }



}