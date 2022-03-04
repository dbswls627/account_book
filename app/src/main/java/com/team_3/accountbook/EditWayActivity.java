package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class EditWayActivity extends AppCompatActivity {
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");
    private EditText mAssetName, mWayName, mBalance, mMemo, mPhoneNumber, mDelimiter;
    private String assetName, wayName, formatBalance, flag = "", division = "expense";
    private int balance, gap = 0;
    private AppDatabase db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_way);

        db = AppDatabase.getInstance(this);

        mAssetName = findViewById(R.id.editWay_assetName);
        mWayName = findViewById(R.id.editWay_wayName);
        mBalance = findViewById(R.id.editWay_balance);
        mMemo = findViewById(R.id.editWay_memo);
        mPhoneNumber = findViewById(R.id.editWay_phoneNumber);
        mDelimiter = findViewById(R.id.editWay_delimiter);


        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if(flag.equals("modify")){
            wayName = intent.getStringExtra("wayName");
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

        mBalance.addTextChangedListener(new AddActivity.NumberTextWatcher(mBalance));


    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.tv_save_editWay:
                int FK_assetId = db.dao().getAssetId(mAssetName.getText().toString());

                String s = mBalance.getText().toString();
                try { s = s.replaceAll(",", ""); }          // 금액의 쉼표(,) 제거 <- null 값을 받으면 에러가 나서 예외처리 사용.
                catch (Exception ignored) { }
                int int_balance = Integer.parseInt(s);

                try {
                    if(flag.equals("modify")){      // Way 수정
                        if(balance != int_balance){
                            gap = balance - int_balance;
                            if(gap < 0){            // 기존 수단 잔액보다 큰 금액으로 수정함
                                gap = -gap;
                                division = "income";
                            }
                            long now = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm");
                            String processedNow = sdf.format(now);

                            db.dao().insertCost(
                                    processedNow,
                                    mWayName.getText().toString(),
                                    "잔액수정",
                                    gap,
                                    "차액",
                                    int_balance,
                                    division,
                                    0
                            );
                        }

                        db.dao().updateWayData(
                                FK_assetId,
                                mWayName.getText().toString(),
                                int_balance,
                                mMemo.getText().toString(),
                                mPhoneNumber.getText().toString(),
                                mDelimiter.getText().toString(),
                                wayName
                        );
                        if(!wayName.equals(mWayName.getText().toString())){
                            db.dao().updateCostWayName(wayName, mWayName.getText().toString());
                        }
                    }
                    else if(flag.equals("new")){    // Way 추가

                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("wayName", mWayName.getText().toString());
                    setResult(RESULT_OK, resultIntent);
                    finish();

                }
                catch (Exception e){
                    Toast.makeText(this, "중복된 수단명은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }






}