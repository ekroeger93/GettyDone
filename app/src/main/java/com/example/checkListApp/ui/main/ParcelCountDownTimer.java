package com.example.checkListApp.ui.main;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class ParcelCountDownTimer<Entry> implements Parcelable {

    ArrayList<Entry> checkList;

    protected ParcelCountDownTimer(Parcel in) {

        checkList = (ArrayList<Entry>) in.createTypedArrayList(CREATOR);

    }

    protected ParcelCountDownTimer(){

    }

    public static final Creator<ParcelCountDownTimer> CREATOR = new Creator<ParcelCountDownTimer>() {
        @Override
        public ParcelCountDownTimer createFromParcel(Parcel in) {
            return new ParcelCountDownTimer(in);
        }

        @Override
        public ParcelCountDownTimer[] newArray(int size) {
            return new ParcelCountDownTimer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeList(checkList);

    }
}
