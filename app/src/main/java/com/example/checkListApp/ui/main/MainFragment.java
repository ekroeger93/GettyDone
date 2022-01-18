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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainActivityBinding;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.timemanagement.MainTimerView;
import com.example.checkListApp.timemanagement.TimerService;
import com.example.checkListApp.timemanagement.parcel.ListTimersParcel;
import com.example.checkListApp.timemanagement.parcel.ListTimersParcelBuilder;
import com.example.checkListApp.timemanagement.utilities.KeyHelperClass;
import com.example.checkListApp.timer.CountDownTimerAsync;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.entry_management.ButtonPanel.ButtonPanel;
import com.example.checkListApp.ui.main.entry_management.ButtonPanel.ButtonPanelToggle;
import com.example.checkListApp.ui.main.entry_management.EntryItemManager;
import com.example.checkListApp.ui.main.entry_management.ButtonPanel.LeafButton;
import com.example.checkListApp.ui.main.entry_management.EntryTimerProcesses;
import com.example.checkListApp.ui.main.entry_management.ListComponent.CustomLayoutManager;
import com.example.checkListApp.ui.main.entry_management.ListComponent.ListItemClickListener;
import com.example.checkListApp.ui.main.entry_management.ListComponent.RecyclerAdapter;
import com.example.checkListApp.ui.main.entry_management.ListComponent.item_touch_helper.ItemTouchCallback;
import com.example.checkListApp.ui.main.entry_management.Operator;
import com.example.checkListApp.ui.main.entry_management.Record.RecordHelper;
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


//TODO: BUGS

   -service
  -repeater time needs to be added to database
  -entry timer not setting?
  -progress overwritten last entries, after a week


//TODO: DESIGN

- entry time label redesign

- show swiping hand icon for hint

- file manager
    -each viewholder have trash icon to delete
    -prompt confirm

- color theme



//TODO: Features

-tips

-back hopper, when timer expires "hops" back
to previous entry and restoring time duration
for a number of iterations
-the entry being hopped back to cannot implement
a back hopper

-color code Entry Lists for graphing,
add legend keys


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
//    private final AuxiliaryData auxiliaryData  = new AuxiliaryData(this);

    private RecyclerAdapter adapter;
    private MainViewModel mViewModel;
    private RecyclerView recyclerView;

    private static CustomLayoutManager customLayoutManager;

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

    public EntryTimerProcesses entryTimerProcesses;

    private boolean isSorting = false;


    private static final MutableLiveData<Boolean> timerRunning = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> getTimerRunning() { return timerRunning; }

    public static Boolean isTimerRunning()   { return timerRunning.getValue();}


    private MediaPlayer[] selectedAudio;

    private final MainTimerView mainTimerView = new MainTimerView();
    public MainTimerView getMainTimerView() { return mainTimerView; }

    ListTimersParcel listTimersParcel;

    private final ListUtility listUtility = new ListUtility();

    private Activity activity;

    private Intent serviceIntent;
    public Intent getServiceIntent() { return serviceIntent; }

    //    private ItemTouchHelper.Callback callback;
    private  ItemTouchCallback callback;

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


        setUpAdapter();

        initialize();

        //TODO: selectionTracker interference with onClick!!!

        assignButtonListeners();

        //TODO: investigate here too
        assignObservers();

