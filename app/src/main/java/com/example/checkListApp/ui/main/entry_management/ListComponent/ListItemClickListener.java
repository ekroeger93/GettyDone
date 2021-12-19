package com.example.checkListApp.ui.main.entry_management.ListComponent;

import android.view.View;

public interface ListItemClickListener {
    void clickPosition(RecyclerAdapter.ViewHolder viewHolder,View view, int position);
}
