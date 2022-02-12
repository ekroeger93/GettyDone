package com.example.checkListApp.ui.main.progress;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.checkListApp.R;
import com.example.checkListApp.ui.main.ColorHelper;
import com.example.checkListApp.ui.main.entry_management.record.ProgressProvider;
import com.example.checkListApp.ui.main.entry_management.record.Record;
import com.example.checkListApp.ui.main.entry_management.record.RecordHelper;
import com.example.checkListApp.ui.main.data_management.JsonService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    CalendarView calendarView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProgressFragment() {
        // Required empty public constructor
    }


    public static void transitionFromProgressToMain(Activity activity){
        Navigation.findNavController(activity,R.id.entryListFragment).navigate(R.id.action_progressFragment_to_mainFragment);
    }
    public static void transitionFromProgressToFile(Activity activity){

        Navigation.findNavController(activity,R.id.entryListFragment)
                .navigate(ProgressFragmentDirections
                                .actionProgressFragmentToFileListFragment
                                        (JsonService.getJsonCheckArrayList()));


    }

    public static void transitionFromProgressToDonation(Activity activity){
        Navigation.findNavController(activity,R.id.entryListFragment).navigate( R.id.action_progressFragment_to_donationFragment);
    }

    public static void transitionFromProgressToSetting(Activity activity){
        Navigation.findNavController(activity,R.id.entryListFragment).navigate(ProgressFragmentDirections.actionProgressFragmentToSettingsFragment());
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


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarChart barChart = (BarChart) view.findViewById(R.id.chart);
        calendarView = view.findViewById(R.id.calendar);


        barChart.setNoDataText("select a date");
        barChart.setNoDataTextColor(Color.BLACK);
        barChart.setNoDataTextTypeface(Typeface.MONOSPACE);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

//        String testDataProgress ="[" +
//                "{\"goalCount\":3,\"dateFinished\":2022.01.06,\"currentWeekOfYear\":2}," +
//                "{\"goalCount\":2,\"dateFinished\":2022.01.07,\"currentWeekOfYear\":2}," +
//                "{\"goalCount\":1,\"dateFinished\":2022.01.09,\"currentWeekOfYear\":3}," +
//                "{\"goalCount\":5,\"dateFinished\":2022.01.10,\"currentWeekOfYear\":3}," +
//                "{\"goalCount\":7,\"dateFinished\":2022.01.13,\"currentWeekOfYear\":3}" +
//                "]";


        RecordHelper.recordArrayList = RecordHelper
                .getJsonRecordGeneratedArray(
                     ProgressProvider.loadProgress(getContext())
//               testDataProgress
                );

        try {
            RecordHelper.buildRecordListJson();
        }catch (NullPointerException e){

        }
//        Log.d("progressTest", ""+RecordHelper.recordListJson);

        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(100);

        barChart.setFitBars(true);

//        Log.d("progressListTest",""+RecordHelper.recordListJson);




        calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> {

            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int week = LocalDate.of(year,month+1,day).get(weekFields.weekOfWeekBasedYear());

//            Log.d("progressTest"," "+year+" "+month+" "+day+" "+week);

            BarDataSet dataSet = new BarDataSet(generateEntriesByWeek(RecordHelper.recordArrayList,LocalDate.of(year,month+1,day)), "Label"); // add entries to dataset
            dataSet.setColor(new ColorHelper(getContext()).Entry_ItemView);
            dataSet.setValueTextColor(Color.BLACK);

            dataSet.setValueFormatter(barChart.getDefaultValueFormatter());
            dataSet.setLabel("entries submitted");

            BarData barData = new BarData(dataSet);
            barChart.setData(barData);
//            barChart.setXAxisRenderer();

            Description description = new Description();
            description.setText("");

            barChart.setDescription( description);

            barChart.invalidate();
        });



            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {

//                    textView.setText((CharSequence) e.getData().toString());
//                    barChart.setDescription();
                }

                @Override
                public void onNothingSelected() {



                }
            });




    }



    public List<BarEntry> generateEntriesByWeek(ArrayList<Record> listRecord, LocalDate localDate){

        List<BarEntry> chartEntries = new ArrayList<>();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        int week = localDate.get(weekFields.weekOfWeekBasedYear());


        int index = 0;
//        String prevCheckDate="";

        for(int i = 1; i <= 7; i ++) {
            chartEntries.add( new BarEntry(i,0,""));
        }

        try {
            for (Record record : listRecord) {

                Log.d("progressTest", "recWeek " + record.getCurrentWeekOfYear() + " # " + week);

                int dayOfTheWeek = record.getLocalDate().getDayOfWeek().getValue();
                dayOfTheWeek++;
                if (dayOfTheWeek > 7) dayOfTheWeek = 1; //who the hell made sunday 7!!!


                if (record.getCurrentWeekOfYear() != week) continue;
                ;


                Log.d("progressTest", "dd = " + dayOfTheWeek);

//                BarEntry barEntry = new BarEntry(index, record.getNumberOfGoals(), record.getCurrentDate());
                BarEntry barEntry = new BarEntry(dayOfTheWeek, record.getNumberOfGoals(), record.getCurrentDate());
                chartEntries.set(dayOfTheWeek - 1, barEntry);

//            index++;
            }
        }catch (NullPointerException e){

        }


        return chartEntries;


    }


    public List<BarEntry> generateEntries(ArrayList<Record> listRecord){

        List<BarEntry> chartEntries = new ArrayList<>();

        int index = 0;
        String prevCheckDate="";

        for (Record record : listRecord) {

            StringBuilder date =  new StringBuilder(
                    record.getCurrentDate().replaceAll("[^0-9 .]", ""));

            String[] holdDate;
            holdDate = date.toString().split("\\.");

            int year = Integer.parseInt(holdDate[0]);
            int month = Integer.parseInt(holdDate[1]);
            int day = Integer.parseInt(holdDate[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);

            calendar.getWeekYear();


            date.append(" : "+  LocalDate.of(year,month,day).getDayOfWeek());

            if(!date.toString().equals(prevCheckDate) && !prevCheckDate.isEmpty()){
                index++;
            }



            BarEntry barEntry = new BarEntry(index, record.getNumberOfGoals(), date);
            prevCheckDate = date.toString();

            chartEntries.add(barEntry);

        }

        return chartEntries;


    }




}

