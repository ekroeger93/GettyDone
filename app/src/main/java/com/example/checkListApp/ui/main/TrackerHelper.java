package com.example.checkListApp.ui.main;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Predicate;

public class TrackerHelper {


    Details details;
    ItemDetailsLookup<Long> itemDetailsLookup;
    KeyProvider keyProvider;
    Predicate predicate;


    public TrackerHelper(RecyclerView recyclerView, RecyclerView.Adapter  adapter){

        details = new Details();
        itemDetailsLookup = new ItemDetailLookup(recyclerView);
        keyProvider = new KeyProvider(adapter);
        predicate = new Predicate();


    }



    static class Details extends ItemDetailsLookup.ItemDetails<Long> {
        long position;

        Details() {
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
            return true;
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

            View view = recyclerView.findChildViewUnder( event.getX(), event.getY());

            if(view != null){
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                if (viewHolder instanceof RecyclerAdapter.ViewHolder) {
                    if(((RecyclerAdapter.ViewHolder) viewHolder).getEntry() != null)
                        return ((RecyclerAdapter.ViewHolder) viewHolder).getItemDetails();
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
