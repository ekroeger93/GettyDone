package com.gettydone.app.ui.main.entry_management.record;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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

        int length = loadProgress(context).length();

        StringBuilder builder = new StringBuilder(input);

        if(length > 0) {
             builder = new StringBuilder(loadProgress(context))
                    .deleteCharAt(length - 1)
                    .append(",")
                    .append(new StringBuilder(input).deleteCharAt(0));

            Log.d("progressAppend", builder.toString());
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(RECORD_FILE, Context.MODE_PRIVATE));
            outputStreamWriter.append(builder.toString());
            outputStreamWriter.close();
        }catch (IOException e){
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


                //TODO:TRYING SOMETHING HERE
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
//                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                output = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e);

            File file1 = new File(RECORD_FILE);

            try (FileWriter fileWriter = new FileWriter(file1)) {

                fileWriter.write("");

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e);
        }

        return output;

    }

    public static void resetProgress(Context context) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(RECORD_FILE, Context.MODE_PRIVATE));
            outputStreamWriter.append("");
            outputStreamWriter.close();
        } catch (IOException e) {

        }
    }
}
