package com.example.checkListApp.ui.main.entry_management.ButtonPanel;

import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import com.example.checkListApp.databinding.MainFragmentBinding;

@FunctionalInterface
public interface CreateButtonManifest {

    void create(AppCompatButton button,
                MainFragmentBinding binding,
                View.OnClickListener onClickListener);

}
