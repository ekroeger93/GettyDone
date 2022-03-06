package com.gettydone.app.ui.main.entry_management.list_component;

import androidx.lifecycle.LifecycleOwner;

public interface ListItemObserver<T> {

     void observe(LifecycleOwner owner, T value,RecyclerAdapter.ViewHolder viewHolder);

}
