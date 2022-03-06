package com.gettydone.app.ui.main.entry_management.button_panel;

import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import com.gettydone.app.databinding.MainFragmentBinding;

@FunctionalInterface
public interface CreateButtonManifest {

    void create(AppCompatButton button,
                MainFragmentBinding binding,
                View.OnClickListener onClickListener);

}
