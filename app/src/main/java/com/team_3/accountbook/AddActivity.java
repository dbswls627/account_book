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
import android.util.Log;
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

public class AddActivity extends AppCompatActivity implements WayAndSortAdapter.touchItem {
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
    private int callValue = -1, myCostId = -1;
    Cost costAll;

    int begin1, begin2, begin3, begin4;

    InputMethodManager imm;
    Calendar c;
    int year, month, day, hour, minute;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    String hh, mm, dd;

    String preDate = "", preWay = "", preSum = "", preBody = "";

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.editText_expense);
        setContentView(R.layout.activity_add);

        reRun(preDate, preWay, preSum, preBody);

        // MainActivity 에서 리스트 클릭시 실행되는 부분
        String date = getIntent().getStringExtra("date");
        String body = getIntent().getStringExtra("body");
        int amount = getIntent().getIntExtra("amount", 0);
        ms = getIntent().getLongExtra("ms", 0);
        mDate.setText(date);
        mBody.setText(body);
        mSum.setText(String.valueOf(amount));

        c = Calendar.getInstance();        // date 가 비어 있을 경우 datePicker 가 현재 날짜를 가져오기 하기 위함
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        if (!mDate.getText().toString().equals("")) {   // date 가 비어 있으면 실행이 되지 않아 현재 시간 아니면 edittext 의 값
            year = Integer.parseInt(mDate.getText().toString().substring(0, 4));
            month = Integer.parseInt(mDate.getText().toString().substring(6, 8)) - 1;
            day = Integer.parseInt(mDate.getText().toString().substring(10, 12));
            hour = Integer.parseInt(mDate.getText().toString().substring(14, 16));
            minute = Integer.parseInt(mDate.getText().toString().substring(17, 19));
        }

        timePickerDialog = new TimePickerDialog(this, (view, h, m) -> {
            // 확인 눌렀을때 실행되는 곳
            hh = Integer.toString(h);
            mm = Integer.toString(m);
            if (h < 9) {
                hh = "0" + h;
            }          // 한자리 일시 앞에 0추가
            if (m < 10) {
                mm = "0" + m;
            }          // 한자리 일시 앞에 0추가
            mDate.setText(mDate.getText().toString().substring(0, 14) + hh + ":" + mm);
        }, hour, minute, true);                    // TimePicker 초기 값 현재 시각 or edittext 값 받아와서

        datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            // 확인 눌렀을때 실행되는 곳
            mm = Integer.toString(m + 1);
            dd = Integer.toString(d);
            if (m < 9) {
                mm = "0" + (m + 1);
            }       // 한자리 일시 앞에 0추가
            if (d < 10) {
                dd = "0" + d;
            }          // 한자리 일시 앞에 0추가
            mDate.setText(y + "년 " + mm + "월 " + dd + "일 00:00");     // y m d 피커에서 받아온 년월일을 edittext 에 설정
            timePickerDialog.show();             // 타임피커 띠우기
        }, year, month, day);                    // datePicker 초기 값  현재 년 월 일 or edittext 값 받아와서

        // ListInAssetActivity 에서 리스트 클릭시 실행되는 부분
        callValue = getIntent().getIntExtra("flag", -1);
        if (callValue == 1) {
            myCostId = getIntent().getIntExtra("costId", -1);
            costAll = db.dao().getCostAllOfCostId(myCostId);

            action = costAll.getDivision();
            setColorOfDivision(action);

            mDate.setText(costAll.getUseDate());
            mWay.setText(costAll.getFK_wayName());
            mSort.setText(costAll.getSortName());
            mSum.setText(costAll.getAmount() + "");
            mBody.setText(costAll.getContent());
        }

        begin1 = HomeActivity.wayBalance[0];
        begin2 = HomeActivity.wayBalance[1];
        begin3 = HomeActivity.wayBalance[2];
        begin4 = HomeActivity.wayBalance[3];


        if(callValue != 1){
            mExpense.setSelected(true);
            mSave.setSelected(true);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void reRun(String predate, String preWay, String preSum, String preBody) {
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
        if (cursorPosition == 1) {
            mWay.requestFocus();
        } else if (cursorPosition == 2) {
            mSort.requestFocus();
        } else if (cursorPosition == 3) {
            mSum.requestFocus();
        } else if (cursorPosition == 4) {
            mBody.requestFocus();
        }
        if (cursorPosition == 3 || cursorPosition == 4 || cursorPosition == -1) {
            mLayout.setVisibility(View.GONE);
        }


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


    private void setWayAndSortRV(String focus) {
        if (!focus.equals("")) {
            if (focus.equals("way")) {
                WayAndSortList = db.dao().getWayNames();
                mFlag.setText(" [ 자산 ] ");
            } else if (focus.equals("sort")) {
                WayAndSortList = db.dao().getSortNames(action);
                mFlag.setText(" [ 분류 ] ");
            }

            mRV_WayAndSort.setAdapter(new WayAndSortAdapter(WayAndSortList, this));
            mRV_WayAndSort.setLayoutManager(new LinearLayoutManager(this));
            mRV_WayAndSort.setVisibility(View.VISIBLE);     // 리스트 보이기
        }
    }



    private void setColorOfTheme(String actionFlag) {
        preDate = mDate.getText().toString();
        preWay = mWay.getText().toString();
        preSum = mSum.getText().toString();
        preBody = mBody.getText().toString();

        if (actionFlag.equals("income")) {
            setTheme(R.style.editText_income);
        } else if (actionFlag.equals("expense")) {
            setTheme(R.style.editText_expense);
        }
        setContentView(R.layout.activity_add);

        reRun(preDate, preWay, preSum, preBody);
    }



    private void setColorOfDivision(String division) {
        if (division.equals("income")) {
            action = "income";
            setColorOfTheme(action);

            checkIncome = true;
            checkExpense = false;

            mIncome.setSelected(true);
            mExpense.setSelected(false);
            mSave.setSelected(false);
            mIncome.setTextColor(Color.parseColor("#5DDE62"));      // 초록색
            mExpense.setTextColor(Color.parseColor("#757575"));     // 진회색
            mSave.setTextColor(Color.parseColor("#5DDE62"));        // 초록색
            mSort.setText("");
        }
        else if (division.equals("expense")) {
            action = "expense";
            setColorOfTheme(action);

            checkIncome = false;
            checkExpense = true;

            mIncome.setSelected(false);
            mExpense.setSelected(true);
            mSave.setSelected(true);
            mIncome.setTextColor(Color.parseColor("#757575"));      // 진회색
            mExpense.setTextColor(Color.parseColor("#FF5252"));     // 빨간색
            mSave.setTextColor(Color.parseColor("#FF5252"));        // 빨간색
            mSort.setText("");
        }
        setWayAndSortRV(focus);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_income:
                if (!checkIncome) {
                    setColorOfDivision("income");
                }
                break;

            case R.id.tv_expense:
                //mRV_WayAndSort.setVisibility(View.GONE);    // 자산 리스트 제거 <-- 입력하던 곳 rv는 띄워줘야 한다고 생각
                if (!checkExpense) {
                    setColorOfDivision("expense");
                }
                break;

            case R.id.tv_save:
                String amount = mSum.getText().toString();
                try {                                             // null 값을 받으면 에러가 나서 예외처리 사용.
                    amount = amount.replaceAll(",", "");          // 금액의 쉼표(,) 제거
                } catch (Exception e) {
                }

                // 저장버튼 클릭 순간의 데이터들 ↓
                int int_amount = Integer.parseInt(amount);
                String changeDate = mDate.getText().toString(), wayName = mWay.getText().toString();

                if (mDate.length() > 0 && mWay.length() > 0 && mSort.length() > 0 && mSum.length() > 0) {
                    if (callValue == 1) {         // 기존 데이터 수정 - ListInAssetActivity 에서 왔을 때
                        String iniDate = costAll.getUseDate();
                        int margin = 0;
                        if(action.equals("income")){ margin = int_amount - costAll.getAmount(); }
                        else if(action.equals("expense")){ margin = costAll.getAmount() - int_amount; }

                        if (iniDate.equals(changeDate) && costAll.getAmount() != int_amount) {    // 값만 변경(기존날짜 == 변경날짜 && 기존금액 != 변경금액)
                            List<Cost> afterData_detail = db.dao().getNowAfter_hard(iniDate, mBody.getText().toString(), myCostId, wayName);
                            List<Cost> afterData_today = db.dao().getNowAfter2(iniDate, mBody.getText().toString(), wayName);
                            List<Cost> afterData = db.dao().getCostDataAfter(iniDate, wayName);
                            afterData_detail.addAll(afterData_today);
                            afterData_detail.addAll(afterData);

                            db.dao().update_CostData(iniDate, wayName, mSort.getText().toString(),
                                    int_amount, costAll.getBalance()+margin, mBody.getText().toString(), myCostId);
                            for (int i = 0; i < afterData_detail.size(); i++) {
                                if (i < afterData_detail.size() - 1) {
                                    db.dao().update_NextCostBal_plus(margin, afterData_detail.get(i).getCostId());
                                }
                                else if (i == afterData_detail.size() - 1) {
                                    db.dao().update_NextCostBal_plus(margin, afterData_detail.get(i).getCostId());
                                    int n = db.dao().getCostBalance(afterData_detail.get(i).getCostId());
                                    db.dao().updateWayBal(n, wayName);
                                }
                            }
                        }
                        else if (!iniDate.equals(changeDate)) {    // 날짜 변경(기존날짜 != 변경날짜)
                            // 최신 데이터(이전 데이터 o, 다음 데이터 x)
                            // 기존 데이터 사이(이전 데이터 o, 다음 데이터 o)
                            // 첫 데이터(이전 데이터 x, 다음 데이터 o)
                        }
                        else if (!costAll.getFK_wayName().equals(mWay.getText().toString())) {    // 수단 변경(기존수단 != 변경수단)

                        }
                        else if(!costAll.getDivision().equals(action)){         // 구분(수입/지출) 변경

                        }



//                        db.dao().updateCostInfo(myCostId, action, mDate.getText().toString(), mWay.getText().toString(),
//                                mSort.getText().toString(), Integer.parseInt(amount), mBody.getText().toString());
//                        if(action.equals("expense")){
//                            if(costAll.getAmount() > Integer.parseInt(amount)){         // 더 작은 값으로 수정시
//                                int margin = costAll.getAmount() - Integer.parseInt(amount);
//                                db.dao().updateCostUnderBalanceOfEx(mDate.getText().toString(), mWay.getText().toString(), margin);
//                                db.dao().updateWayBalanceOfIn(mWay.getText().toString(), margin);
//                            }
//                            else if(costAll.getAmount() < Integer.parseInt(amount)){    // 더 큰 값으로 수정시
//                                int margin = Integer.parseInt(amount) - costAll.getAmount();
//                                db.dao().updateCostOverBalanceOfEx(mDate.getText().toString(), mWay.getText().toString(), margin);
//                                db.dao().updateWayBalanceOfEx(mWay.getText().toString(), margin);
//                            }
//                        }
//                        else if(action.equals("income")){
//                            if(costAll.getAmount() > Integer.parseInt(amount)){         // 더 작은 값으로 수정시
//                                int margin = costAll.getAmount() - Integer.parseInt(amount);
//                                db.dao().updateCostUnderBalanceOfIn(mDate.getText().toString(), mWay.getText().toString(), margin);
//                                db.dao().updateWayBalanceOfEx(mWay.getText().toString(), margin);
//                            }
//                            else if(costAll.getAmount() < Integer.parseInt(amount)){    // 더 큰 값으로 수정시
//                                int margin = Integer.parseInt(amount) - costAll.getAmount();
//                                db.dao().updateCostOverBalanceOfIn(mDate.getText().toString(), mWay.getText().toString(), margin);
//                                db.dao().updateWayBalanceOfIn(mWay.getText().toString(), margin);
//                            }
//                        }
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }


                    /*  ※첫 List<Cost> 4줄 설명※
                    현재 날짜의 이후 데이터들을 가져옴 A(afterData_today)
                        (현재 날짜 이후 데이터 → (입력 내용 >= 내용)의 데이터들)
                    이후 날짜의 데이터들을 가져옴 B(afterData)
                    A = A + B

                    현재 날짜 이전 데이터 가져옴 X(preData_today)
                    (현재 날짜 이전 데이터 → (입력 내용 < 내용)의 데이터들)
                    이전 날짜의 데이터들을 가져옴 Y(preData)
                    X = X + Y
                    */
                    else if (callValue == -1) {       // 새로운 데이터 입력
                        List<Cost> preData_today = db.dao().getNowPre(mDate.getText().toString(), mBody.getText().toString(), mWay.getText().toString());
                        List<Cost> afterData_today = db.dao().getNowAfter(mDate.getText().toString(), mBody.getText().toString(), mWay.getText().toString());
                        List<Cost> preData = db.dao().getCostDataPre(mDate.getText().toString(), mWay.getText().toString());
                        List<Cost> afterData = db.dao().getCostDataAfter(mDate.getText().toString(), mWay.getText().toString());

                        preData_today.addAll(preData);
                        afterData_today.addAll(afterData);

                        int preCostId = -100, afterCostId = -100;

                        try { preCostId = preData_today.get(0).getCostId(); }
                        catch (Exception ignored) { }
                        try { afterCostId = afterData_today.get(0).getCostId(); }
                        catch (Exception ignored) { }

                        updateBalanceOnByNewData(afterData_today, preCostId, afterCostId, int_amount, wayName);

                        Intent intent = new Intent(this, ListActivity.class);
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(this, "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                break;


        }
    }

    

    private void updateBalanceOnByNewData(List<Cost> afterData, int preCostId, int afterCostId, int int_amount, String wayName) {
        int A = -1;

        if (preCostId != -100 && afterCostId == -100) {   // 최신 데이터(이전 데이터 o, 다음 데이터 x)
            Log.d("LEEhj_add", "here1_최신 데이터(이전 데이터 o, 다음 데이터 x)");
            if (action.equals("income")) { A = db.dao().getCostBalance(preCostId) + int_amount; }  // 바로 이전 행의 잔액으로 현재 잔액을 계산
            else if (action.equals("expense")) { A = db.dao().getCostBalance(preCostId) - int_amount; }

            insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, A);
            db.dao().updateWayBal(A, wayName);
        }
        else if (preCostId != -100) {       // 기존 데이터 사이(이전 데이터 o, 다음 데이터 o)
            Log.d("LEEhj_add", "here2_기존 데이터 사이(이전 데이터 o, 다음 데이터 o)");
            if (action.equals("income")) { A = db.dao().getCostBalance(preCostId) + int_amount; }  // 바로 이전 행의 잔액으로 현재 잔액을 계산
            else if (action.equals("expense")) { A = db.dao().getCostBalance(preCostId) - int_amount; }

            insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, A);

            for (int i = 0; i < afterData.size(); i++) {
                if (action.equals("income")) {
                    if (i < afterData.size() - 1) {
                        db.dao().update_NextCostBal_plus(int_amount, afterData.get(i).getCostId());
                    }
                    else if (i == afterData.size() - 1) {
                        db.dao().update_NextCostBal_plus(int_amount, afterData.get(i).getCostId());
                        int n = db.dao().getCostBalance(afterData.get(i).getCostId());
                        db.dao().updateWayBal(n, wayName);
                    }
                }
                else if (action.equals("expense")) {
                    if (i < afterData.size() - 1) {
                        db.dao().update_NextCostBal_minus(int_amount, afterData.get(i).getCostId());
                    }
                    else if (i == afterData.size() - 1) {
                        db.dao().update_NextCostBal_minus(int_amount, afterData.get(i).getCostId());
                        int n = db.dao().getCostBalance(afterData.get(i).getCostId());
                        db.dao().updateWayBal(n, wayName);
                    }
                }
            }
        }
        else if (afterCostId != -100) {       // 첫 데이터(이전 데이터 x, 다음 데이터 o)
            Log.d("LEEhj_add", "here3_첫 데이터(이전 데이터 x, 다음 데이터 o)");
            if (action.equals("income")) {
                if (mWay.getText().toString().equals("지갑")) { A = begin1 + int_amount; }
                else if (mWay.getText().toString().equals("나라사랑")) { A = begin2 + int_amount; }
                else if (mWay.getText().toString().equals("경기지역화폐")) { A = begin3 + int_amount; }
                else if (mWay.getText().toString().equals("노리(nori)")) { A = begin4 + int_amount; }
            }
            else if (action.equals("expense")) {
                if (mWay.getText().toString().equals("지갑")) { A = begin1 - int_amount; }
                else if (mWay.getText().toString().equals("나라사랑")) { A = begin2 - int_amount; }
                else if (mWay.getText().toString().equals("경기지역화폐")) { A = begin3 - int_amount; }
                else if (mWay.getText().toString().equals("노리(nori)")) { A = begin4 - int_amount; }
            }
            insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, A);

            for (int i = 0; i < afterData.size(); i++) {
                if (action.equals("income")) {
                    if (i < afterData.size() - 1) {
                        db.dao().update_NextCostBal_plus(int_amount, afterData.get(i).getCostId());
                    }
                    else if (i == afterData.size() - 1) {
                        db.dao().update_NextCostBal_plus(int_amount, afterData.get(i).getCostId());
                        int n = db.dao().getCostBalance(afterData.get(i).getCostId());
                        db.dao().updateWayBal(n, wayName);
                    }
                }
                else if (action.equals("expense")) {
                    if (i < afterData.size() - 1) {
                        db.dao().update_NextCostBal_minus(int_amount, afterData.get(i).getCostId());
                    }
                    else if (i == afterData.size() - 1) {
                        db.dao().update_NextCostBal_minus(int_amount, afterData.get(i).getCostId());
                        int n = db.dao().getCostBalance(afterData.get(i).getCostId());
                        db.dao().updateWayBal(n, wayName);
                    }
                }
            }
        }
        else {      // 초기 데이터(이전 데이터 x, 다음 데이터 x)
            Log.d("LEEhj_add", "here4_초기 데이터(이전 데이터 x, 다음 데이터 x)");
            if (action.equals("income")) { A = db.dao().getWayBalance(wayName) + int_amount; }
            else if (action.equals("expense")) { A = db.dao().getWayBalance(wayName) - int_amount; }

            insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, A);
            db.dao().updateWayBal(A, wayName);
        }
    }


    private void insertDataToCostTable(String date, String wayName, int amount, int balance) {
        db.dao().insertCost(
                date,                     // 날짜
                wayName,                                        // 수단
                mSort.getText().toString(),                     // 분류
                amount,                                     // 금액
                mBody.getText().toString(),                     // 내용
                balance,                                        // 잔액
                action,                                         // 구분
                ms                                              // 수신시간(마이크로초)
        );
    }


    @Override
    public void clickItem(String itemName) {
        if (focus.equals("way")) {
            mWay.setText(itemName);
        } else if (focus.equals("sort")) {
            mSort.setText(itemName);
        }
    }


    private class NumberTextWatcher implements TextWatcher {
        private DecimalFormat dfnd;        // ~ ~ DecimalFormat 클래스 객체 dfnd
        private EditText edit_sum;

        public NumberTextWatcher(EditText et) {       // 생성자.

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
                } else {             // 커서가 맨 왼쪽으로 가게되면 맨 오른쪽으로 이동시킴
                    edit_sum.setSelection(edit_sum.getText().length());
                }
            } catch (NumberFormatException nfe) {
                // do nothing?
            } catch (ParseException e) {
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