package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("deprecation")
@RequiresApi(api = Build.VERSION_CODES.O)
public class AddActivity extends AppCompatActivity implements WayAndSortAdapter.touchItem {
    List<String> WayAndSortList;
    RecyclerView mRV_WayAndSort;
    LocalDate selectedDate = LocalDate.now();
    LinearLayout mLayout;
    private ImageView mToEditWay, mClear;
    EditText mDate, mWay, mSort, mSum, mBody;
    TextView mTopDivision, mIncome, mExpense, mSave, mFlag, mDelete;
    AppDatabase db;
    long ms;
    boolean checkIncome = false, checkExpense = true;
    String action = "expense", actionKorean = "지출";
    String focus = "";
    int cursorPosition = -1;
    private String callValue = "nothing";
    private int myCostId = -1;
    Cost costAll;

    InputMethodManager imm;
    Calendar c;
    int year, month, day, hour, minute;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    String hh, mm, dd;

    String preDate = "", preWay = "", preSum = "", preBody = "";

    Dialog dialog;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.editText_expense);
        setContentView(R.layout.activity_add);

        reRun(preDate, preWay, preSum, preBody);


        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm");
        String processedNow = sdf.format(now);
        mDate.setText(processedNow);

        c = Calendar.getInstance();        // date 가 비어 있을 경우 datePicker 가 현재 날짜를 가져오기 하기 위함
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);



        callValue = getIntent().getStringExtra("flag");
        if (callValue.equals("ListInAsset_modify")) {
            // ListInAssetActivity 에서 리스트 클릭시 실행되는 부분
            myCostId = getIntent().getIntExtra("costId", -1);
            costAll = db.dao().getCostAllOfCostId(myCostId);

            action = costAll.getDivision();
            setColorOfDivision(action);

            mDate.setText(costAll.getUseDate());
            mWay.setText(costAll.getFK_wayName());
            mSort.setText(costAll.getSortName());
            mSum.setText(costAll.getAmount() + "");
            mBody.setText(costAll.getContent());

            mSave.setVisibility(View.GONE);
            mDelete.setVisibility(View.VISIBLE);
        }
        else if(callValue.equals("ListInAsset_add")){
            // ListInAssetActivity 에서 추가버튼 클릭시 실행되는 부분
            String myWayName = getIntent().getStringExtra("wayName");
            mWay.setText(myWayName);
            setColorOfDivision(action);
        }
        else if(callValue.equals("Main")){
            // MainActivity 에서 리스트 클릭시 실행되는 부분
            String date = getIntent().getStringExtra("date");
            String body = getIntent().getStringExtra("body");
            int amount = getIntent().getIntExtra("amount", 0);
            ms = getIntent().getLongExtra("ms", 0);
            mDate.setText(date);
            mBody.setText(body);
            mSum.setText(String.valueOf(amount));
            setColorOfDivision(action);
        }
        else if(callValue.equals("nothing")){
            // HomeActivity 에서 추가버튼 클릭시 실행되는 부분
            mExpense.setSelected(true);
            mSave.setSelected(true);
        }
        if (!mDate.getText().toString().equals("")) {   // date 가 비어 있으면 실행이 되지 않아 현재 시간 아니면 edittext 의 값
            Log.d("Test","notEmpty");
            year = Integer.parseInt(mDate.getText().toString().substring(0, 4));
            month = Integer.parseInt(mDate.getText().toString().substring(6, 8)) - 1;
            day = Integer.parseInt(mDate.getText().toString().substring(10, 12));
            hour = Integer.parseInt(mDate.getText().toString().substring(14, 16));
            minute = Integer.parseInt(mDate.getText().toString().substring(17, 19));
            Log.d("test",mDate.getText().toString().substring(0, 4));
            Log.d("test",mDate.getText().toString().substring(6, 8));
            Log.d("test",mDate.getText().toString().substring(10, 12));
        }

        timePickerDialog = new TimePickerDialog(this, (view, h, m) -> {
            // 확인 눌렀을때 실행되는 곳
            hh = Integer.toString(h);
            mm = Integer.toString(m);
            if (h < 10) {
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
    }


    private void saveOrDeleteSetting(){
        if(callValue.equals("ListInAsset_modify") && mSave.getVisibility() == View.GONE){
            mSave.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.GONE);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void reRun(String predate, String preWay, String preSum, String preBody) {
        mTopDivision = findViewById(R.id.topDivision);  // 액션바 구분 Text
        mLayout = findViewById(R.id.l_layout);      // RV 레이아웃
        mFlag = findViewById(R.id.tv_flag);         // RV 상단바 내부 text
        mToEditWay = findViewById(R.id.toEditWay_add);  // RV 상단바 편집버튼
        mClear = findViewById(R.id.clearList_add);  // RV 상단바 리스트 닫기버튼
        mIncome = findViewById(R.id.tv_income);     // 수입버튼
        mExpense = findViewById(R.id.tv_expense);   // 지출버튼
        mDate = findViewById(R.id.date);            // 날짜
        mWay = findViewById(R.id.way);              // 수단
        mSort = findViewById(R.id.sort);            // 분류
        mSum = findViewById(R.id.edit_sum);         // 금액
        mBody = findViewById(R.id.body);            // 내용
        mSave = findViewById(R.id.tv_save);         // 저장버튼
        mDelete = findViewById(R.id.tv_delete);     // 삭제버튼
        mRV_WayAndSort = findViewById(R.id.rv_WayAndSort);

        db = AppDatabase.getInstance(this);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   //키보드 내리기 할 때 필요한 객체
        mSum.addTextChangedListener(new NumberTextWatcher(mSum));            // 금액 입력반응

        mTopDivision.setText(actionKorean);
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

        mSave.setVisibility(View.VISIBLE);
        mDelete.setVisibility(View.GONE);


        mDate.setOnTouchListener((view, motionEvent) -> {       // 터치즉시 이벤트 발생(mOnClick 시 2번 터치)
            focus = "";
            mLayout.setVisibility(View.GONE);
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      // 키보드 내리기 (다른 edittext 누른후 누른면 키보드가 뜸)
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            datePickerDialog.show();        // 데이트피커 띠우기
            saveOrDeleteSetting();
            return false;
        });
        mWay.setOnTouchListener((view, motionEvent) -> {
            focus = "way";
            cursorPosition = 1;
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      // 키보드 내리기
            mLayout.setVisibility(View.VISIBLE);                        // 상단바 보이기
            setWayAndSortRV(focus);
            saveOrDeleteSetting();
            return false;
        });
        mSort.setOnTouchListener((view, motionEvent) -> {
            focus = "sort";
            cursorPosition = 2;
            imm.hideSoftInputFromWindow(mWay.getWindowToken(), 0);      // 키보드 내리기
            mLayout.setVisibility(View.VISIBLE);                        // 상단바 보이기
            setWayAndSortRV(focus);
            saveOrDeleteSetting();
            return false;
        });
        mSum.setOnTouchListener((view, motionEvent) -> {
            focus = "";
            cursorPosition = 3;
            mLayout.setVisibility(View.GONE);
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            saveOrDeleteSetting();
            return false;
        });
        mBody.setOnTouchListener((view, motionEvent) -> {
            focus = "";
            cursorPosition = 4;
            mLayout.setVisibility(View.GONE);
            mRV_WayAndSort.setVisibility(View.INVISIBLE);
            saveOrDeleteSetting();
            return false;
        });
    }


    private void setWayAndSortRV(String focus) {
        if (!focus.equals("")) {
            if (focus.equals("way")) {
                WayAndSortList = db.dao().getWayNames();
                mFlag.setText(" [ 자산 - 수단 ] ");
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



    @SuppressLint("ResourceAsColor")
    private void setColorOfDivision(String division) {
        if (division.equals("income")) {
            action = "income";
            actionKorean = "수입";
            setColorOfTheme(action);

            checkIncome = true;
            checkExpense = false;

            mIncome.setSelected(true);
            mExpense.setSelected(false);
            mSave.setSelected(false);
            mIncome.setTextColor(getResources().getColor(R.color.hardGreen));           // 초록색
            mExpense.setTextColor(getResources().getColor(R.color.grayForText));    // 진회색
            mSave.setTextColor(getResources().getColor(R.color.hardGreen));             // 초록색
            mSort.setText("");
        }
        else if (division.equals("expense")) {
            action = "expense";
            actionKorean = "지출";
            setColorOfTheme(action);

            checkIncome = false;
            checkExpense = true;

            mIncome.setSelected(false);
            mExpense.setSelected(true);
            mSave.setSelected(true);
            mIncome.setTextColor(getResources().getColor(R.color.grayForText));     // 진회색
            mExpense.setTextColor(getResources().getColor(R.color.red));            // 빨간색
            mSave.setTextColor(getResources().getColor(R.color.red));               // 빨간색
            mSort.setText("");
        }
        setWayAndSortRV(focus);
    }


    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.toBack_add:
                finish();

                break;

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

            case R.id.toEditWay_add:
                if(focus.equals("way")){
                    Intent intent = new Intent(this, AssetForEditActivity.class);
                    startActivityForResult(intent, 0);
                }
                else if(focus.equals("sort")){
                    Toast.makeText(this, "분류 편집 이동예정", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.clearList_add:
                mLayout.setVisibility(View.GONE);

                break;

            case R.id.tv_save:
                String amount = mSum.getText().toString();
                try { amount = amount.replaceAll(",", ""); }          // 금액의 쉼표(,) 제거 <- null 값을 받으면 에러가 나서 예외처리 사용.
                catch (Exception ignored) { }
                int int_amount = Integer.parseInt(amount);

                if (mDate.length() > 0 && mWay.length() > 0 && mSort.length() > 0 && mSum.length() > 0) {
                    if (callValue.equals("ListInAsset_modify")) {         // 기존 데이터 수정 - ListInAssetActivity 에서 왔을 때
                        // 변경 후&변경 전 데이터들 ↓
                        String initDivision = costAll.getDivision();
                        String changeDate = mDate.getText().toString(), initDate = costAll.getUseDate();
                        String changeWay = mWay.getText().toString(), initWay = costAll.getFK_wayName();
                        int changeAmount = Integer.parseInt(amount), initAmount = costAll.getAmount();
                        String changeContent = mBody.getText().toString(), initContent = costAll.getContent();

                        if(!action.equals(initDivision) || !changeDate.equals(initDate) || !changeWay.equals(initWay) ||
                                changeAmount != initAmount || !changeContent.equals(initContent)){        // 날짜/자산/금액/내용 중 하나라도 바뀌면 실행
                            if(action.equals(initDivision) && changeDate.equals(initDate) && changeWay.equals(initWay) &&
                                    changeAmount != initAmount && changeContent.equals(initContent)){     // 값만 변경시~

                                updateBalanceOnByDelete(initDate, changeWay, changeAmount, "onlyMoney");

                            }

                            else{           // 값만 변경을 제외한 모든 경우
                                updateBalanceOnByDelete(initDate, initWay, initAmount, "anything");

                                // "변경 후" 날짜 "이전"의 데이터 배열
                                List<Cost> preData_detail = db.dao().getNowPre_hard(changeDate, mBody.getText().toString(), myCostId, changeWay);
                                List<Cost> preData_today = db.dao().getNowPre_forChange(changeDate, mBody.getText().toString(), changeWay, myCostId);
                                List<Cost> preData = db.dao().getCostDataPre_forChange(changeDate, changeWay, myCostId);
                                preData_detail.addAll(preData_today);
                                preData_detail.addAll(preData);

                                // "변경 후" 날짜 "이후"의 데이터 배열
                                List<Cost> afterData_detail = db.dao().getNowAfter_hard(changeDate, mBody.getText().toString(), myCostId, changeWay);
                                List<Cost> afterData_today = db.dao().getNowAfter2_forChange(changeDate, mBody.getText().toString(), changeWay, myCostId);
                                List<Cost> afterData = db.dao().getCostDataAfter_forChange(changeDate, changeWay, myCostId);
                                afterData_detail.addAll(afterData_today);
                                afterData_detail.addAll(afterData);

                                int preCostId = -100, afterCostId = -100;
                                try { preCostId = preData_detail.get(0).getCostId(); }
                                catch (Exception ignored) { }
                                try { afterCostId = afterData_detail.get(0).getCostId(); }
                                catch (Exception ignored) { }

                                updateBalanceOnByNewData(afterData_detail, preCostId, afterCostId, changeAmount, changeWay, "change");
                            }
                        }

                        setResult(RESULT_OK);
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
                    else if (callValue.equals("nothing") || callValue.equals("ListInAsset_add") || callValue.equals("Main")) {       // 새로운 데이터 입력
                        List<Cost> preData_today = db.dao().getNowPre(mDate.getText().toString(), mBody.getText().toString(), mWay.getText().toString());
                        List<Cost> afterData_today = db.dao().getNowAfter(mDate.getText().toString(), mBody.getText().toString(), mWay.getText().toString());
                        List<Cost> preData = db.dao().getCostDataPre(mDate.getText().toString(), mWay.getText().toString());
                        List<Cost> afterData = db.dao().getCostDataAfter(mDate.getText().toString(), mWay.getText().toString());

                        preData_today.addAll(preData);
                        afterData_today.addAll(afterData);

                        String wayName = mWay.getText().toString();
                        int preCostId = -100, afterCostId = -100;

                        try { preCostId = preData_today.get(0).getCostId(); }
                        catch (Exception ignored) { }
                        try { afterCostId = afterData_today.get(0).getCostId(); }
                        catch (Exception ignored) { }

                        updateBalanceOnByNewData(afterData_today, preCostId, afterCostId, int_amount, wayName, "new");

                        if(callValue.equals("ListInAsset_add")){ setResult(RESULT_OK); }
                        finish();
                    }
                }
                else {
                    Toast.makeText(this, "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                bluetooth();
                break;

            case R.id.tv_delete:
                dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog);

                showDialog();

                break;


        }
    }



    private void updateBalanceOnByDelete(String date, String wayName, int amount, String flag){
        int money = 0;

        if(flag.equals("onlyMoney")){
            if(action.equals("income")){ money = amount - costAll.getAmount(); }
            else if(action.equals("expense")){ money = costAll.getAmount() - amount; }
            updateCostData(amount, costAll.getBalance()+money);
        }
        else if(flag.equals("anything") || flag.equals("delete")){
            if(costAll.getDivision().equals("income")){ money = -amount; }
            else if(costAll.getDivision().equals("expense")){ money = amount; }

            if(flag.equals("delete")){ db.dao().deleteCostData(myCostId); }
        }

        List<Cost> afterData_detail = db.dao().getNowAfter_hard(date, mBody.getText().toString(), myCostId, wayName);
        List<Cost> afterData_today = db.dao().getNowAfter2_forChange(date, mBody.getText().toString(), wayName, myCostId);
        List<Cost> afterData = db.dao().getCostDataAfter_forChange(date, wayName, myCostId);
        afterData_detail.addAll(afterData_today);
        afterData_detail.addAll(afterData);

        if(afterData_detail.size() > 0){
            for (int i = 0; i < afterData_detail.size(); i++) {
                if (i < afterData_detail.size() - 1) {
                    db.dao().update_NextCostBal(money, afterData_detail.get(i).getCostId());
                }
                else if (i == afterData_detail.size() - 1) {
                    db.dao().update_NextCostBal(money, afterData_detail.get(i).getCostId());
                    int n = db.dao().getCostBalance(afterData_detail.get(i).getCostId());
                    db.dao().updateWayBal(n, wayName);
                }
            }
        }
        else{
            db.dao().updateWayBal2(money, wayName);
        }

    }

    

    private void updateBalanceOnByNewData(List<Cost> afterData, int preCostId, int afterCostId, int int_amount, String wayName, String flag) {
        int myBalance = -1;

        if (preCostId != -100 && afterCostId == -100) {   // 최신 데이터(이전 데이터 o, 다음 데이터 x)
            Log.d("LEEhj_add", "here1_최신 데이터(이전 데이터 o, 다음 데이터 x)");
            if (action.equals("income")) { myBalance = db.dao().getCostBalance(preCostId) + int_amount; }  // 바로 이전 행의 잔액으로 현재 잔액을 계산
            else if (action.equals("expense")) { myBalance = db.dao().getCostBalance(preCostId) - int_amount; }

            if(flag.equals("new")){ insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, myBalance); }
            else if(flag.equals("change")){ updateCostData(int_amount, myBalance); }

            db.dao().updateWayBal(myBalance, wayName);
        }

        else if (preCostId != -100) {       // 기존 데이터 사이(이전 데이터 o, 다음 데이터 o)
            Log.d("LEEhj_add", "here2_기존 데이터 사이(이전 데이터 o, 다음 데이터 o)");
            if (action.equals("income")) { myBalance = db.dao().getCostBalance(preCostId) + int_amount; }  // 바로 이전 행의 잔액으로 현재 잔액을 계산
            else if (action.equals("expense")) { myBalance = db.dao().getCostBalance(preCostId) - int_amount; }

            if(flag.equals("new")){ insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, myBalance); }
            else if(flag.equals("change")){ updateCostData(int_amount, myBalance); }

            if(action.equals("expense")) { int_amount = -int_amount; }      // 지출이면 이후 잔액들에 금액만큼 (-)를 해야함.
            for (int i = 0; i < afterData.size(); i++) {
                if (i < afterData.size() - 1) {
                    db.dao().update_NextCostBal(int_amount, afterData.get(i).getCostId());
                }
                else if (i == afterData.size() - 1) {
                    db.dao().update_NextCostBal(int_amount, afterData.get(i).getCostId());
                    int n = db.dao().getCostBalance(afterData.get(i).getCostId());
                    db.dao().updateWayBal(n, wayName);
                }
            }
        }

        else if (afterCostId != -100) {       // 첫 데이터(이전 데이터 x, 다음 데이터 o)
            Log.d("LEEhj_add", "here3_첫 데이터(이전 데이터 x, 다음 데이터 o)");
            myBalance = db.dao().getCostBalance(afterCostId);       // 바로 앞 데이터의 잔액
            int amt = db.dao().getCostAmount(afterCostId);          // 바로 앞 데이터의 금액
            String dvs = db.dao().getCostDivision(afterCostId);     // 바로 앞 데이터의 구분

            if(dvs.equals("income")) { amt = -amt; }
            myBalance += amt;

            if (action.equals("income")) { myBalance += int_amount; }       // 현재 나의 잔액 구하는 부분
            else if (action.equals("expense")) { myBalance -= int_amount; }

            if(flag.equals("new")){ insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, myBalance); }
            else if(flag.equals("change")){ updateCostData(int_amount, myBalance); }

            if(action.equals("expense")) { int_amount = -int_amount; }      // 지출이면 이후 잔액들에 금액만큼 (-)를 해야함.
            for (int i = 0; i < afterData.size(); i++) {
                if (i < afterData.size() - 1) {
                    db.dao().update_NextCostBal(int_amount, afterData.get(i).getCostId());
                }
                else if (i == afterData.size() - 1) {
                    db.dao().update_NextCostBal(int_amount, afterData.get(i).getCostId());
                    int n = db.dao().getCostBalance(afterData.get(i).getCostId());
                    db.dao().updateWayBal(n, wayName);
                }
            }
        }

        else {      // 초기 데이터(이전 데이터 x, 다음 데이터 x)
            Log.d("LEEhj_add", "here4_초기 데이터(이전 데이터 x, 다음 데이터 x)");
            if (action.equals("income")) { myBalance = db.dao().getWayBalance(wayName) + int_amount; }
            else if (action.equals("expense")) { myBalance = db.dao().getWayBalance(wayName) - int_amount; }

            if(flag.equals("new")){ insertDataToCostTable(mDate.getText().toString(), wayName, int_amount, myBalance); }
            else if(flag.equals("change")){ updateCostData(int_amount, myBalance); }

            db.dao().updateWayBal(myBalance, wayName);
        }
    }


    private void insertDataToCostTable(String date, String wayName, int amount, int balance) {
        db.dao().insertCost(
                date,                                   // 날짜
                wayName,                                // 수단
                mSort.getText().toString(),             // 분류
                amount,                                 // 금액
                mBody.getText().toString(),             // 내용
                balance,                                // 잔액
                action,                                 // 구분
                ms                                      // 수신시간(마이크로초)
        );
    }


    private void updateCostData(int amount, int balance){
        db.dao().update_CostData(
                mDate.getText().toString(),         // 날짜
                mWay.getText().toString(),          // 수단명
                mSort.getText().toString(),         // 분류명
                amount,                             // 금액
                balance,                            // 잔액
                mBody.getText().toString(),         // 내용
                action,                             // 구분(수입/지출)
                costAll.getMs(),                    // ms
                myCostId                            // 업데이트 행의 costId
        );
    }



    private void showDialog(){
        dialog.show();

        TextView mCancel, mAccept;
        mCancel = dialog.findViewById(R.id.tv_cancel);
        mAccept = dialog.findViewById(R.id.tv_accept);
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
                updateBalanceOnByDelete(costAll.getUseDate(), costAll.getFK_wayName(), costAll.getAmount(), "delete");
                bluetooth();
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    @Override
    public void clickItem(String itemName) {
        if (focus.equals("way")) {
            mWay.setText(itemName);
        } else if (focus.equals("sort")) {
            mSort.setText(itemName);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                setWayAndSortRV(focus);
            }
        }
    }



    @Override
    public void onBackPressed() {
        if(mLayout.getVisibility() == View.VISIBLE){
            mLayout.setVisibility(View.GONE);
        }
        else{
            super.onBackPressed();
        }
    }




    public final static class NumberTextWatcher implements TextWatcher {
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



    class SendThread extends Thread {
        String path;
        String message;

        //constructor
        SendThread(String p, String msg) {
            path = p;
            message = msg;
        }

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, no problem.
        public void run() {

            //first get all the nodes, ie connected wearable devices.
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                List<Node> nodes = Tasks.await(nodeListTask);

                //Now send the message to each device.
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(AddActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        Integer result = Tasks.await(sendMessageTask);



                    } catch (ExecutionException exception) {


                    } catch (InterruptedException exception) {

                    }

                }

            } catch (ExecutionException exception) {



            } catch (InterruptedException exception) {

            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {      // LocalDate 형식(YYYY-MM-DD)의 데이터를 '----년 --월' 형식으로 변환하는 함수
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY년 MM월");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return date.format(formatter);
    }



    void bluetooth(){
        if (db.dao().getAmount(monthYearFromDate(selectedDate))!=null){
            new SendThread("/message_path", db.dao().getAmount(monthYearFromDate(selectedDate))+"원").start();
        }
        else{
            new SendThread("/message_path", "0원").start();
        }
    }



}