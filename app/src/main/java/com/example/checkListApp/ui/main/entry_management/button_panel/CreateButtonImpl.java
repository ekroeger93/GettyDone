package com.example.checkListApp.ui.main.entry_management.button_panel;

import android.view.View;

import com.example.checkListApp.databinding.MainFragmentBinding;

public interface CreateButtonImpl {



     ButtonPanelToggle.ToggleButton setBinding(MainFragmentBinding binding);

     ButtonPanelToggle.ToggleButton setListener(View.OnClickListener clickListener);

     void commitCreate( CreateButtonManifest createButtonManifest);

}
