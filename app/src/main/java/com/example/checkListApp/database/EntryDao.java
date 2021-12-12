package com.example.checkListApp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.checkListApp.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;
import java.util.List;

@Dao

public interface EntryDao {

    @Insert
    void insertSpacer(Entry spacer);

    @Insert
    long insertEntry(Entry entry);

    @Delete
    void deleteEntry(Entry name);

//    @Query("DELETE FROM entries WHERE entryID = :name")
//    void deleteEntry(int name);

    @Query("SELECT * FROM entries")
    LiveData<List<Entry>> getAllEntries();

//    @Query("SELECT * FROM note_table ORDER BY priority DESC")
//    LiveData<List<Note>> getAllNotes();
//   @Query("INSERT INTO entries SELECT 'textEntry' AS COLUMN1 , 'isChecked' AS COLUMN2 ")

    @Update
    void updateEntry(Entry entry);

    @Query("DELETE FROM entries")
    void deleteAllEntries();


}
