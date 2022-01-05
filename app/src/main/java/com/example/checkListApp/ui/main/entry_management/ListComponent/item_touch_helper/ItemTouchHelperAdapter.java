package com.example.checkListApp.ui.main.entry_management.ListComponent.item_touch_helper;

public interface ItemTouchHelperAdapter {


    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
