package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.timer.TimeState;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel.ButtonPanel;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel.ButtonPanelToggle;
import com.example.checkListApp.ui.main.EntryManagement.EntryItemManager;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel.LeafButton;
import com.example.checkListApp.ui.main.EntryManagement.ListComponent.RecyclerAdapter;
import com.example.checkListApp.ui.main.EntryManagement.Operator;
import com.example.checkListApp.ui.main.EntryManagement.Record.RecordHelper;
import com.example.checkListApp.ui.main.data_management.AuxiliaryData;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.example.checkListApp.ui.main.data_management.ListUtility;
import com.example.checkListApp.ui.main.entries.Entry;
import com.example.checkListApp.ui.main.entries.Spacer;

import java.util.ArrayList;
import java.util.List;


/*
Objectives

-add in pull down/up to extender, change the recycler view Y size
    -use anchor points when dragging

-order consistent, save index in Json and load in proper order, use Entry ID
//cd /data/user/0/com.example.myfileio/files
-image button checkBox
-change buttons into icons
-animate entry on move and delete
-fix up the file manager, add transitions


TaDone Prototype
--------------------------------------------------------------
https://github.com/PhilJay/MPAndroidChart

-? save as pdf/rich text file -> print appMobilityPrint

-------------------------------------------------------------

-> get to toggle() run in background ? Runnable
-> ? could sum the time values into one and impl to GlobalTimer ?
-> notifications

class AppLifecycleListener : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) { // app moved to foreground
    }

    override fun onStop(owner: LifecycleOwner) { // app moved to background
    }
}


Collaboration
Tags
Calendar Sync
File Attachments
Smart Lists
Widgets
Natural Language Parsing
Repeatable Tasks
Reminders
Smart Assistant Support
Folders / Groups
Subtasks

graphing show progress
https://github.com/PhilJay/MPAndroidChart

calender schedule

revise countdown timer, instead of calling a new thread
do one continuous thread with each entry time summed unto
the global timer,

set Checkpoints based on individual entry time
when the global timer succeeds the an entry time
check off the entry.




 */


public class MainFragment extends Fragment {


    protected MainFragmentBinding binding;

    //reduce need for static
    public static float recyclerScrollCompute,itemHeightPx, ratioOffset;

    private EntryItemManager entryItemManager;
    private SelectionTracker<Long> selectionTracker;

    private Operator operator;
    private ButtonPanel buttonPanel;
    private ButtonPanelToggle buttonPanelToggle;

    private RecyclerAdapter adapter;
    private MainViewModel mViewModel;
    private RecyclerView recyclerView;

    private static CustomLayoutManager customLayoutManager;

    private static ArrayList<Entry> checkList = new ArrayList<>();
    private final MutableLiveData<Integer> selectedEntry = new MutableLiveData<>();

    //alot of classes rely on this being static
    public static ArrayList<Entry> getCheckList(){ return checkList;}

    private boolean isSorting = false;

    public static boolean executionMode = false;

    private MediaPlayer selectedAudio;

    private  MediaPlayer shortBell;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    shortBell = MediaPlayer.create(getContext(), R.raw.short_bell);


    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(inflater,R.layout.main_fragment,container,false);
        binding.setLifecycleOwner(this);

