package com.example.checkListApp.ui.main.entry_management.ListComponent;

import android.view.View;

import androidx.lifecycle.LifecycleOwner;

public interface ListItemObserver<T> {

     void observe(LifecycleOwner owner, T value,RecyclerAdapter.ViewHolder viewHolder);

}
