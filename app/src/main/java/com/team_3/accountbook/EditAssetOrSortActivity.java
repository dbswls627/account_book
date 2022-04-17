package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class EditAssetOrSortActivity extends AppCompatActivity implements AssetInAdapter.OnItemClickInAssetAc{
    private BottomNavigationView mBottom_menu;
    private TextView mAssetOrSortSetting, mIncome, mExpense;
    private RecyclerView mAssetRV;
    private AppDatabase db;
    private String forWhat;
    private LinearLayout layout;
    private String action = "expense";
    private Boolean checkIncome = false, checkExpense = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_asset_or_sort);

        mAssetOrSortSetting = findViewById(R.id.assetOrSortSetting);
        mAssetRV = findViewById(R.id.rv_Asset);
        db = AppDatabase.getInstance(this);
        layout = findViewById(R.id.button_layout_setting);
        mIncome = findViewById(R.id.tv_income_setting);
        mExpense = findViewById(R.id.tv_expense_setting);

        Intent intent = getIntent();
        forWhat = intent.getStringExtra("forWhat");     // 자산설정 or 분류설정 구분자
        if(forWhat.equals("asset")){
            mAssetOrSortSetting.setText("자산설정");
            layout.setVisibility(View.GONE);
        }
        if(forWhat.equals("sort")){ mAssetOrSortSetting.setText("분류설정"); }
        setColorOfDivision("expense");

        settingBottomMenu();
        setRV();
    }

    private void setColorOfDivision(String division) {
        if (division.equals("income")) {
            action = "income";
            setColorOfTheme(action);

            checkIncome = true;
            checkExpense = false;

            mIncome.setSelected(true);
            mExpense.setSelected(false);
            mIncome.setTextColor(getResources().getColor(R.color.hardGreen));           // 초록색
            mExpense.setTextColor(getResources().getColor(R.color.grayForText));    // 진회색
        }
        else if (division.equals("expense")) {
            action = "expense";
            setColorOfTheme(action);

            checkIncome = false;
            checkExpense = true;

            mIncome.setSelected(false);
            mExpense.setSelected(true);
            mIncome.setTextColor(getResources().getColor(R.color.grayForText));     // 진회색
            mExpense.setTextColor(getResources().getColor(R.color.red));            // 빨간색
        }
    }

    private void setColorOfTheme(String actionFlag) {

        if (actionFlag.equals("income")) {
            setTheme(R.style.editText_income);
        } else if (actionFlag.equals("expense")) {
            setTheme(R.style.editText_expense);
        }
    }

    private void setRV(){
        if(forWhat.equals("asset")){
            List<String> assetNameList = db.dao().getAssetName();

            mAssetRV.setAdapter(new AssetInAdapter(assetNameList, this, this));
            mAssetRV.setLayoutManager(new LinearLayoutManager(this));
        }

        if(forWhat.equals("sort")){
            List<String> sortNameList = db.dao().getSortNames(action);

            mAssetRV.setAdapter(new AssetInAdapter(sortNameList, this, this));
            mAssetRV.setLayoutManager(new LinearLayoutManager(this));
        }
    }



    private void settingBottomMenu(){
        mBottom_menu = findViewById(R.id.bottom_menu);
        mBottom_menu.setSelectedItemId(R.id.setting);
        mBottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {
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
    }



    @SuppressWarnings("deprecation")
    @SuppressLint("NonConstantResourceId")
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_editAssetOrSort:
                finish();
                overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.addItem:
                Intent intent = new Intent(this, EditItemNameActivity.class);
                if(forWhat.equals("asset")) {
                    intent.putExtra("flag", "new_assetName");
                }
                if(forWhat.equals("sort")){
                    intent.putExtra("flag", "new_sortName");
                }
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.bottom_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.tv_income_setting:
                Log.d("income", "click");
                if (!checkIncome) {
                    setColorOfDivision("income");
                }
                setRV();
                break;

            case R.id.tv_expense_setting:
                Log.d("expense", "click");
                if (!checkExpense) {
                    setColorOfDivision("expense");
                }
                setRV();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                setRV();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void listItemClick(String assetName, String doFlag, ImageView imageView, ImageView imageView_final) {
        if(doFlag.equals("click")){
            Intent intent = new Intent(this, EditItemNameActivity.class);
            if(forWhat.equals("asset")) {
                intent.putExtra("itemName", assetName);
                intent.putExtra("flag", "modify_assetName");
            }
            if(forWhat.equals("sort")){
                intent.putExtra("itemName", assetName);
                intent.putExtra("flag", "modify_sortName");
            }
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else if(doFlag.equals("delete")){
            imageView.setVisibility(View.GONE);
            imageView_final.setVisibility(View.VISIBLE);
//            imageView.setImageResource(R.drawable.ic_baseline_close_24);
//            imageView.getLayoutParams().height = 110;
//            imageView.getLayoutParams().width = 110;
//            imageView.requestLayout();

            ObjectAnimator ani = ObjectAnimator.ofFloat(imageView_final, "translationX", 130f, 0f);
            ani.setDuration(300);
            ani.start();

            imageView_final.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(forWhat.equals("asset")) {
                        db.dao().deleteAsset(assetName);
                    }
                    if(forWhat.equals("sort")){
                        db.dao().deleteSort(assetName);
                    }
                    setRV();
                }
            });
        }
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }



}