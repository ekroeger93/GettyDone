package com.example.checkListApp.ui.main.entry_management;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkListApp.R;

import java.io.File;

public class SubListFileRecyclerAdapter extends RecyclerView.Adapter {


    private final File[] listOfFiles;
    private int fileSelection = 1;

    public int getFileSelection() {
        return fileSelection;
    }


    public SubListFileRecyclerAdapter(File[] listFiles){
        listOfFiles = listFiles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.sublist_file, (ViewGroup) parent.getRootView(), false);

        return new SubListFileRecyclerAdapter.SubFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        TextView textView = holder.itemView.findViewById(R.id.fileNameSubList);

        textView.setText(listOfFiles[position].getName());

        highlightSelected(holder);

    }


    private void highlightSelected(RecyclerView.ViewHolder holder){

        if ( fileSelection == holder.getBindingAdapterPosition()) {
            holder.itemView.setBackgroundColor(Color.YELLOW);
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE);
        }


    }

    @Override
    public int getItemCount() {
        return listOfFiles.length;
    }

    class SubFileViewHolder extends RecyclerView.ViewHolder {




        public SubFileViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fileSelection = (getBindingAdapterPosition());

                    notifyDataSetChanged();

                }
            });



        }




    }

}
