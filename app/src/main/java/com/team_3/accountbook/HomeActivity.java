package com.team_3.accountbook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements CalendarAdapter.OnItemClick {
    private long firstBackPressedTime = 0;          // 뒤로가기 체크시간
    private TextView monthYearText, date;
    private RecyclerView calendarRecyclerView, listRv;
    private LinearLayout mDateLayout;
    AppDatabase db;

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
        mDateLayout = findViewById(R.id.homeLayout_data);
        date = findViewById(R.id.date);

        db = AppDatabase.getInstance(this);

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
        mDateLayout.setVisibility(View.GONE);

        if(db.dao().getSortNames("income").toString() == "[]") {     // 비어있으면 추가.  초기설정!
            buildTableData();
        }
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
        YearMonth yearMonth = YearMonth.from(date);                    // ex)2022-03  .from(date): date 의 인스턴스를 가져온다.    *안드로이드 developers 설명: from(TemporalAccessor temporal) - Obtains an instance of YearMonth from a temporal object.
        YearMonth yearPreMonth = YearMonth.from(date.minusMonths(1));
        int daysInMonth = yearMonth.lengthOfMonth();                   // 월의 길이(1월이면 31 2월이면 28...).            .lengthOfMonth(): 연도를 고려하여 월의 길이를 반환함.
        int daysInPreMonth = yearPreMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);       // 달 첫째 날짜.ex)2022-03-01         .withDayOfMonth(i): 달의 i일 날짜를 반환함.
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();        // 달 첫째 날의 요일.(0-일 1-월 ... 6-토)      .getDayOfWeek(): 요일 반환 메소드.
        int d = 1;
        for(int i = 1; i <= 42; i++) {                                 // 그리드뷰의 첫번째 칸부터 마지막 칸까지
            if(i <= dayOfWeek) {        // i가 달의 첫째 날보다 작거나 마지막 날보다 크면~
                daysInMonthArray.add(String.valueOf(daysInPreMonth-(dayOfWeek-i))+"!");                              // ~빈칸으로 만듦.
            }
            else if(i > daysInMonth + dayOfWeek){
                daysInMonthArray.add(String.valueOf(d++)+"!");
            }
            else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));

            }
        }
        boolean front = true;
        boolean back = true;


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
    public void onClick(ArrayList<Cost> arrayList, String md) {    // CalendarAdapter 에서 요일을 클릭하면 호출돼어 실행되는 함수. 날짜에 맞는 활동정보 리스트를 받아서 출력함.
        if (md.length() == 7) {        // 빈칸 클릭시 02월일 로 빈칸인 부분도 출력되어 안되도록
            listRv.setAdapter(new adapter(arrayList));
            listRv.setLayoutManager(new LinearLayoutManager(this));
            mDateLayout.setVisibility(View.VISIBLE);
            date.setText(md);
        }
    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.fab_add:
                Intent intent = new Intent(this, AddActivity.class);
                intent.putExtra("flag", "nothing");
                startActivity(intent);

                break;
        }
    }



    private void buildTableData(){
        Asset asset = new Asset();

        String[] assetName = {"현금", "은행", "선불식카드"};

        String[] wayName = {"지갑", "나라사랑", "경기지역화폐", "노리(nori)"};
        int[] wayBalance = {43000, 99600, 3100, 1997500};
        int[] FK_assetId = {1 ,2 ,3, 2};

        String[] sortName = {"식비", "교통/차량", "문화생활", "패션/미용", "생활용품", "경조사/회비", "건강", "교육", "월급", "용돈", "부수입", "금융소득", "기타"};

//        int[] amount = {-1000, -500, -500, -2000, 1100};
//        String[] content = {"버스비", "샤프심", "볼펜", "파리바게트", "차액"};
//        String[] date = {"01월 02일", "01월 10일", "01월 04일", "01월 03일", "01월 20일"};
//        int[] balance = {99000, 98500, 1997500, 1998000, 99600};
//        String[] sortName = {"교통/차량", "생활용품", "생활용품", "식비", "잔액수정"};
//        String[] division = {"expense", "expense", "expense", "expense", null};
//        int[] FK_wayId = {2, 2, 4, 4, 2};


        for (int i = 0; i < assetName.length; i++) {
            asset.setAssetName(assetName[i]);
            db.dao().insertAsset(asset);
        }
        for (int i = 0; i < wayName.length; i++) {
            db.dao().insertWay(wayName[i], wayBalance[i], FK_assetId[i]);
        }
        for (int i = 0; i < sortName.length; i++) {
            if(i < 8){ db.dao().insertSort(sortName[i], "expense"); }
            else { db.dao().insertSort(sortName[i], "income"); }
        }
      /*  for (int i = 0; i < amount.length; i++) {
            db.dao().insertCost(amount[i], content[i], date[i], balance[i], sortName[i], division[i], FK_wayId[i]);
        }*/
    }
}
