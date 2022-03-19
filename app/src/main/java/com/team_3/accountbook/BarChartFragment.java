package com.team_3.accountbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarChartFragment extends Fragment {
    AppDatabase db;
    BarChart barChart;
    List<BarEntry> barEntries;

    ArrayList<Integer> amountList = new ArrayList<>(); // ArrayList 선언

    public BarChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        db = AppDatabase.getInstance(container.getContext());

        barChart = view.findViewById(R.id.barchart);
        barChart.getDescription().setEnabled(false);    //오른쪽에 있는 라벨 제거
        barChart.setTouchEnabled(false); //확대하지못하게 막음
        barChart.setDrawGridBackground(false); // 격자구조
        barChart.setExtraOffsets(15, 15, 15, 15);//마진
        Legend l = barChart.getLegend();
        l.setEnabled(false);       //그래프 목록 표시 비활성화

        setChart();

        return view;



    }

    public void setChart() {
        barEntries = new ArrayList<>();
        //db.dao().getAmountOfMonth();
        barEntries.add(new BarEntry(1f,10f));
        barEntries.add(new BarEntry(2f,20f));
        barEntries.add(new BarEntry(3,30f));
        barEntries.add(new BarEntry(4,40f));
        barEntries.add(new BarEntry(5,50f));
        barEntries.add(new BarEntry(6,60f));
        barEntries.add(new BarEntry(7,70f));
        barEntries.add(new BarEntry(8,80f));
        barEntries.add(new BarEntry(9,90f));
        barEntries.add(new BarEntry(10,100f));
        barEntries.add(new BarEntry(11,110f));
        barEntries.add(new BarEntry(12,120f));


        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        /** X축 글씨*/
        List<String> xAxisValues = new ArrayList<>(Arrays.asList("1월", "2월", "3월", "4월", "5월", "6월","7월", "8월", "9월", "10월", "11월", "12월"));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
        xAxis.setPosition (XAxis.XAxisPosition.BOTTOM); //아래에만 뜨게하기
        xAxis.setCenterAxisLabels (true);

        xAxis.setGranularity(.01f);  //글씨 간격
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(.2f); //막대 너비 설정하기
        barChart.setData(barData);
        barChart.notifyDataSetChanged();

        barChart.invalidate();  //다시그리기
    }



}