package com.example.checkListApp.ui.main.entry_management.Record;

import android.content.Context;

import com.example.checkListApp.databinding.MainFragmentBinding;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.MainFragment;
import com.example.checkListApp.ui.main.entry_management.entries.Spacer;
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

public class RecordHelper {

    static int numOfEntries;
    static int numChecked;
    static RecordFinishButtonToggle recordFinishButtonToggle;

    public static ArrayList<Record>recordArrayList = new ArrayList<>();

    static String recordListJson= "";

    public static void createButton(Context context, MainFragmentBinding binding){
        recordFinishButtonToggle = new RecordFinishButtonToggle();
        RecordFinishButtonToggle.create(context,binding);
    }

    public void update(ArrayList<Entry> list){

        numOfEntries = list.size()-2;
        numChecked = 0;

        for(Entry entry : list){

            if(entry instanceof Spacer){} else {

                try {
                    if (entry.checked.getValue()) numChecked++;
                }catch (NullPointerException e){
                    continue;
                }

            }

        }

        if(numOfEntries > 0 && numChecked !=0){

            RecordFinishButtonToggle.toggleHideButton(numOfEntries != numChecked);

        }




    }

    public static void showRecords(){

//        Gson gson = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()
//                .registerTypeAdapter(Record.class, new SerializeRecordToJson())
//                .registerTypeAdapter(Record.class, new DeserializeJsonToRecord())
//                .create();
//
//
//        Log.d("mRecord",recordListJson);

//        for(Record record : recordArrayList) {
//            Log.d("mRecord",record.getNumberOfGoals()+" :: "+record.getCurrentDate());
//
////            String string = gson.toJson(record);
////            Type userListType = new TypeToken<Record>(){}.getType();
//
//        }
    }

    static public void buildRecordListJson(){

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Record.class, new SerializeRecordToJson())
                .registerTypeAdapter(Record.class, new DeserializeJsonToRecord())
                .create();

        StringBuilder jsonCheckList = new StringBuilder();

        jsonCheckList.append("[");
        for(Record record : recordArrayList){
                jsonCheckList.append(gson.toJson(record)).append(",");
        }

        jsonCheckList.deleteCharAt(jsonCheckList.length()-1);
        jsonCheckList.append("]");


        recordListJson = String.valueOf(jsonCheckList);

    }

    static public ArrayList<Record> getJsonRecordGeneratedArray(String json){

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Record.class, new SerializeRecordToJson())
                .registerTypeAdapter(Record.class, new DeserializeJsonToRecord())
                .create();

        ArrayList<Record> recordArrayList;
        Type userListType = new TypeToken<ArrayList<Record>>(){}.getType();

        recordArrayList = gson.fromJson(String.valueOf(json), userListType);

        return recordArrayList;
    }

    static class SerializeRecordToJson implements JsonSerializer<Record> {


        @Override
        public JsonElement serialize(Record src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("goalCount",src.getNumberOfGoals());
            jsonObject.addProperty("dateFinished",src.getCurrentDate());

            return jsonObject;
        }


    }

    static class DeserializeJsonToRecord implements JsonDeserializer<Record> {


        @Override
        public Record deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();

            String Date = jsonObject.get("dateFinished").toString();
            int goals = jsonObject.get("goalCount").getAsInt();

            return new Record(goals,Date);


        }

    }



}
