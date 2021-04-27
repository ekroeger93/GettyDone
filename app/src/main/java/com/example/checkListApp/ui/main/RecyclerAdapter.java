package com.example.checkListApp.ui.main;

        import android.animation.Animator;
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
        import android.view.animation.BounceInterpolator;
        import android.view.animation.OvershootInterpolator;
        import android.widget.Button;
        import android.widget.TableRow;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.databinding.adapters.SeekBarBindingAdapter;
        import androidx.lifecycle.LifecycleOwner;
        import androidx.lifecycle.Observer;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.checkListApp.database.EntryRepository;
        import com.example.checkListApp.R;
        import com.example.checkListApp.input.CustomEditText;
        import com.example.checkListApp.input.DetectKeyboardBack;

        import java.util.ArrayList;
        import java.util.List;

        //import static com.example.practice7.BR.test;




public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    private List<Entry> mList;
    private ViewGroup viewGroup;
    private LifecycleOwner owner;
    private EntryRepository repository;
    GestureDetector.SimpleOnGestureListener gestureDetector;

    private static int prevPosition;

    private static RecyclerView.ViewHolder selectedView;
    private static MainViewModel mainViewModel;

    private  RecyclerView recyclerView;




    public  void setMainViewModel(MainViewModel mainViewModel) {
        RecyclerAdapter.mainViewModel = mainViewModel;
    }

    public void setSelectedView(RecyclerView.ViewHolder viewHolder){
      if(viewHolder != null) {
          selectedView = viewHolder;
      }
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

    public void setRecyclerView(RecyclerView recyclerView){ this.recyclerView = recyclerView;}

    public RecyclerAdapter(){
        gestureDetector = new GestureDetector.SimpleOnGestureListener();
    }



    public void setList(List<Entry> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        this.viewGroup = parent;

                Context context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);


       // Inflate the custom layout
       View entryView = inflater.inflate(R.layout.entry, parent, false);

       // Return a new holder instance
       ViewHolder viewHolder = new ViewHolder(entryView);
       return viewHolder;


            }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
            public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {

            Entry ent = mList.get(position);

            ent.setViewHolder(holder);

        if(ent.getClass() == Spacer.class)
        {
            ent.getViewHolder().itemView.setVisibility(View.INVISIBLE);
            ent.getViewHolder().setIsRecyclable(false);

        }


       if(holder.getEntry().getClass() != Spacer.class && holder.getEntry()!=null)
        {
            holder.setListeners(holder.itemView);
            holder.setObservers(holder.getEntry());
        }



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

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TableRow tEntryViewRow;
        private final Button button;
        private MotionEvent motionEvent;



                public ViewHolder(@NonNull View itemView) {
                    super(itemView);


                    tEntryViewRow = itemView.findViewById(R.id.entry);
                    button = itemView.findViewById(R.id.checkBtn);
                    textView = itemView.findViewById(R.id.entryText);

                }


                public Entry getEntry() {
//                   return mList.get(getAdapterPosition());
                    return mList.get(getBindingAdapterPosition());
                }



                public void setListeners(View itemView ){



                    tEntryViewRow.setOnLongClickListener(view -> {

                        CustomEditText editHolderText = itemView.findViewById(R.id.entryEditTxt);

                        new DetectKeyboardBack(
                                itemView.getContext(),
                                editHolderText,
                                textView, getEntry());




                        for(Entry n : mList){

                            if(getEntry() != this.getEntry()){
                              itemView.findViewById(R.id.entryEditTxt).setVisibility(View.INVISIBLE);

                              itemView.findViewById(R.id.entryText).setVisibility(View.VISIBLE);
                            }

                        }



                      //  recyclerView.scrollToPosition(getBindingAdapterPosition());

                        float currentScroll;

                        currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();



                        Log.d("test"," "+ prevPosition + " "+getBindingAdapterPosition());


                            recyclerView.smoothScrollToPosition(getAbsoluteAdapterPosition() + 1);


                      //  viewGroup.scrollBy(0,(int)itemView.getY()-(itemView.getHeight()));

                        return false;

                    });

                    button.setOnClickListener(e ->{
                        getEntry().checked.postValue( !getEntry().checked.getValue());
                        //call the observer onChange()
                    });

//
//                    tEntryViewRow.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                            VelocityTracker velocityTracker = VelocityTracker.obtain();
//                            velocityTracker.addMovement(motionEvent);
//                            velocityTracker.computeCurrentVelocity(1);
//
//                            Log.d("track", ""+velocityTracker.getXVelocity());
//
//                            tEntryViewRow.setX( tEntryViewRow.getX() + (velocityTracker.getXVelocity()*40) );
//
//                            velocityTracker.recycle();
//
//                            if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
//                                motionEvent.getAction() == MotionEvent.ACTION_CANCEL
//                            ){
//                                tEntryViewRow.animate()
//                                        .translationX(0)
//                                        .setDuration(500)
//                                        .setInterpolator( new OvershootInterpolator())
//                                        .start();
//
//                            }
//
//
//                            return true;
//                        }
//                    });

                            // GestureDetector.SimpleOnGestureListener gestureDetector = new GestureDetector.SimpleOnGestureListener();
                            //gestureDetector.onDown( );


                }

                public void setObservers(Entry entry){

                    Observer<String> observer = new Observer() {
                        @Override
                        public void onChanged(Object o) {

//
                            textView.setText(o.toString());

                            repository.updateEntry(entry);
                            MainFragment.buildJson((ArrayList<Entry>) mList);



                        }
                    };

                    Observer<Boolean> checkObs = new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {

                            textView.setBackgroundColor( entry.checked.getValue() ?
                                    Color.GRAY:
                                    Color.parseColor("#FFF7B4")
                            );

                            tEntryViewRow.setBackgroundColor( entry.checked.getValue() ?
                                    Color.GRAY:
                                    Color.parseColor("#95FF8D")
                            );


                            repository.updateEntry(entry);
                            MainFragment.buildJson((ArrayList<Entry>) mList);


                        }
                    };



                    getEntry().textEntry.observe(owner,observer);
                    getEntry().checked.observe(owner,checkObs);


                }



            }



}
