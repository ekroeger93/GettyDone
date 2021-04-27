package com.example.checkListApp.management;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.example.checkListApp.R;

import java.io.File;

public class FileListAdapter extends RecyclerView.Adapter {

    File[] listFiles;

    private  int fileSelection = 0;


    public int getFileSelection() {
        return fileSelection;
    }


    public void setListFiles(File[] listFiles) {
        this.listFiles = listFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_file, (ViewGroup) parent.getRootView(), false);

        return new MyViewHolder(view);


    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        TextView textView = holder.itemView.findViewById(R.id.fileName);

        textView.setText((CharSequence) listFiles[position].getName());

        if ( fileSelection == position){
            FileListFragment.updateEditText( listFiles[position].getName().replaceAll(".json",""));
        }
        //there no point in recycling why can't i just have a simple list?
        holder.setIsRecyclable(false);


        highlightSelected(holder);


    }


    private void highlightSelected(ViewHolder holder){

        if ( fileSelection == holder.getBindingAdapterPosition()) {
          holder.itemView.setBackgroundColor(Color.YELLOW);
        }else{
          holder.itemView.setBackgroundColor(Color.WHITE);
        }


    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return listFiles.length;
    }


    class MyViewHolder extends ViewHolder{

        public String fileName;

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }


        public MyViewHolder(@NonNull View itemView) {
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
