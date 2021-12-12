package com.example.checkListApp.database;

import androidx.lifecycle.MutableLiveData;
import androidx.room.TypeConverter;

public class TypeConvertMutableLiveInteger {

    @TypeConverter
    public static MutableLiveData<Integer> stringMutableLiveData(Integer value) {
        return value == null ? null : new MutableLiveData<Integer>(value);
    }

    @TypeConverter
    public static Integer string(MutableLiveData<Integer> liveData) {
        return liveData == null ? null : liveData.getValue();
    }

}
