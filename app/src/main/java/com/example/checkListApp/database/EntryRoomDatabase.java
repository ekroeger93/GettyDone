package com.example.checkListApp.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.checkListApp.ui.main.entry_management.entries.Entry;
import com.example.checkListApp.ui.main.MainViewModel;

@Database(entities = {Entry.class},version = 1,exportSchema = false)
@TypeConverters({TypeConvertTextEntry.class,TypeConvertIsChecked.class,TypeConvertMutableLiveInteger.class})
public abstract class EntryRoomDatabase extends RoomDatabase {


    public abstract EntryDao entryDao();


        private static EntryRoomDatabase INSTANCE;
        private static MainViewModel mainViewModel;

         static EntryRoomDatabase getDatabase(final Context context, MainViewModel main) {
            if (INSTANCE == null) {
                synchronized (EntryRoomDatabase.class) {
                    if (INSTANCE == null) {
                        mainViewModel = main;

                        INSTANCE =
//                                Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
//                                        EntryRoomDatabase.class
//                                        )
//                                       .addCallback(roomCallback)
//                                        .build();
                                Room.databaseBuilder(context.getApplicationContext(),
                                        EntryRoomDatabase.class,
                                        "entry_database").build();

                    }
                }
            }
            return INSTANCE;
        }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private EntryDao entryDao;
        private EntryRoomDatabase database;


        private PopulateDbAsyncTask(EntryRoomDatabase db) {
            entryDao = db.entryDao();
            database = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {

//            if(entryDao.getAllEntries().getValue() == null){
//
//                Entry entry = new Entry();
//                entryDao.insertSpacer(entry);
//
//            }


            return null;
        }
    }


       class initialize extends Callback{

        public  initialize() {
            super();

           entryDao().insertEntry(new Entry());


        }
    }

}


