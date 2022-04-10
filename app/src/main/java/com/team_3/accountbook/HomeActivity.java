package com.team_3.accountbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements CalendarAdapter.OnItemClick {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");
    private long firstBackPressedTime = 0;          // 뒤로가기 체크시간
    private TextView monthYearText, mAutoState, mYearMonth, date, mDayInfo, mIncomeTotal, mExpenseTotal, mTotal;
    private RecyclerView calendarRecyclerView, listRv;
    private LinearLayout mDateLayout, mNoDataLayout;
    String yyyyMM, dd;
    int dayType;
    private ImageView pre,next;
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
    protected void onRestart() {
        super.onRestart();
        setMonthView();
        setTotalAmount();       // 상단 수입 지출 갱신
        if (dd != null) {       // 리스트 갱신
            setList((ArrayList<Cost>) db.dao().getItemList(yyyyMM.replace(".", "년 ")+ "월" + " " + dd), yyyyMM, dd, dayType);
        }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottom_menu = findViewById(R.id.bottom_menu);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        listRv = findViewById(R.id.listRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        mAutoState = findViewById(R.id.autoState_home);
        mDateLayout = findViewById(R.id.homeLayout_data);
        mYearMonth = findViewById(R.id.yearMonth_home);
        date = findViewById(R.id.date);
        mDayInfo = findViewById(R.id.dayInfo);
        mIncomeTotal = findViewById(R.id.income_total);
        mExpenseTotal = findViewById(R.id.expense_total);
        mTotal = findViewById(R.id.total_home);
        mNoDataLayout = findViewById(R.id.homeLayout_noDataInfo);
        pre = findViewById(R.id.toPreMonth);
        next = findViewById(R.id.toNextMonth);
        db = AppDatabase.getInstance(this);


        initialSetting();

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
        setTotalAmount();
        mDateLayout.setVisibility(View.GONE);
        mNoDataLayout.setVisibility(View.GONE);

        pre.setOnClickListener((i)->{
            selectedDate = selectedDate.minusMonths(1);
            setMonthView();
            setTotalAmount();
        });

        next.setOnClickListener((i)->{
            selectedDate = selectedDate.plusMonths(1);
            setMonthView();
            setTotalAmount();
        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callPermission();
            }
        }, 1000);   // 1초 후 권한 요청이 실행됨.(바로 요청하면 요청이 씹힘. 딜레이를 줌으로 해결함)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED){
            db.dao().updateAutoState(false);
            mAutoState.setText("Auto-OFF");
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
            if(i <= dayOfWeek) {        // i가 달의 첫째 날보다 작을 때~
                daysInMonthArray.add((daysInPreMonth-(dayOfWeek-i))+"!");                              // ~빈칸으로 만듦.
            }
            else if(i > daysInMonth + dayOfWeek){   // i가 달의 마지막 날보다 클 때~
                daysInMonthArray.add((d++)+"?");
            }
            else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));

            }
        }


        return  daysInMonthArray;                                      // 완성한 달력 배열 반환.
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {      // LocalDate 형식(YYYY-MM-DD)의 데이터를 '----년 --월' 형식으로 변환하는 함수
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDateOnlyNum(LocalDate date) {      // LocalDate 형식(YYYY-MM-DD)의 데이터를 'yyyy.MMMM' 형식으로 변환하는 함수
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return date.format(formatter);
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {// 달력 이동 버튼 클릭시, 해당 달의 달력을 그리는 함수
        List<String> yyyyMM = new ArrayList<>();
        yyyyMM.add(monthYearFromDateOnlyNum(selectedDate.minusMonths(1)));
        yyyyMM.add(monthYearFromDateOnlyNum(selectedDate));
        yyyyMM.add(monthYearFromDateOnlyNum(selectedDate.plusMonths(1)));

        monthYearText.setText(monthYearFromDate(selectedDate));                    // 현재 년/월을 setText
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);            // 해당 달의 달력 배열을 만들어 daysInMonth 에 저장
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, yyyyMM, monthYearFromDate(selectedDate),
                monthYearFromDate(selectedDate.minusMonths(1)), monthYearFromDate(selectedDate.plusMonths(1)), this, this);   // 달력 배열을 가지는 Adapter 생성
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);   // 가로 7칸의 그리드뷰(퍼즐 형식)로 만드는 리사이클러뷰 레이아웃 매니저 layoutManager 생성
        calendarRecyclerView.setLayoutManager(layoutManager);                      // 레이아웃 매니저를 layoutManager 로 지정
        calendarRecyclerView.setAdapter(calendarAdapter);
    }



    @SuppressLint("SetTextI18n")
    private void setTotalAmount(){
        if(db.dao().getAutoState()){ mAutoState.setText("Auto-ON"); }
        else { mAutoState.setText("Auto-OFF"); }


        int total_in = 0;
        int total_ex = 0;

        try {
            total_in = db.dao().getAmountOfMonth(monthYearText.getText().toString(), "income");
            mIncomeTotal.setText(myFormatter.format(total_in));
        }
        catch (Exception e){ mIncomeTotal.setText("0"); }

        try {
            total_ex = db.dao().getAmountOfMonth(monthYearText.getText().toString(), "expense");
            mExpenseTotal.setText(myFormatter.format(total_ex));
        }
        catch (Exception e){ mExpenseTotal.setText("0"); }

        mTotal.setText(myFormatter.format(total_in-total_ex));
    }



    private void setList(ArrayList<Cost> arrayList, String yyyyMM, String dd, int dayType){
        this.yyyyMM = yyyyMM;
        this.dd = dd;
        this.dayType = dayType;

        if(!arrayList.isEmpty()){
            mNoDataLayout.setVisibility(View.GONE);
            listRv.setVisibility(View.VISIBLE);
            listRv.setAdapter(new adapter(arrayList));
            listRv.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            mNoDataLayout.setVisibility(View.VISIBLE);
            listRv.setVisibility(View.GONE);
        }

        mDateLayout.setVisibility(View.VISIBLE);
        mYearMonth.setText(yyyyMM);
        date.setText(dd);
        parseForKoreanDay(dayType);
    }



    @Override
    public void onClick(ArrayList<Cost> arrayList, String yyyyMM, String dd, int dayType) {    // CalendarAdapter 에서 요일을 클릭하면 호출돼어 실행되는 함수. 날짜에 맞는 활동정보 리스트를 받아서 출력함.
        setList(arrayList, yyyyMM, dd, dayType);

    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void parseForKoreanDay(int dayType){
        if(dayType == 1){
            mDayInfo.setBackground(getResources().getDrawable(R.drawable.weekend_sunday));
            mDayInfo.setText("일");
        }
        else if(dayType == 2){
            mDayInfo.setBackground(getResources().getDrawable(R.drawable.weekday));
            mDayInfo.setText("월");
        }
        else if(dayType == 3){
            mDayInfo.setBackground(getResources().getDrawable(R.drawable.weekday));
            mDayInfo.setText("화");
        }
        else if(dayType == 4){
            mDayInfo.setBackground(getResources().getDrawable(R.drawable.weekday));
            mDayInfo.setText("수");
        }
        else if(dayType == 5){
            mDayInfo.setBackground(getResources().getDrawable(R.drawable.weekday));
            mDayInfo.setText("목");
        }
        else if(dayType == 6){
            mDayInfo.setBackground(getResources().getDrawable(R.drawable.weekday));
            mDayInfo.setText("금");
        }
        else if(dayType == 7){
            mDayInfo.setBackground(getResources().getDrawable(R.drawable.weekend_saturday));
            mDayInfo.setText("토");
        }

    }



    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    public void mOnClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.clearList_home:
                mDateLayout.setVisibility(View.GONE);
                dd = null;

                break;

            case R.id.auto_home:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
                    Dialog dialog1 = new Dialog(this);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setContentView(R.layout.dialog_autosave);

                    showDialogOfAutoSave(dialog1);
                }
                else{
                    dialogForDeniedPermission("auto");
                }

                break;

            case R.id.message_home:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
                    Dialog dialog2 = new Dialog(this);
                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog2.setContentView(R.layout.dialog_movetomain);

                    showDialogToMain(dialog2);
                }
                else{
                    dialogForDeniedPermission("bring");
                }

                break;

            case R.id.fab_add:
                intent = new Intent(this, AddActivity.class);
                intent.putExtra("flag", "nothing");
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in_activity, R.anim.hold_activity);     // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;
        }
    }



    @SuppressLint("SetTextI18n")
    private void showDialogOfAutoSave(Dialog dialog){
        dialog.show();

        TextView mOnOff, mQuestion, mCancel, mAccept;

        mOnOff = dialog.findViewById(R.id.onOff);
        mQuestion = dialog.findViewById(R.id.questionForAutoSave);
        mCancel = dialog.findViewById(R.id.cancel_autoSave);
        mAccept = dialog.findViewById(R.id.accept_autoSave);

        if(mAutoState.getText().equals("Auto-OFF")){
            mOnOff.setText("자동 저장 ON");
            mQuestion.setText("자동 저장 기능을 활성화 하시겠습니까?");
        }
        else if(mAutoState.getText().equals("Auto-ON")){
            mOnOff.setText("자동 저장 OFF");
            mQuestion.setText("자동 저장 기능을 종료하시겠습니까?");
        }

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if(mAutoState.getText().equals("Auto-OFF")){
                    db.dao().updateAutoState(true);
                    mAutoState.setText("Auto-ON");
                    Toast.makeText(getApplicationContext(), "자동 저장 기능이 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(mAutoState.getText().equals("Auto-ON")){
                    db.dao().updateAutoState(false);
                    mAutoState.setText("Auto-OFF");
                    Toast.makeText(getApplicationContext(), "자동 저장 기능이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void showDialogToMain(Dialog dialog){
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        TextView mNoBring, mBring;
        EditText mMonths;

        mNoBring = dialog.findViewById(R.id.tv_noBring);
        mBring = dialog.findViewById(R.id.tv_bring);
        mMonths = dialog.findViewById(R.id.months);

        mMonths.setOnEditorActionListener((textView, i, keyEvent)->{
            try {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("months", Integer.parseInt(mMonths.getText().toString()));
                startActivity(intent);
                overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);     // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                dialog.dismiss();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "숫자만 입력하세요.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        mNoBring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mBring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("months", Integer.parseInt(mMonths.getText().toString()));
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);     // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                    dialog.dismiss();
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "숫자만 입력하세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    private void dialogForPermission(Dialog dialog){
        dialog.show();

        TextView mRefuse, mAccept;

        mRefuse = dialog.findViewById(R.id.refuse_permission);
        mAccept = dialog.findViewById(R.id.accept_permission);

        mRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPermission();
                dialog.dismiss();
            }
        });
    }



    private void dialogForDeniedPermission(String flag){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_denied_permission);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_dialog));
        dialog.show();

        ImageView mDeniedImage = dialog.findViewById(R.id.deniedImage);
        TextView mAccept = dialog.findViewById(R.id.accept_deniedPermission);
        TextView mAutoOrBring = dialog.findViewById(R.id.autoOrBring);

        if(flag.equals("auto")){
            mAutoOrBring.setText("자동저장");
        }
        else if(flag.equals("bring")){
            mDeniedImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_email_24));
            mAutoOrBring.setText("가져오기");
        }

        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    private void buildTableData(){
        Asset asset = new Asset();

        String[] assetName = {"현금", "은행", "선불식카드", "자동저장"};

        String[] wayName = {"지갑", "나라사랑", "경기지역화폐", "노리(nori)", "(Auto)"};
        int[] wayBalance = {43000, 99600, 3100, 1997500, 0};
        int[] FK_assetId = {1 ,2 ,3, 2, 4};

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



    private void initialSetting(){
        if(db.dao().getSortNames("income").toString() == "[]") {    // 비어있으면 추가.  초기설정!
            buildTableData();
        }
        if(db.dao().getAutoState() == null){                        // 비어있으면 추가.  초기설정!
            db.dao().insertAutoState(true);
        }
        if(db.dao().getAmountGoal() == null){                        // 비어있으면 추가.  초기설정!
            db.dao().insertWatch(new Watch("300000","70",true));
        }
    }



    private void callPermission() {       // 문자 권한 얻기
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//
//            requestPermissions(
//                    new String[]{Manifest.permission.READ_SMS},
//                    PERMISSIONS_REQUEST_READ_SMS);
//        } else {
//            // 해당 로직으로 이동
//        }
        List<String> listPermissionsNeeded = new ArrayList<>();

        int permissionCheck_READ = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if(permissionCheck_READ == PackageManager.PERMISSION_DENIED){
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }

        int permissionCheck_RECEIVE = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissionCheck_RECEIVE == PackageManager.PERMISSION_DENIED){
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }

        if(!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);

                if(grantResults.length > 0) {      // 권한 요청이 1개 이상이었을 때~
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if(perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED         // 권한이 수락됐을 때~
                            && perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(this, "SMS 접근 권한 수락됨", Toast.LENGTH_SHORT).show();
                    }
                    else{       // 권한이 처음 거부됐을 때~
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                            Dialog dialog = new Dialog(this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_permission);
                            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_dialog));

                            dialogForPermission(dialog);
                            break;

                        }
                        else {      // 권한 완전거부 상태(재요청 마저도 거절)
//                            Toast.makeText(this, "앱 설정에서 SMS 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                break;

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


}
