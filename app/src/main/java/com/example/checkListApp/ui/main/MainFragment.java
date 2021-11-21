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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

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
    private static CustomGridLayoutManager customGridLayoutManager;
    private static String jsonCheckArrayList;
    private static ArrayList<Entry> checkList = new ArrayList<>();

    public static float recyclerScrollCompute,itemHeightPx, ratioOffset;
    static public ToggleSwitchOrdering toggleSwitchOrdering;
    static public boolean isSorting = false;
    public static ArrayList<Entry> getCheckList(){ return checkList;}
    public static String getJsonCheckArrayList() {
        return jsonCheckArrayList;
    }

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



        loadFile();

        getParcelTime();

        setUpAdapter();

        initialize();

        assignButtonListeners();

        assignObservers();



        toggleSwitchOrdering = new ToggleSwitchOrdering();

        RecordHelper.createButton(getContext(),binding);


    }



    private void getParcelTime() {

        try {
            SetTimerFragmentArgs args = SetTimerFragmentArgs.fromBundle(getArguments());
            timeParcel = args.getTimeParcel();
            loadParcelTime();
        }catch (NullPointerException e){
            Log.d("timeParcel","error");
        }

    }

    private void loadParcelTime(){


        checkList.get(timeParcel.getTimeIndex()).countDownTimer.postValue(timeParcel.getTimeStringVal());

        Log.d("timeParcel",":: " + checkList.get(timeParcel.getTimeIndex()).countDownTimer.getValue());

    }

    public void loadFile(){

        try{
            MainFragmentArgs args = MainFragmentArgs.fromBundle(getArguments());

           if (args.getJsonData().contains("[")){

            ArrayList<Entry> loadedCheckList = getJsonGeneratedArray(args.getJsonData());

            if(loadedCheckList != null){

                for(Entry entry : loadedCheckList){
                    entry.textEntry.setValue(entry.textEntry.getValue().replaceAll("\"" , ""));
                }

                if(checkList.size() > 0){
                    mViewModel.deleteAllEntries(checkList);
                }

                for(Entry entry : loadedCheckList) {
                    mViewModel.loadEntry(entry);
                    Log.d("test",entry.textEntry.getValue());
                }
                loadedCheckList.add(0,new Spacer());
                loadedCheckList.add(new Spacer());

                checkList.addAll(loadedCheckList);


            }
           }

        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }


    }

    public void setUpAdapter(){

        recyclerView = binding.ScrollView;

        adapter = new RecyclerAdapter();

        recyclerView.setAdapter(adapter);
        customGridLayoutManager = new CustomGridLayoutManager(context);




        recyclerView.setLayoutManager(customGridLayoutManager);
        recyclerView.setHasFixedSize(false);
        adapter.setOwner(getViewLifecycleOwner());
        adapter.setRepository(mViewModel.getRepository());
        adapter.setActivity(getActivity());

//        checkList.add(new Spacer());
//        checkList.add(new Entry("a",false));
//       checkList.add(new Entry("b",false));



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


    }

    public static void toggleDisableScroll(boolean flag){
        customGridLayoutManager.setScrollEnabled(flag);
    }

    public static void transitionToFileFromMain(Activity activity){

        MainFragmentDirections.ActionMainFragmentToFileListFragment action =
                MainFragmentDirections.actionMainFragmentToFileListFragment(jsonCheckArrayList);

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


                                selectionTracker.clearSelection();
                                MainFragment.reInitializeAllSelection();
                                adapter.trackerOn(false);

                            });

                            //thread may still be running!!!
                            selectionTracker.clearSelection();
                            MainFragment.reInitializeAllSelection();


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



        if(motionEvent.getAction() == MotionEvent.ACTION_UP){

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
            if(checkList == null || checkList.size()-2 != entries.size()) {
                checkList = (ArrayList<Entry>) entries;

                checkList.add(0, new Spacer());

                checkList.add(checkList.size(), new Spacer());
                adapter.setList(checkList);

                RecordHelper.update();

                if(!isSorting)
                updateToggleOrdering();

                if(isSorting) {
                    MainFragment.updateAllSelection();
                }

                buildJson(checkList);
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
                RecyclerAdapter.ViewHolder entryCurrent = null;
            Entry entrySelected = null;
            int index = 0;

            for (Entry entry : getCheckList()) {
                try {
                    if (entry instanceof Spacer) {} else {

                        if (key.equals(entry.getViewHolder().getKey())) {

                            entryCurrent = entry.getViewHolder();
                            index = getCheckList().indexOf(entry) - 1;

                            //I need the adapter to re-realize the list size
                            //due to the adapter erroneous size prior to cleaning
                            adapter.getItemCount();//this is why its called

                            System.out.println("index: "+index);
                           // entryCurrent.isSelected.setValue(!entryCurrent.isSelected.getValue());

                            /*
                            ultimately the holder.orderInt should reflect the toggleSwitchOrdering.list

                            * */

                            toggleSwitchOrdering.toggleNum(index);

                            entryCurrent.isSelected.setValue(toggleSwitchOrdering.listToOrder.get(index).toggle);
                            entryCurrent.orderInt.setValue(toggleSwitchOrdering.listToOrder.get(index).number);

                            entryCurrent.selectOrder =toggleSwitchOrdering.listToOrder.get(index).number;

                            MainFragment.updateAllSelection();

                           break;
                                }

                            }

                        } catch (NullPointerException  | IndexOutOfBoundsException a) {
                        }
                    }

                       //updateToggleOrdering();




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

    static public void updateToggleOrdering(){

        toggleSwitchOrdering.listToOrder.clear();

        int count = 0;
        for(Entry entry : getCheckList()) {
            int index = getCheckList().indexOf(entry);


            if(entry instanceof Spacer) {}else {
                count++;
                toggleSwitchOrdering.listToOrder
                        .add(new ToggleSwitchOrdering.tNumber(
                                count, false));
            }
        }

    }

    public static void updateAllSelection(){


        for(Entry entry : getCheckList()){
            try {
                if (entry instanceof Spacer) {
                } else {
                    int index= getCheckList().indexOf(entry);

                    for(ToggleSwitchOrdering.tNumber tNumber : MainFragment.toggleSwitchOrdering.listToOrder) {
                        int indexOf = MainFragment.toggleSwitchOrdering.listToOrder.indexOf(tNumber);
                        if(indexOf == index-1){
                        entry.getViewHolder().isSelected.postValue(tNumber.toggle);
                        entry.getViewHolder().orderInt.postValue(tNumber.number);
                       break;
                       }


                    }
                   entry.getViewHolder().selectionUpdate();
                }
            }catch (NullPointerException | IndexOutOfBoundsException e){

            }
      }






    }

    public static void reInitializeAllSelection(){

        System.out.println("clearing...");

        for(Entry entry : getCheckList()){
            try {
                if (entry instanceof Spacer) {
                } else {

                    entry.getViewHolder().orderInt.postValue(-1);
                    entry.getViewHolder().isSelected.postValue(false);
                    entry.getViewHolder().selectOrder=0;
                    entry.swappable = true;
                    entry.getViewHolder().selectionUpdate();

                //    System.out.print("["+entry.getViewHolder().orderInt.getValue()+']');
                }
            }catch (NullPointerException | IndexOutOfBoundsException e){

            }

        }


        toggleSwitchOrdering.listToOrder.clear();
        updateToggleOrdering();
        updateAllSelection();


    }


static public void buildJson(ArrayList<Entry> checkList){

    Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Entry.class, new SerializeEntryToJson())
            .registerTypeAdapter(Entry.class, new DeserializeJsonToEntry())
            .create();


    StringBuilder jsonCheckList = new StringBuilder();


    jsonCheckList.append("[");
    for(Entry entry : checkList){

        if (entry.getClass() == Entry.class)
        jsonCheckList.append(gson.toJson(entry)).append(",");
    }

    jsonCheckList.deleteCharAt(jsonCheckList.length()-1);
    jsonCheckList.append("]");


    jsonCheckArrayList = String.valueOf(jsonCheckList);

}

public ArrayList<Entry> getJsonGeneratedArray(String json){

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Entry.class, new SerializeEntryToJson())
                .registerTypeAdapter(Entry.class, new DeserializeJsonToEntry())
                .create();

        ArrayList<Entry> entryArrayList;
        Type userListType = new TypeToken<ArrayList<Entry>>(){}.getType();

        entryArrayList = gson.fromJson(String.valueOf(json), userListType);

        return entryArrayList;
    }


    static class SerializeEntryToJson implements JsonSerializer<Entry>{


        @Override
        public JsonElement serialize(Entry src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("textEntry",src.textEntry.getValue());
            jsonObject.addProperty("isChecked",src.checked.getValue());

            return jsonObject;
        }


    }

    static class DeserializeJsonToEntry implements JsonDeserializer<Entry>{


        @Override
        public Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();

            String textEntry = "";

            textEntry = jsonObject.get("textEntry").toString();
            boolean isChecked = jsonObject.get("isChecked").getAsBoolean();


            return new Entry(textEntry,isChecked);


        }

    }



}