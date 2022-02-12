package com.team_3.accountbook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

public class AddActivity extends AppCompatActivity implements WayAndSortAdapter.touchItem{
    List<String> WayAndSortList;
    RecyclerView mRV_WayAndSort;

    EditText mDate, mWay, mSort, mEditSum, mBody;
    TextView mIncome, mExpense;
    AppDatabase db;
    long ms;
    boolean checkIncome = false, checkExpense = true;
    String action = "expense";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = AppDatabase.getInstance(this);

        mIncome = findViewById(R.id.tv_income);     // 수입버튼
        mExpense = findViewById(R.id.tv_expense);   // 지출버튼
        mDate = findViewById(R.id.date);            // 날짜
        mWay = findViewById(R.id.way);              // 수단
        mSort = findViewById(R.id.sort);            // 분류
        mEditSum = findViewById(R.id.edit_sum);     // 금액
        mBody = findViewById(R.id.body);            // 내용
        mRV_WayAndSort = findViewById(R.id.rv_WayAndSort);

        mEditSum.addTextChangedListener(new NumberTextWatcher(mEditSum));       // 금액 입력반응
        mExpense.setSelected(true);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   //키보드 내리기 힐때 필요한 객체

        mWay.setInputType(InputType.TYPE_NULL);         // 클릭시 키보드 안올라오게 함.
        mSort.setInputType(InputType.TYPE_NULL);        // 클릭시 키보드 안올라오게 함.

        // MainActivity 에서 리스트 클릭시 실행되는 부분
        String date =getIntent().getStringExtra("date");
        String body =getIntent().getStringExtra("body");
        int amount =getIntent().getIntExtra("amount",0);
        ms =getIntent().getLongExtra("ms",0);
        mDate.setText(date);
        mBody.setText(body);
        mEditSum.setText(String.valueOf(amount));

        mDate.setOnTouchListener((view, motionEvent) -> {       //터치즉시 이벤트 발생(mOnClick 시 2번 터치)
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            return false;
        });
        mWay.setOnTouchListener((view, motionEvent) -> {
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      //키보드 내리기 (다른 edittext 누른후 누른면 키보드가 뜸)
            WayAndSortList = db.dao().getWayName();
            mRV_WayAndSort.setAdapter(new WayAndSortAdapter(WayAndSortList, this));
            mRV_WayAndSort.setLayoutManager(new LinearLayoutManager(this));
            mRV_WayAndSort.setVisibility(View.VISIBLE);     //자산 리스트 보이기
            return false;
        });
        mSort.setOnTouchListener((view, motionEvent) -> {
            /*WayAndSortList = db.dao().getSortName();
            mRV_WayAndSort.setAdapter(new WayAndSortAdapter(WayAndSortList, this));
            mRV_WayAndSort.setLayoutManager(new LinearLayoutManager(this));*/
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      //키보드 내리기 (다른 edittext 누른후 누른면 키보드가 뜸)
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            return false;
        });
        mEditSum.setOnTouchListener((view, motionEvent) -> {

            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            return false;
        });
        mBody.setOnTouchListener((view, motionEvent) -> {
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            return false;
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mOnClick(View v){
        String amount = mEditSum.getText().toString();
        try {                                             // null 값을 받으면 에러가 나서 예외처리 사용.
            amount = amount.replaceAll(",", "");          // 금액의 쉼표(,) 제거
        }
        catch (Exception e){ }

        switch (v.getId()){
            case R.id.tv_income:
                mRV_WayAndSort.setVisibility(View.GONE);    //자산 리스트 제거
                if(!checkIncome){
                    checkIncome = true;
                    checkExpense = false;
                    action = "income";

                    mIncome.setSelected(true);
                    mExpense.setSelected(false);

                }
                break;

            case R.id.tv_expense:
                mRV_WayAndSort.setVisibility(View.GONE);    //자산 리스트 제거
                if(!checkExpense){
                    checkIncome = false;
                    checkExpense = true;
                    action = "expense";

                    mIncome.setSelected(false);
                    mExpense.setSelected(true);
                }
                break;

            case R.id.save:

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

                break;


        }
    }

    @Override
    public void clickItem(String itemName){
        mWay.setText(itemName);
    }



    class NumberTextWatcher implements TextWatcher {
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