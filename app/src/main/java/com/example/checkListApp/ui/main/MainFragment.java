package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.management.FileListFragment;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanel;
import com.example.checkListApp.ui.main.EntryManagement.ButtonPanelToggle;
import com.example.checkListApp.ui.main.EntryManagement.EntryItemManager;
import com.example.checkListApp.ui.main.EntryManagement.Operator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


/*
Objectives

-add in pull down/up to extender, change the recycler view Y size
    -use anchor points when dragging

-order consistent, save index in Json and load in proper order, use Entry ID

-? add in thread handler for moving items, delay timer?

-image button checkBox
-change buttons into icons
-animate entry on move and delete
-fix up the file manager, add transitions

-make tabs
https://www.tutlane.com/tutorial/android/android-tabs-with-fragments-and-viewpager

-delete multiple, delete on hover reveal multiple, all actions above it


-? Timer / schedule implementation


-? add image picture to entry camera/gallery
    -do in separate fragment

 */


public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private MainFragmentBinding binding;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private Context context;

    public static float recyclerScrollCompute,itemHeightPx, ratioOffset;

    private static ArrayList<Entry> checkList;

    private static String jsonCheckArrayList;


    Operator operator;
    ButtonPanel buttonPanel;
    ButtonPanelToggle buttonPanelToggle;
    EntryItemManager entryItemManager;

    public static ArrayList<Entry> getCheckList(){ return checkList;}

    //initialize
    public void initialize() {


        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        ratioOffset = 1.75f;

                        //adjusted so that selection is in middle of recyclerList
                        recyclerScrollCompute = recyclerView.getHeight() / ratioOffset;

                        // the dp height of item_entry
                        itemHeightPx = 100;

                        // Converts dip into its equivalent px
                        float dip = itemHeightPx;
                        Resources r = getResources();
                        float px = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                dip,
                                r.getDisplayMetrics());


                        itemHeightPx = px; // converted to px 262.5


                    }
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

            Log.d("test",args.getLoadJsonData());

        }catch (IllegalArgumentException e){
            e.printStackTrace();
            Log.d("test","no Data");
        }


    }

    public void setUpAdapter(){

        recyclerView = binding.ScrollView;

        adapter = new RecyclerAdapter();

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setRecyclerView(recyclerView);
        adapter.setOwner(getViewLifecycleOwner());
        adapter.setRepository(mViewModel.getRepository());
        adapter.setMainViewModel(mViewModel);
        adapter.setList(checkList);
        adapter.notifyDataSetChanged();

        operator = new Operator(recyclerView,adapter);
        entryItemManager = new EntryItemManager(context,mViewModel,operator);
        buttonPanel = new ButtonPanel(getContext());

        buttonPanelToggle = new ButtonPanelToggle(binding,getContext(),entryItemManager);
        entryItemManager.setButtonPanelToggle(buttonPanelToggle);


    }

@SuppressLint("ClickableViewAccessibility")
public void assignButtonListeners(){



    buttonPanel.addButton(binding.addDeleteBtn, view -> entryItemManager.add(), view -> entryItemManager.delete());
    buttonPanel.addButton(binding.editMoveBtn, view -> entryItemManager.edit(), view -> entryItemManager.move());

    binding.fileMenuBtn.setOnClickListener(view -> {

        MainFragmentDirections.ActionMainFragmentToFileListFragment action =
                MainFragmentDirections.actionMainFragmentToFileListFragment(jsonCheckArrayList);

        Navigation.findNavController(view).navigate(action);

    });

    binding.ScrollView.setOnScrollChangeListener(   (v, i, i1, i2, i3) -> {

        try {
            operator.getSelection();
        }catch (NullPointerException e){

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
        if (motionEvent.getAction() == MotionEvent.ACTION_UP){
        buttonPanel.setButtons();
        buttonPanel.checkWithinButtonBoundary(touch_X,touch_Y);
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



                buildJson(checkList);
            }



        }
    });


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

//    Log.d("test", jsonCheckList.toString());

//    Type userListType = new TypeToken<ArrayList<Entry>>(){}.getType();
//
//    if(test.length()>1){
//    ArrayList<Entry> userArray = gson.fromJson(String.valueOf(test), userListType);
//
//    for(Entry user : userArray) {
//       Log.d("test",""+user);
//    }
//    }

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