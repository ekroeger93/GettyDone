package com.example.checkListApp.ui.main.entry_management.ListComponent;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import java.util.TreeMap;

public class TrackerHelper {


   public Details details;
   public ItemDetailsLookup<Long> itemDetailsLookup;
   public KeyProvider keyProvider;
   public Predicate predicate;

    static boolean isMotionActive = true;


    public TrackerHelper(RecyclerView recyclerView, RecyclerView.Adapter  adapter){

        details = new Details(this);
        itemDetailsLookup = new ItemDetailLookup(recyclerView);
        keyProvider = new KeyProvider(adapter);
        predicate = new Predicate();


    }

    public void setIsMotionActive(boolean isMotionActive) {
        this.isMotionActive = isMotionActive;
    }

    static class Details extends ItemDetailsLookup.ItemDetails<Long> {
        long position;
        TrackerHelper trackerHelper;

        Details(TrackerHelper trackerHelper) {
            this.trackerHelper = trackerHelper;
        }

        @Override
        public int getPosition() {
            return (int) position;
        }

        @Nullable
        @Override
        public Long getSelectionKey() {
            return position;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return isMotionActive;
        }
    }

    static class ItemDetailLookup extends ItemDetailsLookup<Long>{

        private RecyclerView recyclerView;

        ItemDetailLookup(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }


        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent event) {

     if(isMotionActive) {
         View view = recyclerView.findChildViewUnder(event.getX(), event.getY());

         if (view != null) {
             RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
             if (viewHolder instanceof RecyclerAdapter.ViewHolder) {
                 if (((RecyclerAdapter.ViewHolder) viewHolder).getEntry() != null)
                     return ((RecyclerAdapter.ViewHolder) viewHolder).getItemDetails();
             }
         }
     }
            return null;

        }
    }

    static class KeyProvider extends ItemKeyProvider<Long> {

        KeyProvider(RecyclerView.Adapter adapter) {
            super(ItemKeyProvider.SCOPE_MAPPED);
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return (long) position;
        }

        @Override
        public int getPosition(@NonNull Long key) {
            long value = key;
            return (int) value;
        }
    }

    static class Predicate extends SelectionTracker.SelectionPredicate<Long> {

        @Override
        public boolean canSetStateForKey(@NonNull Long key, boolean nextState) {
            return true;
        }

        @Override
        public boolean canSetStateAtPosition(int position, boolean nextState) {
            return true;
        }

        @Override
        public boolean canSelectMultiple() {
            return true;
        }
    }



}
