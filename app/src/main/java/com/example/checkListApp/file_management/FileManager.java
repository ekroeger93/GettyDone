package com.example.checkListApp.file_management;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileManager {


    private final Context context;
    private File[] listOfFiles;


   public FileManager(Context context){

        this.context = context;
        listOfFiles = context.getFilesDir().listFiles();

    }


    public void saveFile(String fileName, String suffix, Object inputData){

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName + suffix, Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(inputData));
            outputStreamWriter.close();

        }catch (IOException e){

            e.printStackTrace();
        }

        update();

    }

    public void deleteFile(int selection){


         listOfFiles[selection].delete();

         update();

    }

    public void deleteAllFiles(){

        for (File file : listOfFiles){
            file.delete();
        }

        update();

    }

   public String loadFile(int selection){

        String output = "";
        String receiveString;
        StringBuilder stringBuilder;

        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;


        try {
            inputStream = context.openFileInput(listOfFiles[selection].getName());


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

    public String loadFile(String fileName){
       //to retain integrity use the file actual name

        String output = "";
        String receiveString;
        StringBuilder stringBuilder;

        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;


        try {
            inputStream = context.openFileInput(fileName);


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


    public File[] getListOfFiles() {
        return listOfFiles;
    }

    void update(){

        listOfFiles = context.getFilesDir().listFiles();

    }


}
