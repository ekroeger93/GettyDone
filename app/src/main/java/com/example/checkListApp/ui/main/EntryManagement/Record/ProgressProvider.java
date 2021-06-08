package com.example.checkListApp.ui.main.EntryManagement.Record;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public final class ProgressProvider {

    File file;
    File[] listOfFiles;
    Context context;
    public static final String RECORD_FILE = "RECORD_FILE.json";

    ProgressProvider(Context context){

        this.context = context;

        listOfFiles = context.getFilesDir().listFiles();
    }

    public static void saveProgress(String input,Context context) {

//        File dir=new File(context.getFilesDir(), "RECORDS");
//        dir.mkdirs();
//        File record = new File(dir,RECORD_FILE);
//
//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter( new FileOutputStream(record,false));
//        outputStreamWriter.write(String.valueOf(input));
//        outputStreamWriter.close();

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(RECORD_FILE, Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(input));
            outputStreamWriter.close();
        }catch (IOException e){
            ;
        }

    }

    public static String loadProgress(Context context){

        String output = "";
        String receiveString;
        StringBuilder stringBuilder;

        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;

//        File file1 = new File("RECORDS/"+RECORD_FILE);

        try {
            inputStream = context.openFileInput(RECORD_FILE);

//            inputStream = new FileInputStream(file1);

            if ( inputStream != null ) {

                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                stringBuilder = new StringBuilder();


                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                output = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return output;

    }


}
