package com.example.checkListApp.ui.main.entry_management.list_component;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;
import com.example.checkListApp.database.EntryRepository;
import com.example.checkListApp.databinding.EntryBinding;
import com.example.checkListApp.time_management.parcel.TimeParcelBuilder;
import com.example.checkListApp.ui.main.ColorHelper;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entry_management.EntryItemManager;
import com.example.checkListApp.ui.main.entry_management.list_component.item_touch_helper.ItemTouchHelperAdapter;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.record.RecordHelper;
import com.example.checkListApp.ui.main.MainFragmentDirections;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;
import com.example.checkListApp.ui.main.data_management.JsonService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;


public class RecyclerAdapter
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
implements ItemTouchHelperAdapter {



    private ArrayList<Entry> mList;

    private final RecordHelper recordHelper;
    private final LifecycleOwner owner;
    private final EntryRepository repository;
    private final MainFragment mainFragment;

    GestureDetector.SimpleOnGestureListener gestureDetector;

    ListItemClickListener listItemClickListener;

    public boolean toggleTracker = true;
    public View setTimerView;

    public TrackerHelper trackerHelper;

    Activity activity;

    private SelectionTracker<Long> selectionTracker;

    public void setTracker(SelectionTracker<Long> tracker){
        this.selectionTracker = tracker;
    }

    public void trackerOn(boolean turnOnTracker){
        toggleTracker = turnOnTracker;
        trackerHelper.setIsMotionActive(turnOnTracker);


        notifyDataSetChanged();
    }


    {
        setHasStableIds(false);
    }


   public void highlightSelected(RecyclerView.ViewHolder viewHolder){
       if(viewHolder != null) {
           viewHolder.itemView.setBackgroundColor(new ColorHelper(mainFragment.getContext()).Entry_ItemView_Selected);
       }
   }



    public RecyclerAdapter(MainFragment fragment){

//        gestureDetector = new GestureDetector.SimpleOnGestureListener();

        mainFragment = fragment;

        this.owner = fragment.getViewLifecycleOwner();
        this.repository = fragment.getmViewModel().getRepository();
        this.activity = fragment.getActivity();
        this.recordHelper = fragment.getRecordHelper();

    }

    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public TrackerHelper trackerHelper(){

       return trackerHelper;

    }


    public void setTrackerHelper(RecyclerView recyclerView){
        trackerHelper = new TrackerHelper(recyclerView,this);
    }


//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//
//        this.recyclerView = recyclerView;
//    }
//

    public void deSetList(){

        this.mList.clear();
        notifyDataSetChanged();
    }

    public void setList(ArrayList<Entry> mList) {

        this.mList = mList;

        //TODO: having issues with notifyItemRangeInsert
        notifyDataSetChanged();


    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        setTimerView = new View(context);


        //LayoutInflater.from(parent.context).inflate(R.layout.log,parent,false);

        View view  = LayoutInflater.from(context).inflate( R.layout.entry,parent, false);

        EntryBinding entryBinding =  EntryBinding.inflate(LayoutInflater.from(context), parent, false);

        view.setLongClickable(false);

        //        viewHolder.onClick(view);



        return new ViewHolder(entryBinding,listItemClickListener);


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

        holder.itemView.setLongClickable(false);

        Entry entry = mList.get(position);

        if(entry instanceof Spacer)
        {
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.setIsRecyclable(false);
        }


        if(entry != null)
        {
           // if(entry.getViewHolder() == null)
            entry.setViewHolder(holder);

            holder.recordHelper = recordHelper;
        //    holder.setListeners(holder.itemView);
            holder.setObservers(holder.getEntry());

           // holder.selectionUpdate();
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


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onItemDismiss(int position) {

//        mList.remove(position);

        EntryItemManager.lastDeletedEntry = mainFragment.getCheckList().get(position);
        EntryItemManager.lastDeletedIndex = position;

        mainFragment.getmViewModel().deleteEntry(mainFragment.getCheckList().get(position));
        mainFragment.getMainUIDynamics().getOperator().updateIndexes();

        notifyItemRemoved(position);

        mainFragment.getMainUIDynamics().showUndoSnackBar();

    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
            , View.OnLongClickListener
    {

        public TextView textView;
        public TextView subTextView;
        public TableRow tEntryViewRow;
        public Button checkButton;
        private final Button setTimeButton;

        private Button subListButton;
        public TextView timerLabelText;

        private RecordHelper recordHelper;

        public  int selectOrder = -1;
        public boolean selected = false;
        public boolean enforceChecked = false;

      public  MutableLiveData<Integer> orderInt = new MutableLiveData<>(-1);
      public  MutableLiveData<Boolean> isSelected = new MutableLiveData<>(false);

      public MutableLiveData<Boolean> isGonnaShakeCauseImMovingIt = new MutableLiveData<>(false);
      public boolean hasAnimateMove = false;

        TrackerHelper.Details details;

        ListItemClickListener listItemClickListener;


        EntryBinding binding;

                public ViewHolder(@NonNull EntryBinding itemView, ListItemClickListener listItemClickListener) {
                    super(itemView.getRoot());

                    binding = itemView;
                    details = new TrackerHelper.Details(trackerHelper);

//                    itemView.getRoot().setLongClickable(false);

                    tEntryViewRow = binding.entry;
                    checkButton = binding.checkBtn;
                    setTimeButton = binding.setEntryTimeBtn;
                    textView = binding.entryText;
                    subListButton = binding.subListBtn;
                    timerLabelText = binding.timerLabelText;

                    subTextView = binding.entrySubText;

                    itemView.getRoot().setOnClickListener(this);
                    itemView.getRoot().setOnLongClickListener(this);

                    checkButton.setOnClickListener(this);
                    setTimeButton.setOnClickListener(this);
                    textView.setOnLongClickListener(this);
                    subListButton.setOnClickListener(this);




                    this.listItemClickListener = listItemClickListener;

//                    animatePopIn();


                    setObservers(getEntry());

                }

                public void animatePopIn(){

                    binding.getRoot().setScaleX(0.3f);
                    binding.getRoot().setScaleY(0.3f);
                    binding.getRoot().setAlpha(0f);

                    binding.getRoot().animate()
                            .scaleX(1)
                            .scaleY(1)
                            .alpha(1)
                            .setDuration(500)
                            .setInterpolator(new OvershootInterpolator())
                            .start();

                }

                public void animateShakeVertically(){

                   ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, -10 , 0 , 10, 0);
                   valueAnimator.setDuration(200)
                           .setRepeatCount(2);
                   valueAnimator.addUpdateListener(valueAnimator1 -> {
                       itemView.setTranslationY((Float)valueAnimator1.getAnimatedValue());
                   });

                   valueAnimator.start();
                }

                public void selectionUpdate(){

                    if(selected){
                        checkButton.setText((CharSequence) String.valueOf(selectOrder));
                    }else{
                        checkButton.setText(null);
                    }

                }

                @SuppressLint("ClickableViewAccessibility")
                void checkSelected(int position){

                    if(toggleTracker){
                       binding.executePendingBindings();

                 //   selectionUpdate();

                       details.position = position;

                       ColorHelper colorHelper = new ColorHelper(itemView.getContext());

                        if (selectionTracker.isSelected(details.getSelectionKey())) {
                            binding.getRoot().setBackgroundColor(colorHelper.Entry_ItemView_Selected);
                        }else{
                            checkButton.setBackgroundColor(colorHelper.Entry_ItemView);
                        }

                 }

                }

                public Entry getEntry() {
                    try{
                    return mList.get(getBindingAdapterPosition());}
                    catch (ArrayIndexOutOfBoundsException e){
                        return null;
                    }catch (NullPointerException e){
                        return mList.get(getLayoutPosition());
                    }


                }


                TrackerHelper.Details getItemDetails(){ return  details; }

                public Long getKey(){ return details.getSelectionKey();}

                public void checkOff(){

                    checkButton.callOnClick();

                }

                public void setObservers(Entry entry){

                    Observer<Boolean> onShaking = o ->{


                        if(o && !hasAnimateMove) {
                            animateShakeVertically();
                        hasAnimateMove = true;
                        }

                    };

                    isGonnaShakeCauseImMovingIt.observe(owner,onShaking);


                    Observer<String> onChangeEntryText = o -> {

                        textView.setText(o);
                        entry.textTemp = o;

                        repository.updateEntry(entry);
                        JsonService.buildJson(mList);

                    };

                    Observer<Boolean> onChangeEntryChecked = aBoolean -> {

//                        if (enforceChecked){
//                            textView.setBackgroundColor(Color.GRAY);
//                            tEntryViewRow.setBackgroundColor(Color.GRAY);
//                        }

                        checkButton.setBackground( entry.checked.getValue() ?
                                        ContextCompat.getDrawable(
                                                binding.entry.getContext(),
                                                R.drawable.outline_check_box_black_48)
                                        :
                                        ContextCompat.getDrawable(
                                                binding.entry.getContext(),
                                                R.drawable.outline_check_box_outline_blank_black_48)
                                );

//                        textView.setBackgroundColor( entry.checked.getValue() ?
//                                Color.GRAY:
//                                Color.parseColor("#FFF7B4")
//                        );
//
//                        tEntryViewRow.setBackgroundColor( entry.checked.getValue() ?
//                                Color.GRAY:
//                                Color.parseColor("#95FF8D")
//                        );


                        entry.checkTemp = aBoolean;

                        recordHelper.update(mList);
                        repository.updateEntry(entry);
                        JsonService.buildJson(mList);

                    };

                    Observer<String> onChangeTimeValue = o ->{

                        timerLabelText.setText(o);
                        entry.timeTemp = o;

                        repository.updateEntry(entry);
                        JsonService.buildJson((ArrayList<Entry>) mList);
                    };

//                    Observer<String> onActiveTimeLabelChange = o-> timerLabelText.setText(o);

                    Observer<Boolean> onChangeTogglePrimer = o->{

                        entry.onTogglePrimerTemp = o;

                        if(o) {
//                            setTimeButton.setBackgroundColor(Color.LTGRAY);
                            setTimeButton.setBackground(
                                    ContextCompat.getDrawable(
                                    binding.entry.getContext(),
                                    R.drawable.ic_ontogglecustom2)
                            );
                            timerLabelText.setText("");

                        }else{
                            setTimeButton.setBackground( ContextCompat.getDrawable(
                                    binding.entry.getContext(),
                                    R.drawable.outline_timer_black_48));
                        }
                        repository.updateEntry(entry);
                        JsonService.buildJson((ArrayList<Entry>) mList);
                    };




                    Observer<Integer> selectOrderObs = o -> {
                        selectOrder = o;//orderInt.getValue();
                        selectionUpdate();

                        //   notifyItemChanged(getBindingAdapterPosition());
                    };

                    Observer<Boolean> selectCheckObs = o ->{
                        selected = o;//isSelected.getValue();



                    };

                    Observer<String> onChangeSubFileName = subName->{

                        if(subName.isEmpty()){
                            subTextView.setText(R.string.entrySubTextNoLoaded);
                        }else{
                            subTextView.setText(subName.replace(".json",""));
                        }

                    };

                    orderInt.observe(owner,selectOrderObs);
                    isSelected.observe(owner,selectCheckObs);

                    //TODO null value

                    try {

                        if(getEntry() !=null) {
                            getEntry().textEntry.observe(owner, onChangeEntryText);
                            getEntry().checked.observe(owner, onChangeEntryChecked);
                            getEntry().countDownTimer.observe(owner, onChangeTimeValue);
                            getEntry().onTogglePrimer.observe(owner, onChangeTogglePrimer);
                            getEntry().subListName.observe(owner, onChangeSubFileName);
                        }
//                            getEntry().activeTimerLabel.observe(owner,onActiveTimeLabelChange);


                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }


                }

                public void transitionToSetTimer(){

                    MainFragmentDirections.ActionMainFragmentToSetTimerFragment action =
                            MainFragmentDirections.actionMainFragmentToSetTimerFragment(
                                );


                    action.setTimeParcel( new TimeParcelBuilder()
                            .setTimeIndexValue(getBindingAdapterPosition())
                            .setTimeStringValue(getEntry().timeTemp)
                        //    .setRetainJsonData(JsonService.getJsonCheckArrayList())
                            .build() );
                    Navigation.findNavController(activity, R.id.entryListFragment).navigate(action);




                }



        @Override
        public void onClick(View view) {

                    try {
                        listItemClickListener.clickPosition(this, view, getBindingAdapterPosition());
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

        }


        @Override
        public boolean onLongClick(View view) {
                   listItemClickListener.clickPosition(this,view,getBindingAdapterPosition());
            return true;
        }



    }



}
