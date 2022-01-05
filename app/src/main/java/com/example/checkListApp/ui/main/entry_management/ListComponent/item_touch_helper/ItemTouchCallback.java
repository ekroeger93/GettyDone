package com.example.checkListApp.ui.main.entry_management.ListComponent.item_touch_helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchCallback extends androidx.recyclerview.widget.ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;



    public ItemTouchCallback(ItemTouchHelperAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }



    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = androidx.recyclerview.widget.ItemTouchHelper.UP | androidx.recyclerview.widget.ItemTouchHelper.DOWN;
        int swipeFlags = androidx.recyclerview.widget.ItemTouchHelper.START | androidx.recyclerview.widget.ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // Notify the adapter of the move
        mAdapter.onItemMove(source.getBindingAdapterPosition(), target.getBindingAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getBindingAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
