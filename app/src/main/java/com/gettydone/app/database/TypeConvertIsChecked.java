package com.gettydone.app.database;

import androidx.lifecycle.MutableLiveData;
import androidx.room.TypeConverter;

public class TypeConvertIsChecked {

    @TypeConverter
    public static MutableLiveData<Boolean> booleanMutableLiveData(Boolean  value){
        return value == null ? null : new MutableLiveData<Boolean>(value);
    }

    @TypeConverter
    public static Boolean aBoolean(MutableLiveData<Boolean> liveData){
        return liveData == null ? null : liveData.getValue();
    }

}
