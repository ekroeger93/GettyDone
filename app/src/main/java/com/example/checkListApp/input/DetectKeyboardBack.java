package com.example.checkListApp.input;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.lifecycle.LifecycleObserver;


import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.Objects;

public class DetectKeyboardBack implements LifecycleObserver{

    private InputMethodManager imm;

   public DetectKeyboardBack(Context context, CustomEditText customEditText, TextView textView, Entry entry ){

       initialization(customEditText,textView);

       showSoftKeyboard(customEditText, context);

       setListeners(customEditText,textView,entry,context);

    }

    public void initialization(CustomEditText customEditText, TextView textView){

        customEditText.setVisibility(View.VISIBLE);

        customEditText.requestFocus();
        customEditText.performClick();

        setEditFocus(customEditText,true);

        textView.setVisibility(View.GONE);

        customEditText.getText().clear();
        customEditText.getText().append(textView.getText());
    }

    public void showSoftKeyboard(View view , Context context) {
        if (view.requestFocus()) {

            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            imm.isActive();
        }
    }

    public void cancelSoftKeyboard(CustomEditText customEditText, Context context){

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(customEditText.getWindowToken(),0);

        customEditText.clearFocus();


    }

    public void setEditFocus(CustomEditText customEditText, boolean flag){

        customEditText.setCursorVisible(flag);
       customEditText.setFocusable(flag);
       customEditText.setFocusableInTouchMode(flag);

    }

    public void setListeners(CustomEditText customEditText, TextView textView, Entry entry, Context context){

        customEditText.setOnEditTextImeBackListener((editText, text) -> {

            if(!customEditText.getText().toString().equals("")){
                entry.textEntry.postValue(Objects.requireNonNull(customEditText.getText()).toString());
            }
            //then calls the observer set in RecycleAdapter to set UI text

            textView.setVisibility(View.VISIBLE);
            customEditText.setVisibility(View.INVISIBLE);
        });

        customEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {


                if(!customEditText.isFocused()) {

                    if(!customEditText.getText().toString().equals("")){
                        entry.textEntry.postValue(Objects.requireNonNull(customEditText.getText()).toString());
                    }

                    setEditFocus(customEditText,customEditText.isFocused());
                    cancelSoftKeyboard(customEditText, context);

                    textView.setVisibility(View.VISIBLE);
                    customEditText.setVisibility(View.INVISIBLE);


                }
            }
        });

    }


}
