package com.gettydone.app.database;

import androidx.lifecycle.MutableLiveData;
import androidx.room.TypeConverter;

public class TypeConvertMutableLiveInteger {

    @TypeConverter
    public static MutableLiveData<Integer> integerMutableLiveData(Integer value) {
        return value == null ? new MutableLiveData<Integer>(-1) : new MutableLiveData<Integer>(value);
    }

    @TypeConverter
    public static Integer integer(MutableLiveData<Integer> liveData) {
        return liveData == null ? -1 : liveData.getValue();
    }

}
