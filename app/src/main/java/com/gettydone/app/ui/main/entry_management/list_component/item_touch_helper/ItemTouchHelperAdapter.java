package com.gettydone.app.ui.main.entry_management.list_component.item_touch_helper;

public interface ItemTouchHelperAdapter {


    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
