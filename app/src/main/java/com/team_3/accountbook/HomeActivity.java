package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements CalendarAdapter.OnItemClick {
    private long firstBackPressedTime = 0;          // 뒤로가기 체크시간
    private TextView monthYearText, date;
    private RecyclerView calendarRecyclerView, listRv;

    BottomNavigationView bottom_menu;
    LocalDate selectedDate;                         // 날짜 변수


    protected void onStart() {
        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.home);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottom_menu = findViewById(R.id.bottom_menu);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        listRv = findViewById(R.id.listRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        date = findViewById(R.id.date);

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
        
        selectedDate = LocalDate.now();      // LocalDate: 지정된 날짜로 구성된 년-월 날짜.(시간 x) / 형식: YYYY-MM-DD
        setMonthView();
    }


    public void onBackPressed() {
        // ↓ 기존 뒤로가기 버튼의 기능을 막기위해 주석처리
        //super.onBackPressed();

        // 뒤로가기를 누르고 2초가 지났다면~
        if(System.currentTimeMillis() > firstBackPressedTime + 2000) {
            firstBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        // 뒤로가기 버튼을 누른지 2초가 안지났다면~
        else if(System.currentTimeMillis() <= firstBackPressedTime + 2000) {
            finish();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date) {   // 월에 맞게 날짜들을 표시하는 함수.
        ArrayList<String> daysInMonthArray = new ArrayList<>();        // String 배열 daysInMonthArray 생성.
        YearMonth yearMonth = YearMonth.from(date);                    // .from(date): date 의 인스턴스를 가져온다.    *안드로이드 developers 설명: from(TemporalAccessor temporal) - Obtains an instance of YearMonth from a temporal object.

        int daysInMonth = yearMonth.lengthOfMonth();                   // 월의 길이.            .lengthOfMonth(): 연도를 고려하여 월의 길이를 반환함.

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);       // 달 첫째 날짜.         .withDayOfMonth(i): 달의 i일 날짜를 반환함.
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();        // 달 첫째 날의 요일.(0-일 1-월 ... 6-토)      .getDayOfWeek(): 요일 반환 메소드.

        for(int i = 1; i <= 42; i++) {                                 // 그리드뷰의 첫번째 칸부터 마지막 칸까지
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {        // i가 달의 첫째 날보다 작거나 마지막 날보다 크면~
                daysInMonthArray.add("");                              // ~빈칸으로 만듦.
            }
            else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;                                      // 완성한 달력 배열 반환.
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {      // LocalDate 형식(YYYY-MM-DD)의 데이터를 '----년 --월' 형식으로 변환하는 함수
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY년 MM월");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return date.format(formatter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view) {    // 이전 달 이동 버튼 클릭. setOnClickListener 로 바꿔도 됨.
        selectedDate = selectedDate.minusMonths(1); // 현재 달 - 1
        setMonthView();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view) {        // 다음 달 이동 버튼 클릭. setOnClickListener 로 바꿔도 됨.
        selectedDate = selectedDate.plusMonths(1);  // 현재 달 + 1
        setMonthView();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {// 달력 이동 버튼 클릭시, 해당 달의 달력을 그리는 함수

        monthYearText.setText(monthYearFromDate(selectedDate));                    // 현재 년/월을 setText
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);            // 해당 달의 달력 배열을 만들어 daysInMonth 에 저장
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, monthYearFromDate(selectedDate),this,this);   // 달력 배열을 가지는 Adapter 생성
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);   // 가로 7칸의 그리드뷰(퍼즐 형식)로 만드는 리사이클러뷰 레이아웃 매니저 layoutManager 생성
        calendarRecyclerView.setLayoutManager(layoutManager);                      // 레이아웃 매니저를 layoutManager 로 지정
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public void onClick(ArrayList<item> arrayList, String md) {    // CalendarAdapter 에서 요일을 클릭하면 호출돼어 실행되는 함수. 날짜에 맞는 활동정보 리스트를 받아서 출력함.
        listRv.setAdapter(new adapter(arrayList));
        listRv.setLayoutManager(new LinearLayoutManager(this));
        if (md.length()==7) { date.setText(md); }       // 빈칸 클릭시 02월일 로 빈칸인 부분도 출력되어 안되도록
    }
}
