package com.example.checkListApp.ui.main.EntryManagement.ListComponent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;
import com.example.checkListApp.database.EntryRepository;
import com.example.checkListApp.databinding.EntryBinding;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.ui.main.CustomGridLayoutManager;
import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.EntryManagement.Record.RecordHelper;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.Spacer;

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

    public TrackerHelper trackerHelper;


    public List<ViewHolder> getViewHolderList() {
        return viewHolderList;
    }

    private SelectionTracker<Long> selectionTracker;
    private SelectionTracker<Long> savedSelectionTracker;

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
        private final Button button;

        public  int selectOrder = -1;
        public boolean selected = false;

      public  MutableLiveData<Integer> orderInt = new MutableLiveData<>(-1);
      public  MutableLiveData<Boolean> isSelected = new MutableLiveData<>(false);

        TrackerHelper.Details details;

        EntryBinding binding;

        public void updater(){


        }

                public ViewHolder(@NonNull EntryBinding itemView) {
                    super(itemView.getRoot());

                    binding = itemView;
                   details = new TrackerHelper.Details();

                    tEntryViewRow = binding.entry;
                    button = binding.checkBtn;
                    textView = binding.entryText;



                }


                public void selectionUpdate(){

                    if(selected){
                      //  button.setText((CharSequence) String.valueOf(orderInt.getValue()));
                        button.setText((CharSequence) String.valueOf(selectOrder));
                    }else{
                        button.setText(null);
                    }

                }

                void checkSelected(int position){

                    if(toggleTracker){
                       binding.executePendingBindings();

                 //   selectionUpdate();

                       details.position = position;



                        if (selectionTracker.isSelected(details.getSelectionKey())) {
                            button.setBackgroundColor(Color.RED);
                        }else{
                            button.setBackgroundColor(Color.BLUE);
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

//                    button.setOnClickListener(e ->{
//                        getEntry().checked.postValue( !getEntry().checked.getValue());
//                        //call the observer onChange()
//                    });


                    button.setOnTouchListener((view, motionEvent) -> {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            getEntry().checked.postValue( !getEntry().checked.getValue());
                        }

                        return true;
                    });

                }

                public void setObservers(Entry entry){

                    Observer<String> observer = o -> {

                        textView.setText(o);
                        entry.textTemp = o;
                        repository.updateEntry(entry);
                        MainFragment.buildJson((ArrayList<Entry>) mList);

                    };

                    Observer<Boolean> checkObs = aBoolean -> {

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

                        /*

                        show completed button here

                         */


                        repository.updateEntry(entry);
                        MainFragment.buildJson((ArrayList<Entry>) mList);


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

                    getEntry().textEntry.observe(owner,observer);
                    getEntry().checked.observe(owner,checkObs);


                }



            }



}
