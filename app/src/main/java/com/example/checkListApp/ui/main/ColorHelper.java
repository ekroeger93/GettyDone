package com.example.checkListApp.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
        Entry_ItemView = ContextCompat.getColor( context, R.color.purple_200);
        Entry_ItemView_Selected = ContextCompat.getColor(context, R.color.red);

    }

    private static int getThemePrimaryColor(Context context) {
        int colorAttr;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimary;
//        } else {
//            //Get colorAccent defined for AppCompat
//            colorAttr = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
//        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);


        return outValue.data;


    }



}
