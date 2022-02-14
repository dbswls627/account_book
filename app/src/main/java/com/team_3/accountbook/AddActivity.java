package com.team_3.accountbook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class AddActivity extends AppCompatActivity implements WayAndSortAdapter.touchItem{
    List<String> WayAndSortList;
    RecyclerView mRV_WayAndSort;

    LinearLayout mLayout;
    EditText mDate, mWay, mSort, mSum, mBody;
    TextView mIncome, mExpense, mSave, mFlag;
    AppDatabase db;
    long ms;
    boolean checkIncome = false, checkExpense = true;
    String action = "expense";
    String focus = "";
    int cursorPosition = -1;

    InputMethodManager imm;
    Calendar c;
    int year, month, day, hour, minute;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    String hh, mm, dd;

    String preDate = "", preWay = "", preSum = "", preBody = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.editText_expense);
        setContentView(R.layout.activity_add);

        reRun(preDate, preWay, preSum, preBody);

        // MainActivity 에서 리스트 클릭시 실행되는 부분
        String date = getIntent().getStringExtra("date");
        String body = getIntent().getStringExtra("body");
        int amount = getIntent().getIntExtra("amount",0);
        ms = getIntent().getLongExtra("ms",0);
        mDate.setText(date);
        mBody.setText(body);
        mSum.setText(String.valueOf(amount));

        mExpense.setSelected(true);
        mSave.setSelected(true);
    }



    private void setWayAndSortRV(String focus){
        if(!focus.equals("")){      // way 랑 sort 를 입력할 때만 실행
            if(focus.equals("way")) {
                WayAndSortList = db.dao().getWayNames();
                mFlag.setText(" [ 자산 ] ");
            }
            else if (focus.equals("sort")) {
                WayAndSortList = db.dao().getSortNames(action);
                mFlag.setText(" [ 분류 ] ");
            }

            mRV_WayAndSort.setAdapter(new WayAndSortAdapter(WayAndSortList, focus, this));
            mRV_WayAndSort.setLayoutManager(new LinearLayoutManager(this));
            mRV_WayAndSort.setVisibility(View.VISIBLE);     // 리스트 보이기
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void reRun(String predate, String preWay, String preSum, String preBody){
        mLayout = findViewById(R.id.l_layout);      // RV 상단바
        mFlag = findViewById(R.id.tv_flag);         // RV 상단바 내부 text
        mIncome = findViewById(R.id.tv_income);     // 수입버튼
        mExpense = findViewById(R.id.tv_expense);   // 지출버튼
        mDate = findViewById(R.id.date);            // 날짜
        mWay = findViewById(R.id.way);              // 수단
        mSort = findViewById(R.id.sort);            // 분류
        mSum = findViewById(R.id.edit_sum);         // 금액
        mBody = findViewById(R.id.body);            // 내용
        mSave = findViewById(R.id.tv_save);         // 저장버튼
        mRV_WayAndSort = findViewById(R.id.rv_WayAndSort);

        db = AppDatabase.getInstance(this);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   //키보드 내리기 할 때 필요한 객체
        mSum.addTextChangedListener(new NumberTextWatcher(mSum));            // 금액 입력반응

        // 다시 그리며 이전 값들 들고오기
        mDate.setText(predate);
        mWay.setText(preWay);
        mSum.setText(preSum);
        mBody.setText(preBody);
        if(cursorPosition == 1){ mWay.requestFocus(); }
        else if(cursorPosition == 2){ mSort.requestFocus(); }
        else if(cursorPosition == 3){ mSum.requestFocus(); }
        else if(cursorPosition == 4){ mBody.requestFocus(); }
        if(cursorPosition == 3 || cursorPosition == 4 || cursorPosition == -1){ mLayout.setVisibility(View.GONE); }

        c = Calendar.getInstance();        // date 가 비어 있을 경우 datePicker 가 현재 날짜를 가져오기 하기 위함
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        if (!mDate.getText().toString().equals("")) {   // date 가 비어 있으면 실행이 되지 않아 현재 시간 아니면 edittext 의 값
            year = Integer.parseInt(mDate.getText().toString().substring(0, 4));
            month = Integer.parseInt(mDate.getText().toString().substring(6, 8))-1;
            day = Integer.parseInt(mDate.getText().toString().substring(10, 12));
            hour = Integer.parseInt(mDate.getText().toString().substring(14, 16));
            minute = Integer.parseInt(mDate.getText().toString().substring(17, 19));
        }

        timePickerDialog = new TimePickerDialog(this, (view, h, m)-> {
            // 확인 눌렀을때 실행되는 곳
            hh = Integer.toString(h);
            mm = Integer.toString(m);
            if(h < 9) { hh = "0" + h; }          // 한자리 일시 앞에 0추가
            if(m < 10) { mm = "0" + m; }          // 한자리 일시 앞에 0추가
            mDate.setText(mDate.getText().toString().substring(0,14)+ hh + ":" + mm);
        }, hour, minute, true);                    // TimePicker 초기 값 현재 시각 or edittext 값 받아와서

        datePickerDialog = new DatePickerDialog(this, (view, y, m, d)-> {
            // 확인 눌렀을때 실행되는 곳
            mm = Integer.toString(m+1);
            dd = Integer.toString(d);
            if(m < 9) { mm = "0" + (m+1); }       // 한자리 일시 앞에 0추가
            if(d < 10) { dd = "0" + d; }          // 한자리 일시 앞에 0추가
            mDate.setText(y+"년 "+mm+"월 "+dd+"일 00:00");     // y m d 피커에서 받아온 년월일을 edittext 에 설정
            timePickerDialog.show();             // 타임피커 띠우기
        }, year, month, day);                    // datePicker 초기 값  현재 년 월 일 or edittext 값 받아와서


        mWay.setInputType(InputType.TYPE_NULL);         // 클릭시 키보드 안올라오게 함.
        mSort.setInputType(InputType.TYPE_NULL);        // 클릭시 키보드 안올라오게 함.

        mDate.setOnTouchListener((view, motionEvent) -> {       // 터치즉시 이벤트 발생(mOnClick 시 2번 터치)
            focus = "";
            mLayout.setVisibility(View.GONE);
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      // 키보드 내리기 (다른 edittext 누른후 누른면 키보드가 뜸)
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            datePickerDialog.show();        // 데이트피커 띠우기
            return false;
        });
        mWay.setOnTouchListener((view, motionEvent) -> {
            focus = "way";
            cursorPosition = 1;
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      // 키보드 내리기
            mLayout.setVisibility(View.VISIBLE);                        // 상단바 보이기
            setWayAndSortRV(focus);
            return false;
        });
        mSort.setOnTouchListener((view, motionEvent) -> {
            focus = "sort";
            cursorPosition = 2;
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      // 키보드 내리기
            mLayout.setVisibility(View.VISIBLE);                        // 상단바 보이기
            setWayAndSortRV(focus);
            return false;
        });
        mSum.setOnTouchListener((view, motionEvent) -> {
            focus = "";
            cursorPosition = 3;
            mLayout.setVisibility(View.GONE);
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            return false;
        });
        mBody.setOnTouchListener((view, motionEvent) -> {
            focus = "";
            cursorPosition = 4;
            mLayout.setVisibility(View.GONE);
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            return false;
        });
    }

    private void setThemeColor(String actionFlag){
        preDate = mDate.getText().toString();
        preWay = mWay.getText().toString();
        preSum = mSum.getText().toString();
        preBody = mBody.getText().toString();

        if(actionFlag.equals("income")){ setTheme(R.style.editText_income); }
        else if(actionFlag.equals("expense")){ setTheme(R.style.editText_expense); }
        setContentView(R.layout.activity_add);

        reRun(preDate, preWay, preSum, preBody);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.tv_income:
                if(!checkIncome){
                    action = "income";
                    setThemeColor(action);

                    checkIncome = true;
                    checkExpense = false;

                    mIncome.setSelected(true);
                    mExpense.setSelected(false);
                    mSave.setSelected(false);
                    mIncome.setTextColor(Color.parseColor("#5DDE62"));      // 초록색
                    mExpense.setTextColor(Color.parseColor("#757575"));     // 진회색
                    mSave.setTextColor(Color.parseColor("#5DDE62"));        // 초록색
                    mSort.setText("");

                    setWayAndSortRV(focus);
                }
                break;

            case R.id.tv_expense:
                //mRV_WayAndSort.setVisibility(View.GONE);    // 자산 리스트 제거 <-- 입력하던 곳 rv는 띄워줘야 한다고 생각
                if(!checkExpense){
                    action = "expense";
                    setThemeColor(action);

                    checkIncome = false;
                    checkExpense = true;

                    mIncome.setSelected(false);
                    mExpense.setSelected(true);
                    mSave.setSelected(true);
                    mIncome.setTextColor(Color.parseColor("#757575"));      // 진회색
                    mExpense.setTextColor(Color.parseColor("#FF5252"));     // 빨간색
                    mSave.setTextColor(Color.parseColor("#FF5252"));        // 빨간색
                    mSort.setText("");

                    setWayAndSortRV(focus);
                }
                break;

            case R.id.tv_save:
                String amount = mSum.getText().toString();
                try {                                             // null 값을 받으면 에러가 나서 예외처리 사용.
                    amount = amount.replaceAll(",", "");          // 금액의 쉼표(,) 제거
                }
                catch (Exception e){ }

                if(mDate.length() > 0 && mWay.length() > 0 && mSort.length() > 0 && mSum.length() > 0){
                    db.dao().insertCost(
                            mDate.getText().toString(),                         // 날짜 - 날짜선택박스 구현 필요
                            db.dao().getFk(mWay.getText().toString()),          // 수단 - 리스트 구현 필요
                            mSort.getText().toString(),                         // 분류 - 리스트 구현 필요
                            Integer.parseInt(amount),                       // 금액
                            mBody.getText().toString(),                     // 내용
                            0,                                                  // 잔액 - 계산 구현 필요
                            action,                                         // 구분
                            ms                                              // 수신시간(마이크로초)
                    );
                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
                }
                else{ Toast.makeText(this, "모두 입력해주세요.", Toast.LENGTH_SHORT).show(); }


                break;


        }
    }

    @Override
    public void clickItem(String itemName, String flag){
        if(flag.equals("way")){
            mWay.setText(itemName);
        }
        else if(flag.equals("sort")){
            mSort.setText(itemName);
        }
    }



    private class NumberTextWatcher implements TextWatcher {
        private DecimalFormat dfnd;        // ~ ~ DecimalFormat 클래스 객체 dfnd
        private EditText edit_sum;

        public NumberTextWatcher(EditText et){       // 생성자.

            dfnd = new DecimalFormat("#,###");         // "#,###" 형식 지정
            this.edit_sum = et;                        // 매개변수로 받은 EditText 를 edit_sum 에 저장
        }


        public void afterTextChanged(Editable s) {
            edit_sum.removeTextChangedListener(this);

            try {
                int inilen, endlen;                     // 초기 문자열 길이, 최종 문자열 길이
                int cp = edit_sum.getSelectionStart();  // 커서 위치값 cp (맨왼쪽부터 0ㅊ1ㅊ2ㅊ3...)

                inilen = edit_sum.getText().length();   // edittext 에 입력된 문자열 길이 (',' 추가 전 길이)
                String v = s.toString().replace(String.valueOf(dfnd.getDecimalFormatSymbols().getGroupingSeparator()), "");   // 입력된 값 s에서 숫자만 추출
                Number n = dfnd.parse(v);                 // 문자열 v를 Number 형 n으로 치환 (Number: 숫자계의 Object 클래스)


                    edit_sum.setText(dfnd.format(n));


                endlen = edit_sum.getText().length();   // 입력된 전체 길이 (새로운 ',' 추가 길이)
                int sel = (cp + (endlen - inilen));     // ★현재 커서 위치 (???)

                if (sel > 0) {
                    edit_sum.setSelection(sel);
                }
                else {             // 커서가 맨 왼쪽으로 가게되면 맨 오른쪽으로 이동시킴
                    edit_sum.setSelection(edit_sum.getText().length());
                }
            }
            catch (NumberFormatException nfe) {
                // do nothing?
            }
            catch (ParseException e) {
                // do nothing?
            }
            edit_sum.addTextChangedListener(this);
        }


        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }


}