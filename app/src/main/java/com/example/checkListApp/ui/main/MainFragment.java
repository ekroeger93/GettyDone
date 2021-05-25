package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanelToggle;
import com.example.checkListApp.ui.main.EntryManagement.EntryItemManager;
import com.example.checkListApp.ui.main.EntryManagement.LeafButton;
import com.example.checkListApp.ui.main.EntryManagement.Operator;
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

-image button checkBox
-change buttons into icons
-animate entry on move and delete
-fix up the file manager, add transitions


TaDone Prototype
--------------------------------------------------------------

-submit on complete when all are checked

    -record number checked
    -show finish button

    -log number of finished and date

-record complete goals and show in progress (calender) completed/date
    -date Time java api
    -record time completed and goals met

-? add reminder notification

-? save as pdf/rich text file -> print appMobilityPrint

-------------------------------------------------------------

-? Timer / schedule implementation


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

    private MainViewModel mViewModel;
    protected MainFragmentBinding binding;

    private RecyclerView recyclerView;
    private static RecyclerAdapter adapter;
    private Context context;

    public static float recyclerScrollCompute,itemHeightPx, ratioOffset;

    private static ArrayList<Entry> checkList = new ArrayList<>();

    SelectionTracker<Long> selectionTracker;

    private static String jsonCheckArrayList;

    public static String getJsonCheckArrayList() {
        return jsonCheckArrayList;
    }


    Operator operator;
    ButtonPanel buttonPanel;
    ButtonPanelToggle buttonPanelToggle;
    EntryItemManager entryItemManager;

    static public ToggleSwitchOrdering toggleSwitchOrdering;

    static public boolean isSorting = false;


    public static ArrayList<Entry> getCheckList(){ return checkList;}


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

        setRetainInstance(true);
        return binding.getRoot();

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // TODO: Use the ViewModel

      //  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);

        loadFile();

        setUpAdapter();

        initialize();

        assignButtonListeners();

        assignObservers();

        toggleSwitchOrdering = new ToggleSwitchOrdering();

    }



    public void loadFile(){

        try{
            MainFragmentArgs args = MainFragmentArgs.fromBundle(getArguments());

            ArrayList<Entry> loadedCheckList = getJsonGeneratedArray(args.getLoadJsonData());

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

        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }


    }

    public void setUpAdapter(){

        recyclerView = binding.ScrollView;

        adapter = new RecyclerAdapter();

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(false);
        adapter.setOwner(getViewLifecycleOwner());
        adapter.setRepository(mViewModel.getRepository());

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

    public static void transitionToFile(Activity activity){

        MainFragmentDirections.ActionMainFragmentToFileListFragment action =
                MainFragmentDirections.actionMainFragmentToFileListFragment(jsonCheckArrayList);


        Navigation.findNavController(activity, R.id.fragment).navigate(action);


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
                                adapter.trackerOn(false);

                                selectionTracker.clearSelection();
                                MainFragment.reInitializeAllSelection();

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

//        VelocityTracker velocityTracker = VelocityTracker.obtain();
//        velocityTracker.addMovement(motionEvent);
//        velocityTracker.computeCurrentVelocity(1);
//
//        Log.d("track", ""+velocityTracker.getXVelocity());
//
//        operator.currentViewHolder.itemView.setX( operator.currentViewHolder.itemView.getX() + (velocityTracker.getXVelocity()*40) );
//
//        velocityTracker.recycle();
//
//        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
//                motionEvent.getAction() == MotionEvent.ACTION_CANCEL
//        ){
//            operator.currentViewHolder.itemView.animate()
//                    .translationX(0)
//                    .setDuration(500)
//                    .setInterpolator( new OvershootInterpolator())
//                    .start();
//
//        }


        //We only need to check this once so lets do it on UP

        buttonPanel.setButtons();
        buttonPanel.checkWithinButtonBoundary(touch_X,touch_Y);

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



                if(!isSorting)
                updateToggleOrdering();

                if(isSorting) {
               //     toggleSwitchOrdering.showNumberList();
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

                        } catch (NullPointerException e) {
                        }
                    }

                       //updateToggleOrdering();




            try {


                System.out.println("");

                try{
                    for(Entry entry : getCheckList()){
                        //   entry.getViewHolder().selectionUpdate();
                        RecyclerAdapter.ViewHolder viewHolder = entry.getViewHolder();
                        if(entry instanceof Spacer){}else{
                            if(viewHolder.isSelected.getValue()) {
                                System.out.print("{" + viewHolder.orderInt.getValue() + "}");
                            }else{
                                System.out.print("{'" + viewHolder.orderInt.getValue() + "'}");
                            }}
                    }}catch (NullPointerException e){ }

//                entryCurrent.isSelected.postValue(toggleSwitchOrdering.listToOrder.get(index).toggle);
//                entryCurrent.orderInt.postValue(toggleSwitchOrdering.listToOrder.get(index).number);


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