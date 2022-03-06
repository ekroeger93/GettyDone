package com.gettydone.app.ui.main.entry_management;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gettydone.app.R;
import com.gettydone.app.ui.main.ColorHelper;

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

        textView.setText(listOfFiles[position].getName().replace(".json",""));

        highlightSelected(holder);

    }


    private void highlightSelected(RecyclerView.ViewHolder holder){

        if ( fileSelection == holder.getBindingAdapterPosition()) {
            holder.itemView.setBackgroundColor(new ColorHelper(holder.itemView.getContext()).Entry_ItemView_Selected);
        }else{
            holder.itemView.setBackgroundColor(new ColorHelper(holder.itemView.getContext()).Entry_ItemView);
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
