package com.example.checkListApp.ui.main.entry_management.ListComponent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;
import com.example.checkListApp.database.EntryRepository;
import com.example.checkListApp.databinding.EntryBinding;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.timemanagement.TimeParcelBuilder;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.Record.RecordHelper;
import com.example.checkListApp.ui.main.MainFragmentDirections;
import com.example.checkListApp.ui.main.entries.Spacer;
import com.example.checkListApp.ui.main.data_management.JsonService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    private List<Entry> mList;

    private List<ViewHolder> viewHolderList;

    private LifecycleOwner owner;
    private EntryRepository repository;
    GestureDetector.SimpleOnGestureListener gestureDetector;
    private RecyclerView recyclerView;

    public boolean toggleTracker = true;
    public View setTimerView;

    public TrackerHelper trackerHelper;

    Activity activity;

    public List<ViewHolder> getViewHolderList() {
        return viewHolderList;
    }

    private SelectionTracker<Long> selectionTracker;
    private SelectionTracker<Long> savedSelectionTracker;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setTracker(SelectionTracker<Long> tracker){
        this.selectionTracker = tracker;
        this.savedSelectionTracker = tracker;

    }

    public void trackerOn(boolean turnOnTracker){
        toggleTracker = turnOnTracker;

        notifyDataSetChanged();
    }


    {
        setHasStableIds(false);
    }


   public void highlightSelected(RecyclerView.ViewHolder viewHolder){
       if(viewHolder != null) {
           viewHolder.itemView.setBackgroundColor(Color.RED);
       }
   }



    public void setOwner(LifecycleOwner owner) {
        this.owner = owner;
    }

    public void setRepository(EntryRepository repository) {
        this.repository = repository;
    }

    public RecyclerAdapter(){
        gestureDetector = new GestureDetector.SimpleOnGestureListener();
    }


    public TrackerHelper trackerHelper(){

    if(trackerHelper == null) trackerHelper = new TrackerHelper(recyclerView,this);

       return trackerHelper;

    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }



    public void setList(List<Entry> mList) {
        this.mList = mList;

    notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        setTimerView = new View(context);

        return new ViewHolder( EntryBinding.inflate(LayoutInflater.from(context), parent, false));


            }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
            public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {


        //holder.setViewHolder();

        holder.setIsRecyclable(!toggleTracker);

        Entry entry = mList.get(position);



        if(entry instanceof Spacer)
        {
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.setIsRecyclable(false);
//            entry.getViewHolder().itemView.setVisibility(View.INVISIBLE);
//            entry.getViewHolder().setIsRecyclable(false);

        }


        if(entry instanceof Entry)
        {


         //   if(entry.getViewHolder() == null)
                entry.setViewHolder(holder);

            holder.setListeners(holder.itemView);
            holder.setObservers(holder.getEntry());

            holder.selectionUpdate();

           //if(toggleTracker)
           holder.checkSelected(position);

        }






    }


    @Override
    public boolean onFailedToRecycleView(@NonNull @NotNull ViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @Override
            public int getItemCount() {
                return mList == null ? 0 : mList.size();
            }


    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);

        holder.itemView.setTranslationX(0f);
        holder.itemView.setTranslationY(0f);
        holder.itemView.setScaleY(1f);
        holder.itemView.setScaleX(1f);

