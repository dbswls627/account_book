package com.team_3.accountbook;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarChartFragment extends Fragment {
    AppDatabase db;
    BarChart barChart;
    List<BarEntry> barEntries = new ArrayList<>();
    String YYYY;
    String halfYear;
    ArrayList<Integer> amountList = new ArrayList<>(); // ArrayList 선언
    Context context;
    public BarChartFragment(String YYYY) {
        // Required empty public constructor
        this.YYYY = YYYY;
    }
    public void setDate(String date,String halfYear){
        YYYY = date;
        this.halfYear = halfYear;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        db = AppDatabase.getInstance(container.getContext());

        barChart = view.findViewById(R.id.barchart);
        barChart.getDescription().setEnabled(false);    //오른쪽에 있는 라벨 제거
        barChart.setTouchEnabled(true);
        barChart.setScaleEnabled(false);    //확대하지 못하게 하기
        barChart.setDrawValueAboveBar(true);
        barChart.setDrawGridBackground(true); // 내부 회색
        barChart.setExtraOffsets(15, 15, 15, 15);//마진
        Legend l = barChart.getLegend();
        l.setEnabled(false);       //그래프 목록 표시 비활성화

        setChart(YYYY,halfYear);

       /* barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d("TEST", String.valueOf(barEntries.get((int) h.getX())));
                Log.d("TEST", String.valueOf(e));
                Log.d("TEST",h+"");
                Log.d("TEST",h.getY()+"");

            }

            @Override
            public void onNothingSelected() {

            }
        });*/

        return view;



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setChart(String YYYY,String halfYear) {
        int mon = 1;
        if (halfYear.equals(" 상반기")){ mon = 1;}
        else if (halfYear.equals(" 하반기")){ mon = 7;}
        barEntries.clear();
        for (int i = mon; i <= mon + 5; i++){
            String s = String.valueOf(i);
            if (s.length()==1){s = "0"+ s;} //1월이면->01월 이 되도록

            try {
                barEntries.add(new BarEntry(i-1,db.dao().getAmountOfMonth(YYYY+" "+s+"월","expense")));
            }catch (Exception e){
                barEntries.add(new BarEntry(i-1,0));        //해당 월에 쓴돈이 없어 null 값 이면 0원
            }
        }


        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);   //비율맞추기 1
        /** X축 글씨*/
        List<String> xAxisValues = new ArrayList<>(Arrays.asList("1월", "2월", "3월", "4월", "5월", "6월","7월", "8월", "9월", "10월", "11월", "12월"));
        barChart.getAxisLeft().setEnabled(false);       //y축 왼쪽 안뜨게

        XAxis xAxis = barChart.getXAxis();              //x축
        YAxis yAxis = barChart.getAxisRight();          //y축 오른쪽



        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
        xAxis.setPosition (XAxis.XAxisPosition.BOTTOM); //아래에만 뜨게하기
        xAxis.setLabelCount(11);
        xAxis.setGranularity(1f);  //글씨 간격
        xAxis.setDrawAxisLine(true); // 축 그리기 설정
        xAxis.setDrawGridLines(false); //격자 라인 활용
        xAxis.setDrawLimitLinesBehindData(true);


        yAxis.setGranularity(10000);   //y축 간격
        yAxis.setDrawGridLines(true); //격자 라인 활용
        yAxis.setDrawAxisLine(true); // 축 그리기 설정
        yAxis.setAxisMinimum(0f);   //비율맞추기 2

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f); //막대 너비 설정하기

        barChart.setData(barData);
        barChart.notifyDataSetChanged();

        barChart.invalidate();  //다시그리기
    }



}