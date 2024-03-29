package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class EditWayActivity extends AppCompatActivity implements WayAndSortAdapter.touchItem{
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");
    private ImageView mDelete;
    private TextView mWayName_top, mInfo;
    private EditText mAssetName, mWayName, mBalance, mMemo, mPhoneNumber, mDelimiter;
    private LinearLayout mLayout;
    private String assetName, wayName, formatBalance, flag = "", division = "income";
    private RecyclerView mRV;
    private int balance, gap = 0;
    private AppDatabase db;
    private InputMethodManager imm;

    private Dialog dialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_way);

        db = AppDatabase.getInstance(this);

        mDelete = findViewById(R.id.delete_wayData);
        mWayName_top = findViewById(R.id.wayName_editWay);
        mInfo = findViewById(R.id.info);
        mAssetName = findViewById(R.id.editWay_assetName);
        mWayName = findViewById(R.id.editWay_wayName);
        mBalance = findViewById(R.id.editWay_balance);
        mMemo = findViewById(R.id.editWay_memo);
        mPhoneNumber = findViewById(R.id.editWay_phoneNumber);
        mDelimiter = findViewById(R.id.editWay_delimiter);
        mLayout = findViewById(R.id.layout_editWayBottom);
        mRV = findViewById(R.id.rv_editWay);

        mLayout.setVisibility(View.GONE);

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if(flag.equals("modify_LIA") || flag.equals("modify_AFE")){
            wayName = intent.getStringExtra("wayName");
            mWayName_top.setText(wayName);
            Way wayData = db.dao().getWayData(wayName);

            assetName = db.dao().getAssetName(wayData.getFK_assetId());
            balance = wayData.getWayBalance();
            formatBalance = myFormatter.format(balance);

            mAssetName.setText(assetName);
            mWayName.setText(wayName);
            mBalance.setText(formatBalance + "");
            mMemo.setText(wayData.getWayMemo());
            mPhoneNumber.setText(wayData.getPhoneNumber());
            mDelimiter.setText(wayData.getDelimiter());
        }
        else if(flag.equals("new")){
            mWayName_top.setText("수단");
            mInfo.setText("추가");
            mDelete.setVisibility(View.GONE);
        }

        mBalance.addTextChangedListener(new AddActivity.NumberTextWatcher(mBalance));

        touchEvent();


    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_editWay:
                finish();
                fadeOutActivity();

                break;

            case R.id.delete_wayData:
                dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog);

                showDialog();

                break;

            case R.id.clearList_editWay:
                mLayout.setVisibility(View.GONE);

                break;

            case R.id.tv_save_editWay:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_editway_modifybalance);

                long now = System.currentTimeMillis();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm");
                String processedNow = sdf.format(now);

                String s = mBalance.getText().toString();
                try { s = s.replaceAll(",", ""); }          // 금액의 쉼표(,) 제거 <- null 값을 받으면 에러가 나서 예외처리 사용.
                catch (Exception ignored) { }
                int int_balance = 0;
                try { int_balance = Integer.parseInt(s); }
                catch (Exception e){
                    if(!mBalance.getText().toString().isEmpty()){
                        Toast.makeText(this, "잔액에 숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if(!mAssetName.getText().toString().isEmpty() && !mWayName.getText().toString().isEmpty()){
                    int FK_assetId = db.dao().getAssetId(mAssetName.getText().toString());
                    boolean modifyBalance = true;

                    try {
                        if(flag.equals("modify_LIA") || flag.equals("modify_AFE")){      // Way 수정시
                            if(mWayName.getText().toString().equals("(Auto)")){
                                Toast.makeText(this, "수단명 '(Auto)'는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            }

                            if(balance != int_balance){     // 수단 잔액이 변경됐을 때~
                                modifyBalance = false;
                                gap = int_balance - balance;
                                if(gap < 0){            // 기존 수단 잔액보다 작은 금액으로 수정한 경우
                                    gap = -gap;
                                    division = "expense";
                                }

                                showDialogOfReflection(dialog, processedNow, gap, int_balance, FK_assetId);
                            }

                            if(modifyBalance){
                                db.dao().updateWayData(
                                        FK_assetId,
                                        mWayName.getText().toString(),
                                        int_balance,
                                        mMemo.getText().toString(),
                                        mPhoneNumber.getText().toString(),
                                        mDelimiter.getText().toString(),
                                        wayName
                                );
                                if(!wayName.equals(mWayName.getText().toString())){     // 수단명 변경시 수단명 update
                                    db.dao().updateCostWayName(wayName, mWayName.getText().toString());
                                }

                                finishForResult();
                                fadeOutActivity();
                            }
                        }

                        else if(flag.equals("new")){    // Way 추가시
                            if(mWayName.getText().toString().equals("(Auto)")){
                                Toast.makeText(this, "수단명 '(Auto)'는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            }

                            try {
                                if(Integer.parseInt(mBalance.getText().toString()) != 0){
                                    modifyBalance = false;
                                    showDialogOfReflection(dialog, processedNow, int_balance, int_balance, FK_assetId);
                                }
                            }
                            catch (Exception ignore){  }

                            if(modifyBalance){      // 새로운 수단 입력시 잔액이 null||0이 아닐 때 실행
                                db.dao().insertWayAll(
                                        mWayName.getText().toString(),
                                        int_balance,
                                        FK_assetId,
                                        mMemo.getText().toString(),
                                        mPhoneNumber.getText().toString(),
                                        mDelimiter.getText().toString()
                                );
                                finishForResult();
                                fadeOutActivity();
                            }
                        }

                    }
                    catch (Exception e){
                        Toast.makeText(this, "중복된 수단명은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "자산과 수단명은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }



    private void showDialog(){
        dialog.show();

        TextView mMessage, mCancel, mAccept;

        mMessage = dialog.findViewById(R.id.deleteMassage);
        mMessage.setText("수단 정보가 사라집니다.");

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
                db.dao().deleteWayData(wayName);

                flag = "modify_LIA_delete";
                finishForResult();
                fadeOutActivity();
            }
        });
    }



    @SuppressWarnings("IfStatementWithIdenticalBranches")
    private void finishForResult(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("wayName", mWayName.getText().toString());
        if(flag.equals("modify_LIA_delete")){
            resultIntent.putExtra("backFlag", "double");
            setResult(RESULT_OK, resultIntent);
        }
        else{
            resultIntent.putExtra("backFlag", "one");
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }



    @SuppressLint("ClickableViewAccessibility")
    private void touchEvent(){
        mAssetName.setInputType(InputType.TYPE_NULL);         // 클릭시 키보드 안올라오게 함.
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   //키보드 내리기 할 때 필요한 객체


        mAssetName.setOnTouchListener((view, motionEvent) -> {
            imm.hideSoftInputFromWindow(mAssetName.getWindowToken(), 0);      // 키보드 내리기
            mLayout.setVisibility(View.VISIBLE);                        // 상단바 보이기

            List<String> assetName = db.dao().getAssetNameAll();
            assetName.remove(getResources().getString(R.string.auto_assetName));    // 자산명 '자동저장' 제거
            mRV.setAdapter(new WayAndSortAdapter(assetName, this));
            mRV.setLayoutManager(new LinearLayoutManager(this));
            mRV.setVisibility(View.VISIBLE);

            return false;
        });
        mWayName.setOnTouchListener((view, motionEvent) -> {
            mLayout.setVisibility(View.GONE);

            return false;
        });
        mBalance.setOnTouchListener((view, motionEvent) -> {
            mLayout.setVisibility(View.GONE);

            return false;
        });
        mMemo.setOnTouchListener((view, motionEvent) -> {
            mLayout.setVisibility(View.GONE);

            return false;
        });
        mPhoneNumber.setOnTouchListener((view, motionEvent) -> {
            mLayout.setVisibility(View.GONE);

            return false;
        });
        mDelimiter.setOnTouchListener((view, motionEvent) -> {
            mLayout.setVisibility(View.GONE);

            return false;
        });
    }



    @SuppressLint("SetTextI18n")
    private void showDialogOfReflection(Dialog dialog, String processedNow, int gap, int int_balance, int FK_assetId){
        dialog.show();

        TextView mTitle, mInOrEx, mRefuse, mAccept;

        mTitle = dialog.findViewById(R.id.InOrEx_title);
        mInOrEx = dialog.findViewById(R.id.InOrEx);
        mRefuse = dialog.findViewById(R.id.refuse_editWay);
        mAccept = dialog.findViewById(R.id.accept_editWay);

        if(division.equals("income")) {
            mTitle.setText("수입 반영");
            mInOrEx.setText("수입");
        }
        else if(division.equals("expense")){
            mTitle.setText("지출 반영");
            mInOrEx.setText("지출");
        }

        mRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.dao().insertCost(
                        processedNow,       // 날짜
                        mWayName.getText().toString(),  // 수단
                        "잔액수정",           // 분류
                        gap,                // 금액
                        "차액",              // 내용
                        int_balance,        // 잔액
                        division,           // 구분
                        0,                  // 문자 수신 마이크로초
                        false,              // 목표금액 반영 여부  ※false 고정
                        false               // 수입or지출 반영 여부
                );
                if(flag.equals("modify_LIA") || flag.equals("modify_AFE")){
                    db.dao().updateWayData(
                            FK_assetId,
                            mWayName.getText().toString(),
                            int_balance,
                            mMemo.getText().toString(),
                            mPhoneNumber.getText().toString(),
                            mDelimiter.getText().toString(),
                            wayName
                    );
                    if(!wayName.equals(mWayName.getText().toString())){     // 수단명 변경시 수단명 update
                        db.dao().updateCostWayName(wayName, mWayName.getText().toString());
                    }
                }
                else if(flag.equals("new")){
                    db.dao().insertWayAll(
                            mWayName.getText().toString(),
                            int_balance,
                            FK_assetId,
                            mMemo.getText().toString(),
                            mPhoneNumber.getText().toString(),
                            mDelimiter.getText().toString()
                    );
                }
                dialog.dismiss();

                finishForResult();
                fadeOutActivity();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.dao().insertCost(
                        processedNow,       // 날짜
                        mWayName.getText().toString(),  // 수단
                        "잔액수정",           // 분류
                        gap,                // 금액
                        "차액",              // 내용
                        int_balance,        // 잔액
                        division,           // 구분
                        0,                  // 문자 수신 마이크로초
                        false,              // 목표금액 반영 여부  ※false 고정
                        true                // 수입or지출 반영 여부
                );
                if(flag.equals("modify_LIA") || flag.equals("modify_AFE")){
                    db.dao().updateWayData(
                            FK_assetId,
                            mWayName.getText().toString(),
                            int_balance,
                            mMemo.getText().toString(),
                            mPhoneNumber.getText().toString(),
                            mDelimiter.getText().toString(),
                            wayName
                    );
                    if(!wayName.equals(mWayName.getText().toString())){     // 수단명 변경시 수단명 update
                        db.dao().updateCostWayName(wayName, mWayName.getText().toString());
                    }
                }
                else if(flag.equals("new")){
                    db.dao().insertWayAll(
                            mWayName.getText().toString(),
                            int_balance,
                            FK_assetId,
                            mMemo.getText().toString(),
                            mPhoneNumber.getText().toString(),
                            mDelimiter.getText().toString()
                    );
                }

                dialog.dismiss();

                finishForResult();
                fadeOutActivity();
            }
        });
    }



    @Override
    public void clickItem(String itemName) {
        mAssetName.setText(itemName);
    }


    @Override
    public void onBackPressed() {
        if(mLayout.getVisibility() == View.VISIBLE){
            mLayout.setVisibility(View.GONE);
        }
        else{
            super.onBackPressed();
            fadeOutActivity();
        }
    }



    private void fadeOutActivity(){
        if(flag.equals("new")){
            overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else{
            overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);
        }
    }
}