package com.example.checkListApp.input;

import android.view.GestureDetector;
import android.view.MotionEvent;


//https://stackoverflow.com/questions/45054908/how-to-add-a-gesture-detector-to-a-view-in-android
public class DoubleTapCustom  extends GestureDetector.SimpleOnGestureListener {
    public DoubleTapCustom() {
        super();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return super.onDoubleTapEvent(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return super.onSingleTapConfirmed(e);
    }
}
