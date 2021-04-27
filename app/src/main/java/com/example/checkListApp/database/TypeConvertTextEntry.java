package com.example.checkListApp.database;

import androidx.lifecycle.MutableLiveData;
import androidx.room.TypeConverter;

public class TypeConvertTextEntry {

    @TypeConverter
    public static MutableLiveData<String> stringMutableLiveData(String value) {
        return value == null ? null : new MutableLiveData<String>(value);
    }

    @TypeConverter
    public static String string(MutableLiveData<String> liveData) {
        return liveData == null ? null : liveData.getValue();
    }

}
