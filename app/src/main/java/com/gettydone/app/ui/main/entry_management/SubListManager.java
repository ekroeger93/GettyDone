package com.gettydone.app.ui.main.entry_management;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gettydone.app.R;
import com.gettydone.app.fragments.file_management.FileManager;
import com.gettydone.app.ui.main.MainFragment;
import com.gettydone.app.ui.main.data_management.AuxiliaryData;
import com.gettydone.app.ui.main.entry_management.entries.Entry;

import java.util.ArrayList;

public class SubListManager {



    private final MainFragment mainFragment;
    private final FileManager fileManager;
    private final MainListTimeProcessHandler mainListTimeProcessHandler;
    private final Context context;


    public SubListManager(MainFragment mainFragment){
        this.mainFragment = mainFragment;

        fileManager = mainFragment.getFileManager();
        mainListTimeProcessHandler = mainFragment.getMainListTimeProcessHandler();
        context = mainFragment.getContext();
    }



    public void showSubListSelection(View view, int index){

        buildSubListAdapter(buildSubListDialog(view) , index);

    }

    public AlertDialog buildSubListDialog(View view){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        AlertDialog subListDialog = dialogBuilder.create();


        subListDialog.setMessage("load a sub list");
        subListDialog.setView(LayoutInflater.from(context)
                .inflate(
                        R.layout.dialog_sublist_selection,
                        (ViewGroup) view,
                        false));

        subListDialog.show();

        return subListDialog;

    }

    public void buildSubListAdapter(AlertDialog alertDialog, int index){

        ArrayList<Entry> checkList = mainFragment.getCheckList();

        RecyclerView subListRecyclerView = alertDialog.findViewById(R.id.subListSelection);
        SubListFileRecyclerAdapter subListAdapter = new SubListFileRecyclerAdapter(fileManager.getListOfFiles());

        subListRecyclerView.setAdapter(subListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(alertDialog.getContext());

        subListRecyclerView.setLayoutManager(linearLayoutManager);
        subListRecyclerView.setHasFixedSize(true);

        Button loadSub = alertDialog.findViewById(R.id.loadSubListFileBtn);

        Button unLoadSub = alertDialog.findViewById(R.id.unsetSubListBtn);

        loadSub.setOnClickListener(view -> {
            setSubList(index, subListAdapter.getFileSelection());
            alertDialog.dismiss();

            checkList.get(index).subListName.setValue(
                    fileManager.getFileName(
                            subListAdapter.getFileSelection()));

            mainFragment.getmViewModel().updateEntry( checkList.get(index));

        });

        unLoadSub.setOnClickListener(view -> {
            checkList.get(index).unSetSubList();

            checkList.get(index).subListName.postValue("");

            checkList.get(index).getViewHolder()
                    .subTextView.setText(
                    R.string.entrySubTextNoLoaded);

            alertDialog.dismiss();
        });

    }

    public void setSubList(int checkListIndex , int fileListIndex){

        ArrayList<Entry> checkList = mainFragment.getCheckList();

        Entry entry = checkList.get(checkListIndex);

        ArrayList<Entry> subList = AuxiliaryData.loadFile(
                fileManager.loadFile(fileListIndex));

        entry.subListJson.setValue(fileManager.loadFile(fileListIndex));

        Log.d("subListingTest",""+fileManager.loadFile(fileListIndex));

        checkList.get(checkListIndex).isSubEntry = true;

        for(Entry n: subList) {
            n.isSubEntry = true;
            n.setViewHolder(entry.getViewHolder());
        }

        entry.setSubCheckList(subList);

        mainListTimeProcessHandler.subAccumulation(checkList);

    }

    public void setSubList(int checkListIndex, String jsonData){

        ArrayList<Entry> checkList = mainFragment.getCheckList();

        Entry entry = checkList.get(checkListIndex);

        ArrayList<Entry> subList = AuxiliaryData.loadFile(jsonData);

        checkList.get(checkListIndex).subListJson.setValue(jsonData);

        if(subList!=null) {

            for (Entry n : subList) {
                n.isSubEntry = true;
                n.setViewHolder(entry.getViewHolder());
            }

            checkList.get(checkListIndex).isSubEntry = true;

            checkList.get(checkListIndex).setSubCheckList(subList);

            mainListTimeProcessHandler.subAccumulation(checkList);
        }


        mainFragment.getmViewModel().updateEntry(entry);



    }

    //because its damn hard to maintain integrity while moving items
    public void sanityCheckSubList(){

        ArrayList<Entry> checkList = mainFragment.getCheckList();


        for(Entry entry : checkList){

            entry.subListJson.setValue(fileManager.loadFile(entry.subListName.getValue()));

            //if a name is there but no sublist what the hell?!
            if( entry.subListJson.getValue() != null || !entry.subListJson.getValue().isEmpty())
                setSubList(checkList.indexOf(entry),entry.subListJson.getValue());

            //if there is no name why the hell is there a sublist?
            if( entry.subListName.getValue() == null || entry.subListName.getValue().isEmpty() ){
                entry.unSetSubList();
            }



        }

    }

    public void loadSubLists(){

        ArrayList<Entry> checkList = mainFragment.getCheckList();

        for(Entry entry: checkList){

            if(!entry.subListJson.getValue().isEmpty() && !entry.subListJson.getValue().equals("\"\"")){

                Log.d("jsonTest", ":"+entry.subListJson.getValue());

                ArrayList<Entry> subList = AuxiliaryData.loadFile(entry.subListJson.getValue());

                entry.isSubEntry = true;

                for(Entry n: subList) {
                    n.isSubEntry = true;
                    n.setViewHolder(entry.getViewHolder());
                }

                entry.setSubCheckList(subList);
                mainListTimeProcessHandler.subAccumulation(checkList);

            }else{
                entry.unSetSubList();
            }


        }




    }

}
