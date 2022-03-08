package com.gettydone.app.fragments.file_management;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gettydone.app.MainActivity;
import com.gettydone.app.databinding.FragmentFileListBinding;


import com.gettydone.app.R;


import org.jetbrains.annotations.NotNull;


public class FileListFragment extends Fragment {


    FileManager fileManager;
    FileListAdapter fileListAdapter;

    FragmentFileListBinding binding;

     EditText editText;
     Button buttonSave;

     ViewGroup fragmentViewGroup;

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

        fragmentViewGroup = container;

        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fileManager = new FileManager(view.getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());

        fileListAdapter = new FileListAdapter(fileManager,this);

        fileListAdapter.setListFiles(fileManager.getListOfFiles());

        RecyclerView recyclerView = binding.fileListView;

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(fileListAdapter);

        editText = binding.editFileName;
        buttonSave = binding.saveAsBtn;




        FileListFragmentArgs args = FileListFragmentArgs.fromBundle(getArguments());

//        binding.goToMainList.setOnClickListener(view1 -> {
//
//            //action_fileListFragment_to_mainFragment
//
//            Navigation.findNavController(view).navigate(R.id.action_fileListFragment_to_mainFragment);
//
//
//        });


        binding.saveAsBtn.setOnClickListener(view1 -> {

        fileManager.saveFile(binding.editFileName.getText().toString(),".json",args.getJsonData());
        updateOnSave();

        });

//        binding.deleteFileBtn.setOnClickListener(view1 -> {
//            fileManager.deleteFile(fileListAdapter.getFileSelection());
//            updateOnDelete();
//
//        });


        binding.loadFileBtn.setOnClickListener(view1 -> {


            if (fileListAdapter.getFileSelection() != -1) {
                FileListFragmentDirections.ActionFileListFragmentToMainFragment action =
                        FileListFragmentDirections
                                .actionFileListFragmentToMainFragment
                                        (fileManager.loadFile(fileListAdapter.getFileSelection()));

                Log.d("checkListTest", fileManager.loadFile(fileListAdapter.getFileSelection()));

                Navigation.findNavController(view).navigate(action);


                MainActivity.isLoadingData = true;

//            MainActivity.visualSelect = true;
                MainActivity.tabLayout.getTabAt(0).select();

                //  (MainActivity.activityBinding.tabs.getTabAt(0));
            }

        });


    }

    public void showDeleteFileDialog(String fileName, int index){

        buildDeleteFileAdapter(buildDeleteFileDialog(),fileName,index);
    }

    public AlertDialog buildDeleteFileDialog(){


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog confirmDeleteFileDialog = dialogBuilder.create();

        confirmDeleteFileDialog.setView(LayoutInflater.from(getContext())
                .inflate(
                        R.layout.dialog_confirm_file_delete,
                        fragmentViewGroup,
                        false));

        confirmDeleteFileDialog.show();

        return confirmDeleteFileDialog;

    }

    public void buildDeleteFileAdapter(AlertDialog alertDialog, String filename, int index){

        TextView textView = alertDialog.findViewById(R.id.fileBeingDeletedText);

        textView.setText(filename);


        alertDialog.findViewById(R.id.confirm_file_delete_btn)
                .setOnClickListener(view -> {

                    fileManager.deleteFile(index);
                    updateOnDelete();
                    alertDialog.dismiss();

                });

        alertDialog.findViewById(R.id.cancel_file_delete_btn).setOnClickListener(
                view -> { alertDialog.dismiss();
                });


    }


     void updateEditText(String fileNameTxt){

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


//
//    public static void transitionFromFileToMain(Activity activity){
//        Navigation.findNavController(activity,R.id.entryListFragment).navigate(R.id.action_fileListFragment_to_mainFragment);
//    }
//
//    public static void transitionFromFileToProgress(Activity activity){
//        Navigation.findNavController(activity,R.id.entryListFragment).navigate(R.id.action_fileListFragment_to_progressFragment);
//    }
//
//
//    public static void transitionFromFileToDonation(Activity activity){
//        Navigation.findNavController(activity,R.id.entryListFragment).navigate( R.id.action_fileListFragment_to_donationFragment);
//    }
//
//    public static void transitionFromFileToSettings(Activity activity){
//        Navigation.findNavController(activity,R.id.entryListFragment).navigate( R.id.action_fileListFragment_to_settingsFragment);
//    }
