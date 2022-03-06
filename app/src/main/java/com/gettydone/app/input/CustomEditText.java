package com.gettydone.app.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//From here:
//https://stackoverflow.com/questions/3425932/detecting-when-user-has-dismissed-the-soft-keyboard

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText{

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(@NonNull Context context, EditTextImeBackListener mOnImeBack) {
        super(context);
        this.mOnImeBack = mOnImeBack;
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mOnImeBack = mOnImeBack;
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mOnImeBack = mOnImeBack;
    }

    private EditTextImeBackListener mOnImeBack;

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null)
                mOnImeBack.onImeBack(this, this.getText().toString());
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnEditTextImeBackListener(EditTextImeBackListener listener) {
        mOnImeBack = listener;
    }


    @Override
    public boolean hasOnLongClickListeners() {
//        return super.hasOnLongClickListeners();
    return false;
    }

}

interface EditTextImeBackListener {
    void onImeBack(CustomEditText ctrl, String text);
}
