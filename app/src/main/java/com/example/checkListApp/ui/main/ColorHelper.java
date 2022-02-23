package com.example.checkListApp.ui.main;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.example.checkListApp.R;

public class ColorHelper {


    Context context;

    public  int Entry_ItemView;
    public  int Entry_ItemView_Selected;

    public ColorHelper(Context context){
        this.context = context;
//        Entry_ItemView = ContextCompat.getColor( context, R.color.purple_200);
        Entry_ItemView = getThemeAccentColor(context);
//        Entry_ItemView_Selected = ContextCompat.getColor(context, R.color.red);
        Entry_ItemView_Selected = getThemeControlHighlightColor(context);

    }

    private int getThemeAccentColor(Context context) {
        int colorAttr;
        colorAttr = R.attr.colorAccent;
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);


        return outValue.data;


    }

    private int getThemeControlHighlightColor(Context context){

        int colorAttr;
        colorAttr = R.attr.colorControlHighlight;
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);


        return outValue.data;

    }



}
