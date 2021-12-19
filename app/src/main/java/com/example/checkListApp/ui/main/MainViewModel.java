package com.example.checkListApp.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.checkListApp.database.EntryRepository;
import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {


    private final EntryRepository repository;
    private final LiveData<List<Entry>> allEntries;

    public EntryRepository getRepository() {
        return repository;
    }

    public MainViewModel (Application application) {
        super(application);
        repository = new EntryRepository(application,this);
        allEntries = repository.getAllEntries();

    }

    public LiveData<List<Entry>> getAllEntries(){
        return allEntries;
    }

    public void insertEntry(Entry n){
        repository.insertEntry(n);
    }

    public void deleteEntry(Entry name){
        repository.deleteEntry(name);
    }

    public void deleteAllEntries(ArrayList<Entry> list){ repository.deleteAllEntries(list);}

    public void swapEntryValues(Entry one, Entry two){ repository.swapEntryValues(one, two);}

    public void swapEntryIdValues(Entry one, Entry two){ repository.swapEntryIdValues(one,two);}

    public void loadEntry(Entry entry){ repository.loadEntry(entry);}

    public void updateEntry(Entry n){repository.updateEntry(n);}



}