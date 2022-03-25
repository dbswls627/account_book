package com.team_3.accountbook;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ListInAssetActivity extends AppCompatActivity{
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");
    private FloatingActionButton mFabAdd, mFabReWrite, mFabMain;
    private boolean isFabOpen = false;
    private long now = 0;
    private String backFlag = "";
    ArrayList<String> dateList = new ArrayList<>();
    RecyclerView mRV_listInAsset;
    adapter2 adapter2;

    LinearLayout mTotalIncome, mLayoutForAuto, mLayoutNoData;
    TextView mTopWayName, mDuration, mNowMonth, mIncomeTotal, mExpenseTotal;
    LocalDate localDate;
    String formatAmount = "";

    AppDatabase db;

    String wayName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_in_asset);

        mTopWayName = findViewById(R.id.listInAsset_wayName);
        mDuration = findViewById(R.id.duration);
        mIncomeTotal = findViewById(R.id.income_total);
        mExpenseTotal = findViewById(R.id.expense_total);
        mTotalIncome = findViewById(R.id.totalIncome_ListInAsset);
        mLayoutForAuto = findViewById(R.id.layout_notiForAuto);
        mLayoutNoData = findViewById(R.id.layout_noData);
        mRV_listInAsset = findViewById(R.id.rv_listInAsset);
        mNowMonth = findViewById(R.id.nowMonth);
        mFabAdd = findViewById(R.id.fab_add2);
        mFabReWrite = findViewById(R.id.fab_reWrite);
        mFabMain = findViewById(R.id.fab_main);

        db = AppDatabase.getInstance(this);

        mLayoutNoData.setVisibility(View.GONE);
        localDate = LocalDate.now();
        now = System.currentTimeMillis();

        wayName = getIntent().getStringExtra("wayName");
        if(wayName.equals(getResources().getString(R.string.auto_wayName))){
            mTotalIncome.setVisibility(View.GONE);
            mFabMain.setVisibility(View.GONE);
            mFabReWrite.setVisibility(View.GONE);
            mFabAdd.setVisibility(View.GONE);
        }
        else{
            mLayoutForAuto.setVisibility(View.GONE);
        }

        setMonthList();
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthList(){
        mTopWayName.setText(wayName);

        LocalDate fistDate = localDate.withDayOfMonth(1);                           // 월의 첫째 날
        LocalDate lastDate = localDate.withDayOfMonth(localDate.lengthOfMonth());   // 월의 마지막 날
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        if(!fistDate.format(DateTimeFormatter.ofPattern("yyyy")).equals(sdf.format(now))){      // 현재 년도랑 이동한 달의 년도랑 다르면~
            mDuration.setText(fistDate.format(DateTimeFormatter.ofPattern("yy.MM.dd")) + " ~ " + lastDate.format(DateTimeFormatter.ofPattern("yy.MM.dd")));
        }
        else {
            mDuration.setText(fistDate.format(DateTimeFormatter.ofPattern("MM.dd")) + " ~ " + lastDate.format(DateTimeFormatter.ofPattern("MM.dd")));
        }

        mNowMonth.setText(monthFromLocalDate(localDate));

        int totalAmount = 0;

        try {
            totalAmount = db.dao().getAmountOfMonth(mNowMonth.getText().toString(), wayName, "income");
            formatAmount = myFormatter.format(totalAmount);
            mIncomeTotal.setText(formatAmount);
        }
        catch (Exception e){ mIncomeTotal.setText(totalAmount+""); }

        try {
            totalAmount = db.dao().getAmountOfMonth(mNowMonth.getText().toString(), wayName, "expense");
            formatAmount = myFormatter.format(totalAmount);
            mExpenseTotal.setText(formatAmount);
        }
        catch (Exception e){ mExpenseTotal.setText(totalAmount+""); }

        // RecyclerView
        List<Cost> costList = db.dao().getCostOfMonthAndWayName(mNowMonth.getText().toString(), wayName);
        if(costList.size() == 0){
            mLayoutNoData.setVisibility(View.VISIBLE);
            mRV_listInAsset.setVisibility(View.GONE);
        }
        else{
            mLayoutNoData.setVisibility(View.GONE);
            mRV_listInAsset.setVisibility(View.VISIBLE);

            dateList.clear();
            for (int i = 0; i < costList.size(); i++) {
                if(!dateList.contains(costList.get(i).getUseDate().substring(0, 14))){
                    dateList.add(costList.get(i).getUseDate().substring(0, 14));
                }
            }

            adapter2 = new adapter2(this, (ArrayList<Cost>) costList, dateList);
            mRV_listInAsset.setAdapter(adapter2);
            mRV_listInAsset.setLayoutManager(new LinearLayoutManager(this));
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthFromLocalDate(LocalDate ld){        // LocalDate 형식(YYYY-MM-DD)의 데이터를 '----년 --월' 형식으로 변환하는 함수
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return ld.format(formatter);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_listInAsset:
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.fab_main:
                toggleFab();

                break;

            case R.id.fab_add2:
                Intent intent = new Intent(this, AddActivity.class);
                intent.putExtra("wayName", wayName);
                intent.putExtra("flag", "ListInAsset_add");
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.bottom_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.fab_reWrite:
                Intent intent2 = new Intent(this, EditWayActivity.class);
                intent2.putExtra("wayName", wayName);
                intent2.putExtra("flag", "modify_LIA");
                startActivityForResult(intent2, 0);
                overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.toPreMonth:
                localDate = localDate.minusMonths(1);
                setMonthList();

                break;

            case R.id.toNextMonth:
                localDate = localDate.plusMonths(1);
                setMonthList();

                break;
        }
    }


    private void toggleFab() {
        if (isFabOpen) {
            mFabMain.setImageResource(R.drawable.ic_baseline_list);
            mFabMain.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));

            ObjectAnimator ani = ObjectAnimator.ofFloat(mFabAdd, "translationY", 0f);
            ani.start();
            ani = ObjectAnimator.ofFloat(mFabReWrite, "translationY", 0f);
            ani.start();

            isFabOpen = false;

        } else {
            mFabMain.setImageResource(R.drawable.ic_baseline_clear_24);
            mFabMain.setColorFilter(getResources().getColor(R.color.black));            // 'X' 아이콘 색상을 검정으로 set
            mFabMain.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightGray)));

            ObjectAnimator ani = ObjectAnimator.ofFloat(mFabAdd, "translationY", -190f);
            ani.start();
            ani = ObjectAnimator.ofFloat(mFabReWrite, "translationY", -360f);
            ani.start();

            isFabOpen = true;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                if (data != null) {         // ListInAsset 에서 Way 편진하러 간 후 Way 를 삭제했을 때
                    wayName = data.getStringExtra("wayName");
                    backFlag = data.getStringExtra("backFlag");
                }

                if(backFlag.equals("double")){
                    setResult(RESULT_OK);
                    finish();
                }
                else{
                    setMonthList();
                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }
}


