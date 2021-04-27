package com.example.checkListApp.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.checkListApp.database.EntryRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel


    private EntryRepository repository;
    private LiveData<List<Entry>> allProducts;

    public EntryRepository getRepository() {
        return repository;
    }

    public MainViewModel (Application application) {
        super(application);
        repository = new EntryRepository(application,this);
        allProducts = repository.getAllEntries();

    }

    public LiveData<List<Entry>> getAllEntries(){
        return allProducts;
    }

    public void insertEntry(Entry n){
        repository.insertEntry(n);
    }

    public void deleteEntry(Entry name){
        repository.deleteEntry(name);
    }

    public void deleteAllEntries(ArrayList<Entry> list){ repository.deleteAllEntries(list);}

    public void loadEntry(Entry entry){ repository.loadEntry(entry);}

    public void updateEntry(Entry n){repository.updateEntry(n);}


}