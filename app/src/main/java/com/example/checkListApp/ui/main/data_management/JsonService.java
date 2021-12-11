package com.example.checkListApp.ui.main.data_management;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public final class JsonService {

    private static String jsonCheckArrayList;

    public static String getJsonCheckArrayList(){
        return jsonCheckArrayList;
    }

    public static void buildJson(ArrayList<Entry> checkList){

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Entry.class, new SerializeEntryToJson())
                .registerTypeAdapter(Entry.class, new DeserializeJsonToEntry())
                .create();


        StringBuilder jsonCheckList = new StringBuilder();


        jsonCheckList.append("[");
        for(Entry entry : checkList){

//            Log.d("checkListTest",""+jsonCheckList);
//            Log.d("checkListTest",""+entry);

            if (entry.getClass() == Entry.class)
                jsonCheckList.append(gson.toJson(entry)).append(",");
        }

        jsonCheckList.deleteCharAt(jsonCheckList.length()-1);
        jsonCheckList.append("]");

        Log.d("checkListTest","gen: "+jsonCheckList);


        jsonCheckArrayList = String.valueOf(jsonCheckList);

    }

    public static ArrayList<Entry> getJsonGeneratedArray(String json){


        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Entry.class, new SerializeEntryToJson())
                .registerTypeAdapter(Entry.class, new DeserializeJsonToEntry())
                .create();

        ArrayList<Entry> entryArrayList;
        Type userListType = new TypeToken<ArrayList<Entry>>(){}.getType();

        entryArrayList = gson.fromJson(String.valueOf(json), userListType);

        return entryArrayList;
    }



    static class SerializeEntryToJson implements JsonSerializer<Entry> {


        @Override
        public JsonElement serialize(Entry src, Type typeOfSrc, JsonSerializationContext context) {


    JsonObject jsonObject = new JsonObject();

    //TODO NULL FAULT
            if(src.textEntry != null) {
    jsonObject.addProperty("textEntry", src.textEntry.getValue());
    jsonObject.addProperty("isChecked", src.checked.getValue());
    jsonObject.addProperty("timerLabel", src.countDownTimer.getValue());

}
                return jsonObject;


        }


    }

    static class DeserializeJsonToEntry implements JsonDeserializer<Entry> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();

            String textEntry;
            String timeText;

            textEntry = jsonObject.get("textEntry").toString();
            boolean isChecked = jsonObject.get("isChecked").getAsBoolean();
            timeText = jsonObject.get("timerLabel").toString();
            Log.d("checkListTime",">>> "+timeText);

            //TODO:fix this
            timeText = timeText.replaceAll("\"","").trim();

            return new Entry(textEntry,isChecked,timeText);


        }

    }


}
