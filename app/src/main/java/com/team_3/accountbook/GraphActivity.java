package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {
    BottomNavigationView bottom_menu;
    ImageView preButton,nextButton;
    private TextView monthYearText, graphName;
    LinearLayout graph;
    PieChartFragment pieChartFragment;
    BarChartFragment barChartFragment;
    ImageView graphImage;
    AppDatabase db;
    LocalDate pieSelectedDate,barSelectDate;

    FragmentTransaction transaction;
    boolean graphCheck;
    private TextView GraphName;

    @Override
    protected void onStart() {
        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.graph);
    }
    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        db=AppDatabase.getInstance(this);
        preButton = findViewById(R.id.preButton);
        graph = findViewById(R.id.graph_button);
        monthYearText = findViewById(R.id.monthYearTV);
        nextButton = findViewById(R.id.nextButton);
        graphImage = findViewById(R.id.graph_image);
        GraphName = findViewById(R.id.graph_name);


        pieSelectedDate = LocalDate.now();      // LocalDate: 지정된 날짜로 구성된 년-월 날짜.(시간 x) / 형식: YYYY-MM-DD
        barSelectDate = LocalDate.now();

        graphCheck = true; //true = 원그래프    false = 막대그래프


        transaction = getSupportFragmentManager().beginTransaction();

        pieChartFragment = new PieChartFragment(monthYearFromDate(pieSelectedDate));
        barChartFragment = new BarChartFragment(barSelectDate);
        transaction.replace(R.id.container,pieChartFragment).commit();
        monthYearText.setText(monthYearFromDate(pieSelectedDate));

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
            if (graphCheck) {           //원 그래프 일때
                pieSelectedDate = pieSelectedDate.minusMonths(1);
                monthYearText.setText(monthYearFromDate(pieSelectedDate));
                pieChartFragment.setChart(monthYearFromDate(pieSelectedDate));
            }
            else {                      //막대 그래프 일때
                barSelectDate = barSelectDate.minusMonths(6);
                monthYearText.setText(monthYearFromDate(barSelectDate)+"~"+monthYearFromDate(barSelectDate.plusMonths(5)));
                barChartFragment.setChart(barSelectDate);
                barChartFragment.setList((new ArrayList<Cost>())) ; //빈리스트 띄우기
                barChartFragment.MMLayoutInvisible();
            }
        });

        nextButton.setOnClickListener(view -> {
            if (graphCheck) {           //원 그래프 일때
                pieSelectedDate = pieSelectedDate.plusMonths(1);
                monthYearText.setText(monthYearFromDate(pieSelectedDate));
                pieChartFragment.setChart(monthYearFromDate(pieSelectedDate));
            }
            else {                      //막대그래프 일때
                barSelectDate = barSelectDate.plusMonths(6);
                monthYearText.setText(monthYearFromDate(barSelectDate.minusMonths(5))+"~"+monthYearFromDate(barSelectDate));
                barChartFragment.setChart(barSelectDate);
                barChartFragment.setList((new ArrayList<Cost>())); //빈리스트 띄우기
                barChartFragment.MMLayoutInvisible();
            }
        });

        graph.setOnClickListener(view -> {
            transaction = getSupportFragmentManager().beginTransaction();
            if (graphCheck) {       //원 그래프 일때(막대 그래프로 가는 버튼)
                barChartFragment.setDate(barSelectDate);
                monthYearText.setText(monthYearFromDate(barSelectDate.minusMonths(5))+"~"+monthYearFromDate(barSelectDate));
                transaction.replace(R.id.container, barChartFragment).commit();
                graphImage.setImageResource(R.drawable.ic_baseline_pie_chart_24);
                graphCheck = false;
                GraphName.setText("원형 그래프");

            }
            else {                 //막대 그래프 일때(원 그래프로 가는 버튼)
                monthYearText.setText(monthYearFromDate(pieSelectedDate));
                transaction.replace(R.id.container, pieChartFragment).commit();
                graphImage.setImageResource(R.drawable.ic_baseline_bar_chart_24);
                graphCheck = true;
                GraphName.setText("막대 그래프");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {      // LocalDate 형식(YYYY-MM-DD)의 데이터를 '----년 --월' 형식으로 변환하는 함수
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY년 MM월");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return date.format(formatter);
    }

}