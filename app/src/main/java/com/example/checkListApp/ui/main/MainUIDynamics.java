package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.MainActivity;
import com.example.checkListApp.R;
import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.input.CustomEditText;
import com.example.checkListApp.input.DetectKeyboardBack;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.example.checkListApp.ui.main.entry_management.EntryItemManager;
import com.example.checkListApp.ui.main.entry_management.Operator;
import com.example.checkListApp.ui.main.entry_management.button_panel.ButtonPanel;
import com.example.checkListApp.ui.main.entry_management.button_panel.ButtonPanelToggle;
import com.example.checkListApp.ui.main.entry_management.button_panel.LeafButton;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;
import com.example.checkListApp.ui.main.entry_management.list_component.CustomLayoutManager;
import com.example.checkListApp.ui.main.entry_management.list_component.ListItemClickListener;
import com.example.checkListApp.ui.main.entry_management.list_component.RecyclerAdapter;
import com.example.checkListApp.ui.main.entry_management.list_component.item_touch_helper.ItemTouchCallback;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainUIDynamics implements ListItemClickListener {

    /*
    responsible for various ui dynamics such as
    scrolling
    hiding elements
    setting listeners
    audio
    snackbar
    selection tracker
    button panel
    */


    MainFragment mainFragment;
    MainFragmentBinding binding;

    EntryItemManager entryItemManager;
    ButtonPanel buttonPanel;
    ButtonPanelToggle buttonPanelToggle;
    Operator operator;

    public Operator getOperator() { return operator; }

    public

    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    SelectionTracker<Long> selectionTracker;

    static CustomLayoutManager customLayoutManager;

    ArrayList<Entry> checkList = new ArrayList<>();

    MutableLiveData<Integer> selectedEntry = new MutableLiveData<>(1);

    View fragmentView;
    private final ItemTouchCallback callback;

    public View getFragmentView(){
        return fragmentView;
    }

    MainUIDynamics(MainFragment mainFragment){

        this.mainFragment = mainFragment;
        binding = mainFragment.binding;

        adapter = mainFragment.getAdapter();
        recyclerView = mainFragment.getRecyclerView();
        selectionTracker = mainFragment.getSelectionTracker();

        customLayoutManager = MainFragment.customLayoutManager;

        fragmentView = mainFragment.getFragmentView();

        operator = new Operator(mainFragment);

        entryItemManager = new EntryItemManager( mainFragment,operator);
        buttonPanel = new ButtonPanel(mainFragment.getContext(), binding);
        buttonPanelToggle  = buttonPanel.buttonPanelToggle;

        callback = new ItemTouchCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        entryItemManager.setButtonPanelToggle(buttonPanelToggle);


    }

    public void selectionTrackerObserver(){

        selectionTracker.addObserver( new SelectionTracker.SelectionObserver<Long>(){

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onItemStateChanged(@NonNull Long key, boolean selected) {
                super.onItemStateChanged(key, selected);

                if(adapter.toggleTracker && !mainFragment.isSorting) {
                    RecyclerAdapter.ViewHolder entryCurrent;
                    int index;

                    for (Entry entry : checkList) {
                        try {
                            if (!(entry instanceof Spacer)) {

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

                                    mainFragment.getListUtility().toggleSwitchOrdering.toggleNum(index);

                                    entryCurrent.isSelected.setValue( mainFragment.getListUtility().toggleSwitchOrdering.listToOrder.get(index).toggle);
                                    entryCurrent.orderInt.setValue( mainFragment.getListUtility().toggleSwitchOrdering.listToOrder.get(index).number);

                                    entryCurrent.selectOrder =  mainFragment.getListUtility().toggleSwitchOrdering.listToOrder.get(index).number;

                                    mainFragment.getListUtility().updateAllSelection(checkList);

                                    break;
                                } }

                        } catch (NullPointerException  | IndexOutOfBoundsException a) { }


                    }





                }

            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();

            }

        });

    }

    public Observer<Boolean> getOnTimerRunningObs(){


        return aBoolean -> {

            callback.setEnableSwipe(!aBoolean);

            if(!aBoolean){//timer not running

                MainActivity.tabLayout.setVisibility(View.VISIBLE);
                hideButtonPanel(false);
            }else{

                MainActivity.tabLayout.setVisibility(View.GONE);
                hideButtonPanel(true);
            }

        };
    }

    public void updateCheckList(ArrayList<Entry> newCheckList){
        checkList = newCheckList;
    }

    public void showUndoSnackBar(){

        Snackbar snackbar = Snackbar.make(mainFragment.binding.main, "item deleted",
                Snackbar.LENGTH_LONG).setAction(
                "UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       entryItemManager.undoLastDeletionSingle();
                    }
                });

        snackbar.show();

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

                Entry.globalCycle = value;
                JsonService.buildJson(mainFragment.getCheckList());

            }
        });

        binding.addDeleteBtn.setOnLongClickListener(view -> {

            entryItemManager.addDuplicate();
            return true;
        });

        buttonPanel.addButtonWithLeaf(
                binding.addDeleteBtn
                , view -> entryItemManager.add()
                , view -> entryItemManager.delete(),

                new LeafButton(binding, binding.addDeleteBtn)
                        //assign a onClickListener for leaf button
                        .assignListener(view -> {

                            hideTimeExecuteBtn(true);

                            //listener for the button panel
                            buttonPanelToggle.setSubmitBtnOnClickListener(
                                    view1 -> { //deletes selected Entries
                                        entryItemManager.deleteSelected(selectionTracker);
                                        buttonPanelToggle.toggleDisableToButton();
                                        adapter.trackerOn(false);
                                        hideTimeExecuteBtn(false);
                                    });

                            operator.updateIndexes();
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
                            hideTimeExecuteBtn(true);

                            buttonPanelToggle.setSubmitBtnOnClickListener(view1 -> {

                                mainFragment.isSorting = true;
                                entryItemManager.sortSelected(selectionTracker);
                                buttonPanelToggle.toggleDisableToButton();
                                adapter.notifyDataSetChanged();

                                selectionTracker.clearSelection();
                                mainFragment.getListUtility().reInitializeAllSelection(checkList);
                                adapter.trackerOn(false);
                                hideTimeExecuteBtn(false);
                            });

                            //thread may still be running!!!
                            selectionTracker.clearSelection();
                            mainFragment.getListUtility().reInitializeAllSelection(checkList);
                            operator.updateIndexes();

                            mainFragment.isSorting = false;

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

            } catch (NullPointerException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

//               if (operator.isMovingItem) {
////                   operator.moveItem(operator.movingItem);
//               }


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

    public void playAudio(int audio){
        mainFragment.selectedAudio[audio].start();
    }

    @Override
    public void clickPosition(RecyclerAdapter.ViewHolder viewHolder, View view, int position) {


        if (view.getId() == R.id.checkBtn) {

            viewHolder.getEntry().checked.postValue(!viewHolder.getEntry().checked.getValue());

        }

        if(!MainFragment.isTimerRunning()) {
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


            if(view.getId() == R.id.subListBtn){

                mainFragment.getSubListManager().showSubListSelection(fragmentView, position);

            }

        }

    }

    public static void scrollPosition(int position, int offset){
        customLayoutManager.scrollToPositionWithOffset(position,offset);
    }

    public static void scrollPosition(int position){
        customLayoutManager.scrollToPositionWithOffset(position,100);
    }

    public void hideButtonPanel(boolean hide){

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


}
