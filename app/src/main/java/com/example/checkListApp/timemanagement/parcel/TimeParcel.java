package com.example.checkListApp.timemanagement.parcel;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeParcel implements Parcelable{

     protected String timeStringVal;
     protected int timeNumberValue;
     protected int timeIndex;

     protected String retainedJsonData;


    static public TimeParcel getDefault() {
         return new TimeParcelBuilder().build();
     }

    public int getTimeIndex() {
        return timeIndex;
    }

    public String getRetainedJsonData() {
        return retainedJsonData;
    }



    public String getTimeStringVal() {
        return timeStringVal;
    }

    public int getTimeNumberValue() {
        return timeNumberValue;
    }

    protected TimeParcel(Parcel in) {
        timeStringVal = in.readString();
        timeNumberValue = in.readInt();
        timeIndex  = in.readInt();

        retainedJsonData = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeStringVal);
        dest.writeInt(timeNumberValue);
        dest.writeInt(timeIndex);

        dest.writeString(retainedJsonData);
    }

    protected TimeParcel() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimeParcel> CREATOR = new Creator<TimeParcel>() {
        @Override
        public TimeParcel createFromParcel(Parcel in) {
            return new TimeParcel(in);
        }

        @Override
        public TimeParcel[] newArray(int size) {
            return new TimeParcel[size];
        }
    };

}
