package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EditItemNameActivity extends AppCompatActivity {
    private TextView mAssetOrSort, mSave, mExpense, mIncome;
    private EditText mItemName;
    private AppDatabase db;
    private String itemName;
    private String flag;
    private String action = "expense", actionKorean = "지출";
    private LinearLayout mInExLayout;
    private boolean checkIncome = false, checkExpense = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_name);

        mAssetOrSort = findViewById(R.id.assetOrSort);

        mInExLayout = findViewById(R.id.button_layout_add);
        mIncome = findViewById(R.id.tv_income_add);
        mExpense = findViewById(R.id.tv_expense_add);
        mItemName = findViewById(R.id.name_editItemName);
        mSave = findViewById(R.id.save_editItemName);

        db = AppDatabase.getInstance(this);

        itemName = getIntent().getStringExtra("itemName");
        flag = getIntent().getStringExtra("flag");


        if(flag.equals("modify_assetName")){ mAssetOrSort.setText("자산 수정");
            mInExLayout.setVisibility(View.GONE);
        }
        else if(flag.equals("new_assetName")){
            mAssetOrSort.setText("자산 추가");
            mInExLayout.setVisibility(View.GONE);
        }
        else if(flag.equals("modify_sortName")) {
            mAssetOrSort.setText("분류 수정");
            if(db.dao().getSortDivision(mItemName.getText().toString()).equals("income")){
                mExpense.setEnabled(false);
            }
            else if(db.dao().getSortDivision(mItemName.getText().toString()).equals("expense")){
                mIncome.setEnabled(false);
            }
            setColorOfDivision(db.dao().getSortDivision(mItemName.getText().toString()));
        }
        else if(flag.equals("new_sortName")){
            mAssetOrSort.setText("분류 추가");
            setColorOfDivision("expense");
        }
        mItemName.setText(itemName);

    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_editItemName:
                setResult(RESULT_OK);
                finish();
                if(flag.equals("modify_assetName")){
                    overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }
                else if(flag.equals("new_assetName")){
                    overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }
                else if(flag.equals("modify_sortName")){
                    overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }
                else if(flag.equals("new_sortName")){
                    overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }

                break;

            case R.id.tv_income_add:
                Log.d("income", "click");
                if (!checkIncome) {
                    setColorOfDivision("income");
                }
                break;

            case R.id.tv_expense_add:
                Log.d("expense", "click");
                if (!checkExpense) {
                    setColorOfDivision("expense");
                }
                break;

            case R.id.save_editItemName:
                if(mItemName.getText().length() > 0){
                    List<String> assetName = db.dao().getAssetName();
                    List<String> sortName = db.dao().getSortName();
                    boolean notExist = true;

                    if( flag.equals("modify_assetName") || flag.equals("new_assetName") ){

                        for (String str : assetName) {
                            if (str.equals(mItemName.getText().toString())) {
                                notExist = false;
                            }
                        }

                        if (notExist) {       // 존재하지 않는 이름이면 실행
                            if (flag.equals("modify_assetName")) {
                                db.dao().updateAsset(mItemName.getText().toString(), itemName);
                                closeAnimation();
                            } else if (flag.equals("new_assetName")) {
                                db.dao().insertAsset(mItemName.getText().toString());
                                closeAnimation();
                            }
                        }
                        else {
                            Toast.makeText(this, "중복된 자산명은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if( flag.equals("modify_sortName") || flag.equals("new_sortName") ){

                        for (String str : sortName) {
                            if (str.equals(mItemName.getText().toString())) {
                                notExist = false;
                            }
                        }

                        if (notExist) {       // 존재하지 않는 이름이면 실행
                            if (flag.equals("modify_sortName")) {
                                db.dao().updateSort(mItemName.getText().toString(), itemName);
                                closeAnimation();
                            } else if (flag.equals("new_sortName")) {
                                db.dao().insertSortName(mItemName.getText().toString(), action);
                                closeAnimation();
                            }
                        } else {
                            Toast.makeText(this, "중복된 분류명은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(this, "한글자 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }




    private void closeAnimation(){
        setResult(RESULT_OK);
        finish();
        if(flag.equals("modify_assetName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else if(flag.equals("new_assetName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else if(flag.equals("new_sortName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else if(flag.equals("modify_sortName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
    }



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
            mItemName.setText("");
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
            mItemName.setText("");
        }
    }



    private void setColorOfTheme(String actionFlag) {

        if (actionFlag.equals("income")) {
            setTheme(R.style.editText_income);
        } else if (actionFlag.equals("expense")) {
            setTheme(R.style.editText_expense);
        }
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

        closeAnimation();
    }
}