//        configureMainTimer();

        entryTimerProcesses = new EntryTimerProcesses(this);

        entryTimerProcesses.configureMainTimer();


        recordHelper.createButton(getContext(),binding);

        MainTimerView.mainTimerViewModel.setRepeaterTime(Entry.globalCycler);


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


        if(!getArguments().isEmpty()) {

//            Log.d("loadTest", ""+ (getArguments().get(KeyHelperClass.TIME_PARCEL_DATA) == null));

//            Log.d("loadTest","json "+JsonService.getJsonGeneratedArray());

            Log.d("loadTest", " "+ MainFragmentArgs.fromBundle(getArguments()).getJsonData().isEmpty());



            if(
                    //AuxiliaryData.loadFile(getArguments()) != null
                    !MainFragmentArgs.fromBundle(getArguments()).getJsonData().isEmpty()
            ){

                mViewModel.deleteAllEntries(checkList);
                checkList = AuxiliaryData.loadFile(getArguments());
                _checkList.setValue(checkList);
                for(Entry entry : getCheckList()) mViewModel.loadEntry(entry);
                updateIndexes();

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


    public static void scrollPosition(int position){
        customLayoutManager.scrollToPositionWithOffset(position,100);
    }

    public static void scrollPosition(int position, int offset){
        customLayoutManager.scrollToPositionWithOffset(position,offset);
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
                .setGlobalTimer(MainTimerView.mainTimerViewModel.getValueTime())
                .setIndexActive(listUtility.activeProcessTimeIndex).build();


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


    public void configureMainTimer(){


    //update text timer based on current scroll selected position

        CountDownTimerAsync.CountDownTask countDownTask = time -> {
         {

                int elapsedTime = listUtility.getSummationTime(checkList) - time;

                if(listUtility.currentActiveTime == null) listUtility.currentActiveTime = checkList.get(1);

                processTimerTask(elapsedTime);


         };

        };


    binding.timerExecuteBtn.setOnClickListener(view -> {


        setTimer();

        if(MainTimerView.mainTimerViewModel.isToggled()) {
            binding.timerExecuteBtn.setBackground(
                    ContextCompat.getDrawable(
                            getContext(),
                            R.drawable.outline_play_circle_filled_black_48
                    ));
        }else{
            binding.timerExecuteBtn.setBackground(
                    ContextCompat.getDrawable(
                            getContext(),
                            R.drawable.outline_pause_circle_filled_black_48
                    ));

        }

        if( listUtility.getSummationTime(checkList)  > 0 ) {

            startService();

            if(binding.repeatTimer.getText().toString().isEmpty()){
                binding.repeatTimer.setText("0");
            }

                int repeater = Integer.parseInt(binding.repeatTimer.getText().toString());
                MainTimerView.mainTimerViewModel.setRepeaterTime(repeater);


            timerRunning.postValue(true);

            MainTimerView.mainTimerViewModel.setTaskCustom(countDownTask);

            MainTimerView.mainTimerViewModel.toggleTime();

        }

    });

    binding.timerExecuteBtn.setOnLongClickListener(view -> {

        //hideButtons(false);


        MainTimerView.mainTimerViewModel.resetAbsolutely();
        timerRunning.postValue(false);

//        if(isMyServiceRunning(TimerService.class))
           activity.stopService(serviceIntent);

        return  true;
    });

    //update time of both View and ViewModel
    mainTimerView.setObserverForMainTextTime(binding.timeTextMain,getViewLifecycleOwner());

    //set a post execution after timer expires, proceeds to next Entry
    mainTimerView.setPostExecute(() -> {

        displayEntryToast();

        if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= -1) {

            //go back to top of list
            MainTimerView.mainTimerViewModel.resetTimeState();
            timerRunning.postValue(false);

        }
        else {

            endOfTimerTask(countDownTask);

        }



    });


}

    public int setTimer(){

        if(MainTimerView.mainTimerViewModel.getNumberValueTime() == 0) {
            int summationTime = listUtility.getSummationTime(checkList);
            String setTime = new TimeState(summationTime).getTimeFormat();
            MainTimerView.mainTimerViewModel.setCountDownTimer(setTime);

            listUtility.accumulation(checkList);

            listUtility.revertTimeIndex();
            listUtility.currentActiveTime = checkList.get(1);

            for(Entry entry: checkList){ entry.checked.postValue(false); }

            return summationTime;

        }else{
            listUtility.accumulation(checkList);

            return listUtility.getSummationTime(checkList);
        }
}

    public static void resetTime(){

        MainTimerView.mainTimerViewModel.resetAbsolutely();
        timerRunning.postValue(false);

    }

    public void displayEntryToast(){

        if (getActivity() != null && getContext() !=null) {
            getActivity().runOnUiThread(() -> {

                String message = checkList.get(listUtility.activeProcessTimeIndex-1).textEntry.getValue();

                scrollPosition(listUtility.activeProcessTimeIndex);

                Toast toast = Toast.makeText(getContext(), message + " done!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();


            });
        }

    }

    public void processTimerTask(int elapsedTime){

        if(!listUtility.currentActiveTime.onTogglePrimerTemp){
            if (listUtility.currentActiveTime.timeElapsed(elapsedTime)) {

                //selectedAudio[checkList.get(listUtility.activeProcessTimeIndex).getSelectAudio()].start();

                playAudio(listUtility.currentActiveTime.getSelectAudio());


                displayEntryToast();

                if (listUtility.currentActiveTime.numberValueTime != 0) {

                    //if repeater time is 0 check off
                    if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= 0)
                        listUtility.currentActiveTime.getViewHolder().checkOff();

                    listUtility.currentActiveTime = listUtility.getNextActiveProcessTime(checkList);
                }


            }
        }
        else{
            MainTimerView.mainTimerViewModel.toggleTime();

            if (MainTimerView.mainTimerViewModel.getRepeaterTime() <= 0) {
                listUtility.currentActiveTime.getViewHolder().checkOff();
            }

            listUtility.currentActiveTime = listUtility.getNextActiveProcessTime(checkList);

        }

    }

    public void endOfTimerTask( CountDownTimerAsync.CountDownTask countDownTask){

//        selectedAudio[checkList.get(listUtility.activeProcessTimeIndex).getSelectAudio()].start();

        playAudio(listUtility.currentActiveTime.getSelectAudio());

        listUtility.currentActiveTime.getViewHolder().checkOff();

        listUtility.revertTimeIndex();
        listUtility.currentActiveTime = checkList.get(1);
        MainTimerView.mainTimerViewModel.setTaskCustom(countDownTask);


    }


    public void playAudio(int audio){
        selectedAudio[audio].start();
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

                Entry.globalCycler = value;
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

               if (operator.isMovingItem) {

//                   operator.moveItem(operator.movingItem);


               }


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

                binding.repeatTimer.setText(""+Entry.globalCycler);

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

        }

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


}