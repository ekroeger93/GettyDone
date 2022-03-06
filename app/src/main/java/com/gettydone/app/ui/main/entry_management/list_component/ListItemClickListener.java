package com.gettydone.app.ui.main.entry_management.list_component;

import android.view.View;

public interface ListItemClickListener {
    void clickPosition(RecyclerAdapter.ViewHolder viewHolder,View view, int position);
}