        //setRetainInstance(true);
        return binding.getRoot();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);


        checkList = AuxiliaryData.loadFile(checkList, mViewModel, getArguments());

        setUpAdapter();

        initialize();

        AuxiliaryData.receiveParcelTime(checkList,getArguments());

        assignButtonListeners();

        assignObservers();

        configureMainTimer();

        RecordHelper.createButton(getContext(),binding);


    }

    public void setUpAdapter(){

        recyclerView = binding.ScrollView;

        adapter = new RecyclerAdapter();

        recyclerView.setAdapter(adapter);
        customLayoutManager = new CustomLayoutManager(getContext());


        recyclerView.setLayoutManager(customLayoutManager);
        recyclerView.setHasFixedSize(false);
        adapter.setOwner(getViewLifecycleOwner());
        adapter.setRepository(mViewModel.getRepository());
        adapter.setActivity(getActivity());

//        checkList.add(new Spacer());
//        checkList.add(new Entry("a",false,"00:00:00"));
//       checkList.add(new Entry("b",false,"00:00:00"));

        adapter.setList(checkList);
        adapter.notifyDataSetChanged();


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

        operator = new Operator(recyclerView,adapter);
        entryItemManager = new EntryItemManager(getContext(),mViewModel,operator);
        buttonPanel = new ButtonPanel(getContext(), binding);
        buttonPanelToggle  = buttonPanel.buttonPanelToggle;

        entryItemManager.setButtonPanelToggle(buttonPanelToggle);

      //  enableScroll(false);


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


@RequiresApi(api = Build.VERSION_CODES.O)
public void configureMainTimer(){

        MainTimerView mainTimerView = new MainTimerView();


    //update text timer based on current scroll selected position
    //selectedEntry.observe(getViewLifecycleOwner(),mainTimerView.getObserver(checkList));

    //bind listener to button to toggle Time
    //mainTimerView.setListener(binding.timerExecuteBtn);



    binding.timerExecuteBtn.setOnClickListener(view -> {

        int setTime = setTimer(mainTimerView);
      //  mainTimerView.toggled.postValue(mainTimerView.mainTimerViewModel.isToggled());

        mainTimerView.mainTimerViewModel.setCountDownTimer(new TimeState(setTime).getTimeFormat());


        mainTimerView.mainTimerViewModel.toggleTimeWithCustomTask(time -> {

            int elapsedTime = setTime - time;

        if(ListUtility.currentActiveTime.timeElapsed(elapsedTime)){

            String message = ListUtility.activeProcessTimeIndex+" = "+ListUtility.currentActiveTime.timeAccumulated + " "+elapsedTime;
            Log.d("testTime",message);
            shortBell.start();

            new Handler(Looper.getMainLooper()).post(new Runnable () {
                @Override
                public void run () {
                    scrollPosition(ListUtility.activeProcessTimeIndex);
                }
            });

            ListUtility.currentActiveTime.getViewHolder().checkOff();
            ListUtility.currentActiveTime = ListUtility.getNextActiveProcessTime(checkList);



        }

        });
    });



    //
   // mainTimerView.setObserverForToggledLiveData(getViewLifecycleOwner(), aBoolean -> executionMode = aBoolean);

    //update time of both View and ViewModel
    mainTimerView.setObserverForMainTextTime(binding.timeTextMain,getViewLifecycleOwner());

    //set a post execution after timer expires, proceeds to next Entry
    mainTimerView.setPostExecute(() -> {
        checkList.get(checkList.size()-2).getViewHolder().checkOff();

    });


}


@RequiresApi(api = Build.VERSION_CODES.O)
public int setTimer(MainTimerView mainTimerView){

    int summationTime  = ListUtility.getSummationTime(checkList);
    String setTime = new TimeState(summationTime).getTimeFormat();

    mainTimerView.mainTimerViewModel.setCountDownTimer(setTime);

    ListUtility.accumulation(checkList);

    ListUtility.revertTimeIndex();
    ListUtility.currentActiveTime = checkList.get(1);

    return summationTime;
}

@SuppressLint("ClickableViewAccessibility")
public void assignButtonListeners(){

        buttonPanel.addButtonWithLeaf(
            binding.addDeleteBtn
            , view -> entryItemManager.add()
            , view -> entryItemManager.delete(),

            new LeafButton(getContext())
                    .setViewGroup(binding.main)
                    .assignListener(view -> {
                        buttonPanelToggle.setOnClickListener(
                                view1 -> { //deletes selected Entries
                            entryItemManager.deleteSelected(selectionTracker);
                            buttonPanelToggle.toggleDisableToButton();
                            adapter.trackerOn(false);
                        });
                        adapter.trackerOn(true);
                        buttonPanelToggle.toggleDisableToButton();
                    }).create()

    );

                buttonPanel.addButtonWithLeaf(
                        binding.editMoveBtn,
                        view -> entryItemManager.edit(),
                        view -> entryItemManager.move(),
                        new LeafButton(getContext())
                                .setViewGroup(binding.main)
                        .assignListener(view ->{

                            //I'm having a tough time with the recyclerView
                            //for some reason the object references (viewHolders) are disorganized
                            //until you scroll the entirety of the list so
                            //I'm starting it on 0 and setting setHasFixedSize here

                            recyclerView.scrollToPosition(0);
                            recyclerView.setHasFixedSize(true);

                            buttonPanelToggle.setOnClickListener(view1 -> {

                                isSorting = true;
                                entryItemManager.sortSelected(selectionTracker);
                                buttonPanelToggle.toggleDisableToButton();
                                adapter.notifyDataSetChanged();

                                selectionTracker.clearSelection();
                                ListUtility.reInitializeAllSelection(checkList);
                                adapter.trackerOn(false);

                            });

                            //thread may still be running!!!
                            selectionTracker.clearSelection();
                            ListUtility.reInitializeAllSelection(checkList);

                            isSorting = false;

                            adapter.trackerOn(true);
                            buttonPanelToggle.toggleDisableToButton();


                        }).create()
                );



    binding.ScrollView.setOnScrollChangeListener(   (v, i, i1, i2, i3) -> {

        try {
          selectedEntry.postValue(operator.getSelection());

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(operator.isMovingItem) {
            operator.moveItem(operator.movingItem);

        }
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

        operator.currentViewHolder.itemView.setTranslationX(xMove);
        velocityTracker.recycle();

        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL
        ){
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


    mViewModel.getAllEntries().observe(getViewLifecycleOwner(),new Observer<List<Entry>>() {

        @Override
        public void onChanged(@Nullable final List<Entry> entries) {

            //makes sure we keeps those spacers at the ends
            if( checkList == null || checkList.size()-2 != entries.size()
            ) {
                checkList = (ArrayList<Entry>) entries;
                checkList.add(0, new Spacer());
                checkList.add(checkList.size(), new Spacer());
                adapter.setList(checkList);

                RecordHelper.update();

                if(!isSorting){
                checkList = ListUtility.updateToggleOrdering(checkList);
                }else{ ListUtility.updateAllSelection(checkList);
                }

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

            for (Entry entry : getCheckList()) {
                try {
                    if (entry instanceof Spacer) {} else {

                        if (key.equals(entry.getViewHolder().getKey())) {

                            entryCurrent = entry.getViewHolder();
                            index = getCheckList().indexOf(entry) - 1;

                            //I need the adapter to re-realize the list size
                            //due to the adapter erroneous size prior to cleaning
                            adapter.getItemCount();//this is why its called

                           // entryCurrent.isSelected.setValue(!entryCurrent.isSelected.getValue());

                            /*
                            ultimately the holder.orderInt should reflect the toggleSwitchOrdering.list
                            * */

                            ListUtility.toggleSwitchOrdering.toggleNum(index);

                            entryCurrent.isSelected.setValue(ListUtility.toggleSwitchOrdering.listToOrder.get(index).toggle);
                            entryCurrent.orderInt.setValue(ListUtility.toggleSwitchOrdering.listToOrder.get(index).number);

                            entryCurrent.selectOrder = ListUtility.toggleSwitchOrdering.listToOrder.get(index).number;

                            ListUtility.updateAllSelection(checkList);

                           break;
                                }

                            }

                        } catch (NullPointerException  | IndexOutOfBoundsException a) {
                        }
                    }





            try {

                    for(Entry entry : getCheckList()){
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



}