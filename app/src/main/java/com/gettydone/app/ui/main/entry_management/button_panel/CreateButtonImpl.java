package com.gettydone.app.ui.main.entry_management.button_panel;

import android.view.View;

import com.gettydone.app.databinding.MainFragmentBinding;

public interface CreateButtonImpl {



     ButtonPanelToggle.ToggleButton setBinding(MainFragmentBinding binding);

     ButtonPanelToggle.ToggleButton setListener(View.OnClickListener clickListener);

     void commitCreate( CreateButtonManifest createButtonManifest);

}
