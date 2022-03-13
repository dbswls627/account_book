package com.team_3.accountbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    BottomNavigationView bottom_menu;
    ImageView preButton,nextButton;
    private TextView monthYearText,sortName;
    LinearLayout listLayout,chartLayout,noData;
    PieChart pieChart;
    RecyclerView rv;
    AppDatabase db;
    LocalDate selectedDate;
    List<PieEntry> pieEntries;
    List<graphDate> graphDateList;
    Context context;
    @Override
    protected void onStart() {
        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.graph);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        db=AppDatabase.getInstance(this);
        pieChart = findViewById(R.id.pieChart);
        monthYearText = findViewById(R.id.monthYearTV);
        preButton = findViewById(R.id.preButton);
        nextButton = findViewById(R.id.nextButton);
        rv = findViewById(R.id.rv);
        sortName = findViewById(R.id.sortName);
        listLayout = findViewById(R.id.listLayout);
        chartLayout = findViewById(R.id.chartLayout);
        noData = findViewById(R.id.layout_noData);

        selectedDate = LocalDate.now();      // LocalDate: 지정된 날짜로 구성된 년-월 날짜.(시간 x) / 형식: YYYY-MM-DD

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
        pieChart.setOnChartValueSelectedListener(this);

        monthYearText.setText(monthYearFromDate(selectedDate));
        setChart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {
            Intent intent;
            switch (menuItem.getItemId()) {
                case R.id.home:
                    intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.graph:
                    intent = new Intent(this, GraphActivity.class);
                    startActivity(intent);
                    break;
                case R.id.assets:
                    intent = new Intent(this, AssetsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting:
                    intent = new Intent(this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });

        preButton.setOnClickListener(view -> {
            selectedDate = selectedDate.minusMonths(1);
            setChart();
        });

        nextButton.setOnClickListener(view -> {
            selectedDate = selectedDate.plusMonths(1);
            setChart();
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChart() {
        if (db.dao().getMDate(monthYearFromDate(selectedDate),"expense").isEmpty()){
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
        monthYearText.setText(monthYearFromDate(selectedDate));
        setList((ArrayList<Cost>) db.dao().getMDate(monthYearFromDate(selectedDate),"expense"));
        pieEntries = new ArrayList<>();
        graphDateList = db.dao().getGraphDate(monthYearFromDate(selectedDate),"expense"); //해당월 데이터 가져옴
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
    private String monthYearFromDate(LocalDate date) {      // LocalDate 형식(YYYY-MM-DD)의 데이터를 '----년 --월' 형식으로 변환하는 함수
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY년 MM월");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onValueSelected(Entry e, Highlight h) { // 그래프 클릭
        sortName.setText(graphDateList.get((int)h.getX()).getSortName());
        setList((ArrayList<Cost>) db.dao().getMDate(monthYearFromDate(selectedDate),graphDateList.get((int)h.getX()).getSortName(),"expense"));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onNothingSelected() {   //클릭해서 아무것도 선택 안되어 있는 상태
        sortName.setText("전체");
        setList((ArrayList<Cost>) db.dao().getMDate(monthYearFromDate(selectedDate),"expense"));

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
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}