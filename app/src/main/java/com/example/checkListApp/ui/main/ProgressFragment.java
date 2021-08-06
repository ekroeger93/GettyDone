package com.example.checkListApp.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkListApp.R;
import com.example.checkListApp.ui.main.EntryManagement.Record.ProgressProvider;
import com.example.checkListApp.ui.main.EntryManagement.Record.Record;
import com.example.checkListApp.ui.main.EntryManagement.Record.RecordHelper;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerImage;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Canvas canvas = new Canvas();

    TextView textView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProgressFragment() {
        // Required empty public constructor
    }


    public static void transitionFromProgressToMain(Activity activity){
        Navigation.findNavController(activity,R.id.fragment).navigate(R.id.action_progressFragment_to_mainFragment);
    }
    public static void transitionFromProgressToFile(Activity activity){

        Navigation.findNavController(activity,R.id.fragment)
                .navigate(ProgressFragmentDirections
                                .actionProgressFragmentToFileListFragment
                                        (MainFragment.getJsonCheckArrayList()));


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressFragment newInstance(String param1, String param2) {
        ProgressFragment fragment = new ProgressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarChart barChart = (BarChart) view.findViewById(R.id.chart);

        List<BarEntry> someEntries = new ArrayList<>();

        ArrayList<Record> listRecord = RecordHelper.getJsonRecordGeneratedArray(ProgressProvider.loadProgress(requireContext()));

        textView = view.findViewById(R.id.dateChartValue);

        RecordHelper.recordArrayList = listRecord;
        RecordHelper.buildRecordListJson();

        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(100);

        int index = 0;
        String prevCheckDate="";

        Calendar cal = Calendar.getInstance();

        for (Record record : listRecord) {

            StringBuilder date =  new StringBuilder(
                    record.getCurrentDate().replaceAll("[^0-9 .]", ""));


            String[] holdDate;
            holdDate = date.toString().split("\\.");

            int year = Integer.parseInt(holdDate[0]);
            int month = Integer.parseInt(holdDate[1]);
            int day = Integer.parseInt(holdDate[2]);


            date.append(" : "+  LocalDate.of(year,month,day).getDayOfWeek());

            if(!date.toString().equals(prevCheckDate) && !prevCheckDate.isEmpty()){
                index++;
            }



            BarEntry barEntry = new BarEntry(
                    index
                    , record.getNumberOfGoals(), date);

            prevCheckDate = date.toString();

            someEntries.add(barEntry);

        }


        barChart.setFitBars(true);

        BarDataSet dataSet = new BarDataSet(someEntries, "Label"); // add entries to dataset
            dataSet.setColor(Color.RED);
            dataSet.setValueTextColor(Color.BLACK);


            BarData barData = new BarData(dataSet);
            barChart.setData(barData);


            /*
            * update date via get scroll X to bar position, detect with gesture
            * */

         //   barChart.getScrollX();

            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {

                    textView.setText((CharSequence) e.getData().toString());
                }

                @Override
                public void onNothingSelected() {

                }
            });


            barChart.invalidate();


    }




}

