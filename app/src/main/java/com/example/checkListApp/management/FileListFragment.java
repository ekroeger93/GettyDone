package com.example.checkListApp.management;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.checkListApp.databinding.FragmentFileListBinding;


import com.example.checkListApp.R;
import org.jetbrains.annotations.NotNull;


public class FileListFragment extends Fragment {


    FileManager fileManager;
    FileListAdapter fileListAdapter;

    FragmentFileListBinding binding;

    static EditText editText;
    static Button buttonSave;


    public FileListFragment() {
        // Required empty public constructor

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_file_list, (ViewGroup) container, false);

      //  return inflater.inflate(R.layout.fragment_file_list, container, false);

        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        fileManager = new FileManager(view.getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());

        fileListAdapter = new FileListAdapter();
        fileListAdapter.setListFiles(fileManager.getListOfFiles());

        RecyclerView recyclerView = binding.fileListView;

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(fileListAdapter);

        editText = binding.editFileName;
        buttonSave = binding.saveAsBtn;




        FileListFragmentArgs args = FileListFragmentArgs.fromBundle(getArguments());

        Log.d("test","m: "+args.getJsonData());

        binding.goToMainList.setOnClickListener(view1 -> {

            //action_fileListFragment_to_mainFragment

            Navigation.findNavController(view).navigate(R.id.action_fileListFragment_to_mainFragment);


        });


        binding.saveAsBtn.setOnClickListener(view1 -> {

        fileManager.saveFile(binding.editFileName.getText().toString(),".json",args.getJsonData());
        updateOnSave();

        });

        binding.deleteFileBtn.setOnClickListener(view1 -> {
            fileManager.deleteFile(fileListAdapter.getFileSelection());
            updateOnDelete();

        });


        binding.loadFileBtn.setOnClickListener(view1 -> {

            FileListFragmentDirections.ActionFileListFragmentToMainFragment action  =
                    FileListFragmentDirections.actionFileListFragmentToMainFragment(fileManager.loadFile(fileListAdapter.getFileSelection()));

            Navigation.findNavController(view).navigate(action);

        });


    }

    static void updateEditText(String fileNameTxt){

        editText.setText(fileNameTxt);

    }

    void updateOnSave(){

        fileListAdapter.setListFiles(fileManager.getListOfFiles());//RIDICULOUS!!!

        fileListAdapter.notifyDataSetChanged();
        fileListAdapter.notifyItemInserted(fileManager.getListOfFiles().length-1);

    }

    void updateOnDelete(){

        fileListAdapter.setListFiles(fileManager.getListOfFiles());//RIDICULOUS!!!

        fileListAdapter.notifyDataSetChanged();
        fileListAdapter.notifyItemRemoved(fileListAdapter.getFileSelection());

    }
}