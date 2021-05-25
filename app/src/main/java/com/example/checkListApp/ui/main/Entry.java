package com.example.checkListApp.ui.main;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.checkListApp.database.EntryRepository;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "Entries")
public class Entry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entryID")
    private int entryID = 0;
    @ColumnInfo(name = "textEntry")
    public MutableLiveData<String> textEntry = new MutableLiveData<String>("o") ;
    @ColumnInfo(name = "isChecked")
    public MutableLiveData<Boolean> checked = new MutableLiveData<>(false);

    @Ignore
    private MutableLiveData<RecyclerAdapter.ViewHolder> mViewHolder = new MutableLiveData<>();
    @Ignore
     private RecyclerAdapter.ViewHolder viewHolder;
    @Ignore
    public boolean swappable = true;

    public Entry(){
    }

    public Entry(String text,boolean check){
        textEntry.setValue(text);
        checked.setValue(check);
    }

    public Entry(Entry entry) {
        textEntry.postValue(entry.textEntry.getValue());
        checked.postValue(entry.checked.getValue());
    }

    public int getEntryID() {
        return this.entryID;
    }

    public void setEntryID(int ID) {
        this.entryID = ID;
    }

    public void setViewHolder(RecyclerAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
      //  mViewHolder.setValue(viewHolder);
    }

    public RecyclerAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }




    public void setObservers(EntryRepository repository, ArrayList<Entry> mList){

        TextView textView = viewHolder.textView;
        TableRow tEntryViewRow = viewHolder.tEntryViewRow;
        Entry entry  = this;


        Observer<String> observer = new Observer() {
            @Override
            public void onChanged(Object o) {


                textView.setText(o.toString());


                //getMainViewModel().updateEntry(getEntry());
                repository.updateEntry(entry);
                MainFragment.buildJson((ArrayList<Entry>) mList);



            }
        };

        Observer<Boolean> checkObs = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                textView.setBackgroundColor( checked.getValue() ?
                        Color.GRAY:
                        Color.parseColor("#FFF7B4")
                );

                tEntryViewRow.setBackgroundColor( checked.getValue() ?
                        Color.GRAY:
                        Color.parseColor("#95FF8D")
                );


                //  getMainViewModel().updateEntry(getEntry());
                repository.updateEntry(entry);
                MainFragment.buildJson((ArrayList<Entry>) mList);


            }
        };



//        getEntry().textEntry.observe(owner,observer);
//        getEntry().checked.observe(owner,checkObs);


    }




}
