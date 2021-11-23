package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.file_management.FileListFragmentArgs;
import com.example.checkListApp.settimer.SetTimerFragmentArgs;
import com.example.checkListApp.timemanagement.TimeParcel;
import com.example.checkListApp.timemanagement.TimeParcelBuilder;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel.ButtonPanel;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel.ButtonPanelToggle;
import com.example.checkListApp.ui.main.EntryManagement.EntryItemManager;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel.LeafButton;
import com.example.checkListApp.ui.main.EntryManagement.ListComponent.RecyclerAdapter;
import com.example.checkListApp.ui.main.EntryManagement.ListComponent.ToggleSwitchOrdering;
import com.example.checkListApp.ui.main.EntryManagement.Operator;
import com.example.checkListApp.ui.main.EntryManagement.Record.RecordHelper;
import com.example.checkListApp.ui.main.data_management.AuxiliaryData;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.example.checkListApp.ui.main.data_management.ListRefurbishment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

-? add reminder notification
-? save as pdf/rich text file -> print appMobilityPrint

-------------------------------------------------------------

Type Entries
- Timer
    -checks when expired
    -has delay start period
    -proceeds to next timer (chained)
- Counter.0

    -checks when at zero



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


 */


public class MainFragment extends Fragment {


    protected MainFragmentBinding binding;


    private Context context;
    private MainViewModel mViewModel;
    private RecyclerView recyclerView;

    private static RecyclerAdapter adapter;
    private static CustomLayoutManager customLayoutManager;
    private static volatile ArrayList<Entry> checkList = new ArrayList<>();

    public static float recyclerScrollCompute,itemHeightPx, ratioOffset;
    static public boolean isSorting = false;
    public static ArrayList<Entry> getCheckList(){ return checkList;}
    public static void setCheckList(ArrayList<Entry> data){checkList = data;}



    SelectionTracker<Long> selectionTracker;
    Operator operator;
    ButtonPanel buttonPanel;
    ButtonPanelToggle buttonPanelToggle;
    EntryItemManager entryItemManager;

    TimeParcel timeParcel;


    //initialize
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);



        AuxiliaryData.loadFile(checkList, mViewModel, this);

        AuxiliaryData.receiveParcelTime(checkList,this);

        setUpAdapter();

        initialize();

        assignButtonListeners();

        assignObservers();




        RecordHelper.createButton(getContext(),binding);


    }






    public void setUpAdapter(){

        recyclerView = binding.ScrollView;

        adapter = new RecyclerAdapter();

        recyclerView.setAdapter(adapter);
        customLayoutManager = new CustomLayoutManager(context);



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
        entryItemManager = new EntryItemManager(context,mViewModel,operator);
        buttonPanel = new ButtonPanel(getContext(), binding);
        buttonPanelToggle  = buttonPanel.buttonPanelToggle;

        entryItemManager.setButtonPanelToggle(buttonPanelToggle);

      //  enableScroll(false);


    }

    public static void enableScroll(boolean flag){
        customLayoutManager.setScrollEnabled(flag);
    }

    public static void scrollToPosition(RecyclerView recyclerView,int position){
        recyclerView.scrollToPosition(position);
    }

    public static void transitionToFileFromMain(Activity activity){

        MainFragmentDirections.ActionMainFragmentToFileListFragment action =
                MainFragmentDirections.actionMainFragmentToFileListFragment(JsonService.getJsonCheckArrayList());

        Navigation.findNavController(activity, R.id.entryListFragment).navigate(action);

    }

    public static void transitionToProgressFromMain(Activity activity){

        Navigation.findNavController(activity, R.id.entryListFragment).navigate(  MainFragmentDirections.actionMainFragmentToProgressFragment());

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
                        buttonPanelToggle.setOnClickListener(view1 -> {
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
                                ListRefurbishment.reInitializeAllSelection(checkList);
                                adapter.trackerOn(false);

                            });

                            //thread may still be running!!!
                            selectionTracker.clearSelection();
                            ListRefurbishment.reInitializeAllSelection(checkList);


                            isSorting = false;

                            adapter.trackerOn(true);
                            buttonPanelToggle.toggleDisableToButton();


                        }).create()
                );



    binding.ScrollView.setOnScrollChangeListener(   (v, i, i1, i2, i3) -> {

        try {
            operator.getSelection();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(operator.isMovingItem)
        operator.moveItem(operator.movingItem);

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

                if(!isSorting)
                ListRefurbishment.updateToggleOrdering(checkList);

                if(isSorting) {
                    ListRefurbishment.updateAllSelection(checkList);
                }

                for(Entry entry : checkList) {
                    Log.d("checkListTest", "..." + entry);
                }

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

                            ListRefurbishment.toggleSwitchOrdering.toggleNum(index);

                            entryCurrent.isSelected.setValue(ListRefurbishment.toggleSwitchOrdering.listToOrder.get(index).toggle);
                            entryCurrent.orderInt.setValue(ListRefurbishment.toggleSwitchOrdering.listToOrder.get(index).number);

                            entryCurrent.selectOrder =ListRefurbishment.toggleSwitchOrdering.listToOrder.get(index).number;

                            ListRefurbishment.updateAllSelection(checkList);

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




}