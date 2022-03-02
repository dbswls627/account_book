package com.team_3.accountbook;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ListInAssetActivity extends AppCompatActivity implements adapter.OnItemClickInListInAsset{
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");
    private FloatingActionButton mFabAdd, mFabReWrite, mFabMain;
    private boolean isFabOpen = false;
    ArrayList<String> dateList = new ArrayList<>();
    RecyclerView mRV_listInAsset;
    adapter2 adapter2;

    LinearLayout mLayoutNoData;
    TextView mNowMonth, mIncomeTotal, mExpenseTotal;
    LocalDate localDate;
    String formatAmount = "";

    AppDatabase db;

    String wayName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_in_asset);

        mIncomeTotal = findViewById(R.id.income_total);
        mExpenseTotal = findViewById(R.id.expense_total);
        mLayoutNoData = findViewById(R.id.layout_noData);
        mRV_listInAsset = findViewById(R.id.rv_listInAsset);
        mNowMonth = findViewById(R.id.nowMonth);
        mFabAdd = findViewById(R.id.fab_add2);
        mFabReWrite = findViewById(R.id.fab_reWrite);
        mFabMain = findViewById(R.id.fab_main);

        db = AppDatabase.getInstance(this);

        mLayoutNoData.setVisibility(View.GONE);
        localDate = LocalDate.now();

        wayName = getIntent().getStringExtra("wayName");
        setMonthList();
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthList(){
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY년 MM월");   // 변환 형식 formatter 구축. (MMMM: 01월, MM: 01)
        return ld.format(formatter);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.fab_main:
                toggleFab();

                break;

            case R.id.fab_add2:
                Intent intent = new Intent(this, AddActivity.class);
                intent.putExtra("wayName", wayName);
                intent.putExtra("flag", "ListInAsset_add");
                startActivityForResult(intent, 0);

                break;

            case R.id.fab_reWrite:
                Toast.makeText(this, "Way 편집으로 이동 예정", Toast.LENGTH_SHORT).show();

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
            mFabMain.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightGray)));

            ObjectAnimator ani = ObjectAnimator.ofFloat(mFabAdd, "translationY", -190f);
            ani.start();
            ani = ObjectAnimator.ofFloat(mFabReWrite, "translationY", -360f);
            ani.start();

            isFabOpen = true;

        }

    }



    @Override
    public void onClick(Cost cost) {
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra("costId", cost.getCostId());
        intent.putExtra("flag", "ListInAsset_modify");
        startActivityForResult(intent, 0);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                setMonthList();      // adapter2.NotifyDataSetChanged();를 해도 새로고침이 안되어있어 그냥 다시 연결해버림.
            }
        }
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}


