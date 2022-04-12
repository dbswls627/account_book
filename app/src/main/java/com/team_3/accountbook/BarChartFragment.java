package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BarChartFragment extends Fragment {
    AppDatabase db;
    BarChart barChart;
    List<BarEntry> barEntries = new ArrayList<>();
    TextView month;
    LinearLayout MMLayout,listLayout, chartLayout, noData;
    String YYYY;
    String MM;
    RecyclerView rv;
    String halfYear;
    ArrayList<String> xAxisValues;
    Context context;
    public BarChartFragment(String YYYY) {
        // Required empty public constructor
        this.YYYY = YYYY;


    }
    public void setDate(String date,String halfYear){
        YYYY = date;
        this.halfYear = halfYear;
    }


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        db = AppDatabase.getInstance(container.getContext());

        rv = (view).findViewById(R.id.rv);
        MMLayout = (view).findViewById(R.id.MMLayout);
        month = (view).findViewById(R.id.month);
        barChart = (view).findViewById(R.id.barchart);
        listLayout = (view).findViewById(R.id.listLayout);
        chartLayout = (view).findViewById(R.id.chartLayout);
        noData = (view).findViewById(R.id.layout_noData);

        barChart.getDescription().setEnabled(false);    //오른쪽에 있는 라벨 제거
        barChart.setTouchEnabled(true);
        barChart.setScaleEnabled(false);    //확대하지 못하게 하기
        barChart.setDrawValueAboveBar(true);
        barChart.setDrawGridBackground(true); // 내부 회색
        barChart.setExtraOffsets(15, 15, 15, 15);//마진
        Legend l = barChart.getLegend();
        l.setEnabled(false);       //그래프 목록 표시 비활성화

        MM = LocalDate.now().getMonthValue() +"월";


        setChart(YYYY,halfYear);

        /**클릭 이벤트*/
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                MMLayout.setVisibility(View.VISIBLE);
                MM = xAxisValues.get((int) h.getX());
                if (MM.length()==2){ MM = "0"+MM;}      //1월 -> 01월로 바꾸기
                month.setText(MM);
                setList((ArrayList<Cost>) db.dao().getMDate(YYYY+" "+MM, "expense"));
            }

            @Override
            public void onNothingSelected() {
                setList((new ArrayList<Cost>()));       //빈리스트
                MMLayoutInvisible();
            }
        });

        return view;



    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setChart(String YYYY,String halfYear) {
        AtomicBoolean barEntriesIsEmpty = new AtomicBoolean(true); //forEach 에서 쓰려면 이런 형태여야 한다네? 원랜 boolean barEntriesIsEmpty =true 란 의미
        if (MM.length()==2){ MM = "0"+MM;}      //1월 -> 01월로 바꾸기
        month.setText(MM);
        this.YYYY = YYYY;
        this.halfYear = halfYear;
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

        barEntries.forEach((item)->{
            if (item.getY() != 0){  //하나라도 값이 있으면..
                barEntriesIsEmpty.set(false);
            }
        });

        if (barEntriesIsEmpty.get()){//비어 있으면..
            listLayout.setVisibility(View.GONE);
            chartLayout.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else {
            listLayout.setVisibility(View.VISIBLE);
            chartLayout.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }



        setList((ArrayList<Cost>) db.dao().getMDate(YYYY+" "+MM, "expense"));

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);   //비율맞추기 1
        /** X축 글씨*/

        xAxisValues = new ArrayList<>(Arrays.asList("1월", "2월", "3월", "4월", "5월", "6월","7월", "8월", "9월", "10월", "11월", "12월"));
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
        barChart.highlightValues(null); //그래프 클릭 안된 상태로 바꾸기
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setList(ArrayList<Cost> arrayList) {
        ArrayList<String> dateArray = new ArrayList<>();        // 중복 제거한 날짜(yyyy년 MM월 dd일)만 담는 리스트 (adapter2로 넘겨주기 위함)
        for (Cost cost : arrayList) {
            if (!dateArray.contains(cost.getUseDate().substring(0, 14))) {
                dateArray.add(cost.getUseDate().substring(0, 14));
            }
        }
        rv.setAdapter(new adapter2(context, arrayList, dateArray));
        rv.setLayoutManager(new LinearLayoutManager(context));
        Log.d("TEST",MM);
    }
    void MMLayoutInvisible (){
        MMLayout.setVisibility(View.INVISIBLE);
    }

}