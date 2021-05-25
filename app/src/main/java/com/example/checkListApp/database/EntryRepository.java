package com.example.checkListApp.database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.checkListApp.ui.main.Entry;
import com.example.checkListApp.ui.main.MainViewModel;
import com.example.checkListApp.ui.main.Spacer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class EntryRepository {

private final EntryDao entryDao;

private final LiveData<List<Entry>> allEntries;

public EntryRepository(Application application, MainViewModel mainViewModel){

    EntryRoomDatabase db;
    db = EntryRoomDatabase.getDatabase(application, mainViewModel);
    entryDao = db.entryDao();

    allEntries = entryDao.getAllEntries();

}

public LiveData<List<Entry>> getAllEntries(){
    return allEntries;
}


public static class TaskDeleteAllEntries {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final EntryDao asyncDao;

    TaskDeleteAllEntries(EntryDao dao){
        asyncDao = dao;
    }

    public void executeAsync(){
        executor.execute(asyncDao::deleteAllEntries);
    }

}


public static class TaskInsertEntry {

        private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements

        private final EntryDao asyncDao;
        private final Entry entry;

        TaskInsertEntry(EntryDao dao, Entry e){
            asyncDao = dao;
            entry = e;
        }

        public  void executeAsync() {
            executor.execute(() -> {
                long id;
                id = asyncDao.insertEntry(entry);
                entry.setEntryID(((int) id));
            });
        }


    }



public static class TaskLoadEntry{

    private final ScheduledExecutorService loadExecutor = Executors.newSingleThreadScheduledExecutor();

    private final EntryDao asyncDao;
    private final Entry entry;

    TaskLoadEntry(EntryDao dao, Entry e){
        asyncDao = dao;
        entry = e;
    }

    public void executeLoadAsync() {
        loadExecutor.schedule(()->{
            long id;
            id = asyncDao.insertEntry(entry);
            entry.setEntryID(((int) id));
        },500, TimeUnit.MILLISECONDS);

    }

}

public static class TaskDeleteEntry{
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final EntryDao asyncDao;
    private final Entry entry;
   // private final int name;

    TaskDeleteEntry(EntryDao dao, Entry e){
        asyncDao = dao;
        entry = e;
    }

    public  void executeAsync() {
         executor.execute(() -> {
           asyncDao.deleteEntry(entry);
        });

    }
}

public static class TaskUpdateEntry {

        private final Executor executor = Executors.newSingleThreadExecutor();
        private final EntryDao asyncDao;
        private final Entry entry;

        TaskUpdateEntry(EntryDao dao, Entry e){
            asyncDao = dao;
            entry = e;
        }

        public  void executeAsync() {
            executor.execute(() -> {
                asyncDao.updateEntry(entry);
            });

        }
    }

public static class TaskInsertSpace{

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final EntryDao asyncDao;
    private final Spacer entry;

    TaskInsertSpace(EntryDao dao, Spacer e){
        asyncDao = dao;
        entry = e;
    }

    public void executeAsync() {
        executor.execute(() -> {
            long id;
            id = asyncDao.insertEntry(entry);
            entry.setEntryID(((int) id));
        });

    }
}

    public void insertEntry(Entry entry){
        new TaskInsertEntry(entryDao,entry).executeAsync();
    }

    public void loadEntry(Entry entry){
        new TaskLoadEntry(entryDao,entry).executeLoadAsync();
    }

    public void deleteEntry(Entry name){
        new TaskDeleteEntry(entryDao,name).executeAsync();
    }

    public void updateEntry(Entry entry){
        new TaskUpdateEntry(entryDao,entry).executeAsync();
    }

    public void deleteAllEntries(ArrayList<Entry> list){ new TaskDeleteAllEntries(entryDao).executeAsync();}

    public void insertSpace(Spacer spacer) { new TaskInsertSpace(entryDao,spacer).executeAsync();}





}


