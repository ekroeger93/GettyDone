package com.gettydone.app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gettydone.app.ui.main.entry_management.entries.Entry;

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



    @Query("UPDATE entries SET orderIndex = :index WHERE entryID = :id")
    void updateEntryIndex(int id, int index);

    @Query( "SELECT * FROM entries ORDER BY orderIndex ASC")
    LiveData<List<Entry>>  sortIndexes();

    @Query("UPDATE entries " +
            "SET entryID = (CASE WHEN entryID = :idOne THEN :idTwo ELSE :idOne END)" +
            "WHERE entryID IN (:idOne,:idTwo)")
    void swapRowId(int idOne, int idTwo);


    @Query("UPDATE entries " +
            "SET textEntry = CASE WHEN textEntry = :textEntry THEN :textEntry2 END, " +
            "isChecked = CASE WHEN isChecked = :checked THEN :checked2  END," +
            "timerLabel = CASE WHEN timerLabel = :timerLabel THEN :timerLabel2 END " +
            "WHERE entryID = :idOne " +
            "AND entryID = :idTwo ")
    void swapEntries(
            boolean checked, boolean checked2,
            String textEntry, String textEntry2,
            String timerLabel, String timerLabel2,
            int idOne, int idTwo);


    @Query("DELETE FROM entries")
    void deleteAllEntries();


}