//        holder.selectionUpdate(//holder.selectOrder
//                holder.orderInt.getValue()
//        );



    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TableRow tEntryViewRow;
        public final Button checkButton;
        private final Button setTimeButton;



        public  int selectOrder = -1;
        public boolean selected = false;
        public boolean enforceChecked = false;

      public  MutableLiveData<Integer> orderInt = new MutableLiveData<>(-1);
      public  MutableLiveData<Boolean> isSelected = new MutableLiveData<>(false);

        TrackerHelper.Details details;

        EntryBinding binding;



                public ViewHolder(@NonNull EntryBinding itemView) {
                    super(itemView.getRoot());

                    binding = itemView;
                   details = new TrackerHelper.Details();


                    tEntryViewRow = binding.entry;
                    checkButton = (Button) itemView.getRoot().findViewById(R.id.checkBtn);
                    setTimeButton = binding.setEntryTimeBtn;
                    textView = binding.entryText;



                }


                public void selectionUpdate(){

                    if(selected){
                      //  button.setText((CharSequence) String.valueOf(orderInt.getValue()));
                        checkButton.setText((CharSequence) String.valueOf(selectOrder));
                    }else{
                        checkButton.setText(null);
                    }

                }

                void checkSelected(int position){

                    if(toggleTracker){
                       binding.executePendingBindings();

                 //   selectionUpdate();

                       details.position = position;



                        if (selectionTracker.isSelected(details.getSelectionKey())) {
                            checkButton.setBackgroundColor(Color.RED);
                        }else{
                            checkButton.setBackgroundColor(Color.BLUE);
                        }

                 }



                }


                public Entry getEntry() {
                    try{
                    return mList.get(getBindingAdapterPosition());}
                    catch (ArrayIndexOutOfBoundsException e){
                        return null;
                    }
                }


        TrackerHelper.Details getItemDetails(){ return  details; }

       public Long getKey(){ return details.getSelectionKey();}

                @SuppressLint("ClickableViewAccessibility")
                public void setListeners(View itemView ){


                    itemView.setOnClickListener(view -> { });

                    tEntryViewRow.setOnLongClickListener(view -> {
                        CustomEditText editHolderText = itemView.findViewById(R.id.entryEditTxt);
                        new DetectKeyboardBack(
                                itemView.getContext(),
                                editHolderText,
                                textView, getEntry());


                            if(getEntry() != this.getEntry()){
                              itemView.findViewById(R.id.entryEditTxt).setVisibility(View.INVISIBLE);

                              itemView.findViewById(R.id.entryText).setVisibility(View.VISIBLE);
                            }



                        return false;

                    });

                    /*
                    TODO: Having issue with button not clicking on setOnClick
                   unless you use OnTouchListener... also setOnClick will activate
                   on manual override method via .onClick() but touch won't.
                    I don't understand whats going on
                     */


                    //FUCK IT USE BOTH
                    checkButton.setOnClickListener(e ->{
                        getEntry().checked.postValue( !getEntry().checked.getValue());
                        //call the observer onChange()
                    });

                    checkButton.setOnTouchListener((view, motionEvent) -> {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            getEntry().checked.postValue( !getEntry().checked.getValue());
                        }

                        return true;
                    });
                    //TODO: I FUCKED IT AND USED BOTH

                    //TODO: https://stackoverflow.com/questions/30284067/handle-button-click-inside-a-row-in-recyclerview

                    setTimeButton.setOnTouchListener((view, motionEvent)->{
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            transitionToSetTimer();
                        }
                        return true;
                    });
//
//                    setTimeButton.setOnClickListener( view -> {
//                        transitionToSetTimer();
//
//                    });


                }

                public void checkOff(){

                    checkButton.callOnClick();

                }

                public void setObservers(Entry entry){

                    Observer<String> observer = o -> {

                        textView.setText(o);
                        entry.textTemp = o;
                        repository.updateEntry(entry);
                        JsonService.buildJson((ArrayList<Entry>) mList);

                    };

                    Observer<Boolean> checkObs = aBoolean -> {

                        if (enforceChecked){
                            textView.setBackgroundColor(Color.GRAY);
                            tEntryViewRow.setBackgroundColor(Color.GRAY);

                        }

                        textView.setBackgroundColor( entry.checked.getValue() ?
                                Color.GRAY:
                                Color.parseColor("#FFF7B4")
                        );

                        tEntryViewRow.setBackgroundColor( entry.checked.getValue() ?
                                Color.GRAY:
                                Color.parseColor("#95FF8D")
                        );

                        entry.checkTemp = aBoolean.booleanValue();

                        RecordHelper.update();

                        repository.updateEntry(entry);
                        JsonService.buildJson((ArrayList<Entry>) mList);

                    };

                    Observer<String> changeTimeValue = o ->{

                 //       Log.d("timerTest",o);
                        setTimeButton.setText(o);
                        entry.timeTemp = o;

                        repository.updateEntry(entry);
                        JsonService.buildJson((ArrayList<Entry>) mList);
                    };


                    Observer selectOrderObs = (Observer) o -> {
                        selectOrder = orderInt.getValue();
                        selectionUpdate();
                        //   notifyItemChanged(getBindingAdapterPosition());
                    };

                    Observer selectCheckObs = (Observer) o ->{
                        selected = isSelected.getValue();
                    } ;


                    orderInt.observe(owner,selectOrderObs);
                    isSelected.observe(owner,selectCheckObs);

                    //TODO null value
                    if(getEntry().textEntry !=null && !MainFragment.executionMode) {
                        getEntry().textEntry.observe(owner, observer);
                        getEntry().checked.observe(owner, checkObs);
                        getEntry().countDownTimer.observe(owner, changeTimeValue);
                    }

                }


                public void transitionToSetTimer(){

                    MainFragmentDirections.ActionMainFragmentToSetTimerFragment action =
                            MainFragmentDirections.actionMainFragmentToSetTimerFragment(
                                );


                    action.setTimeParcel( new TimeParcelBuilder().setTimeIndexValue(getBindingAdapterPosition()).build() );
                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(action);

                }

            }



}