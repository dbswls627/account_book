package com.team_3.accountbook;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class PieChartFragment extends Fragment  {
    TextView sortName;
    LinearLayout listLayout,chartLayout,noData;
    PieChart pieChart;
    RecyclerView rv;
    AppDatabase db;
    List<PieEntry> pieEntries;
    List<graphDate> graphDateList;
    Context context;
    String YYYYMM;

    public PieChartFragment(String date) {
        YYYYMM = date;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        context = container.getContext();
        db=AppDatabase.getInstance(context);

        rv = (view).findViewById(R.id.rv);
        listLayout = (view).findViewById(R.id.listLayout);
        chartLayout = (view).findViewById(R.id.chartLayout);
        noData = (view).findViewById(R.id.layout_noData);
        pieChart = (view).findViewById(R.id.pieChart);
        sortName = (view).findViewById(R.id.sortName);
        pieChart.setEntryLabelColor(Color.WHITE);   //sortName 색갈
        pieChart.setRotationEnabled(false);//그래프 돌리면 돌아감 (true 일시)
        pieChart.getDescription().setEnabled(false);    //오른쪽에 있는 라벨 제거
        pieChart.setNoDataText("A");
        pieChart.setDrawHoleEnabled(false); //가운데 구멍 유무
        pieChart.setDrawCenterText(false); //가운데 글씨 유무
        Legend l = pieChart.getLegend();
        l.setEnabled(false);       //그래프 목록 표시 비활성화
        //pieChart.setCenterText("TEST");   //가운데 글씨
        //pieChart.setHoleColor(Color.WHITE);//가운데 구멍 색
        //pieChart.setExtraOffsets(5, 0, 5, 5);//??

        pieChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) requireContext());


        setChart();


        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setChart() {

        if (db.dao().getMDate(YYYYMM,"expense").isEmpty()){
            listLayout.setVisibility(View.GONE);
            chartLayout.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        else {
            listLayout.setVisibility(View.VISIBLE);
            chartLayout.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }

        sortName.setText("전체");
        setList((ArrayList<Cost>) db.dao().getMDate(YYYYMM,"expense"));
        pieEntries = new ArrayList<>();
        graphDateList = db.dao().getGraphDate(YYYYMM,"expense"); //해당월 데이터 가져옴
        graphDateList.forEach(i->{
            pieEntries.add(new PieEntry(i.getAmount(),i.getSortName()));
        });

        PieDataSet dataSet = new PieDataSet(pieEntries,"");
        dataSet.setSliceSpace(5f);      //그래프 사이 빈공간
        dataSet.setSelectionShift(8);  //그래프 클릭시 해당 파이 커지는 크기 설정

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);   //색 조합
        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.rgb(0,0,0));//amount 글자 색갈


        pieChart.setData(data);
        pieChart.invalidate();  //다시그리기
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setList(ArrayList<Cost> arrayList){
        ArrayList<String> dateArray = new ArrayList<>();        // 중복 제거한 날짜(yyyy년 MM월 dd일)만 담는 리스트 (adapter2로 넘겨주기 위함)
        for (Cost cost : arrayList) {
            if(!dateArray.contains(cost.getUseDate().substring(0, 14))) {
                dateArray.add(cost.getUseDate().substring(0, 14));
            }
        }
        rv.setAdapter(new adapter2(context,arrayList,dateArray));
        rv.setLayoutManager(new LinearLayoutManager(context));
    }
}