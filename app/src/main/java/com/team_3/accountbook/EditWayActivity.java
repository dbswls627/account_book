package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;

public class EditWayActivity extends AppCompatActivity {
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");
    private EditText mAssetName, mWayName, mBalance, mMemo, mPhoneNumber, mDelimiter;
    private String assetName, wayName, formatBalance;
    private int balance;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_way);

        mAssetName = findViewById(R.id.editWay_assetName);
        mWayName = findViewById(R.id.editWay_wayName);
        mBalance = findViewById(R.id.editWay_balance);
        mMemo = findViewById(R.id.editWay_memo);
        mPhoneNumber = findViewById(R.id.editWay_phoneNumber);
        mDelimiter = findViewById(R.id.editWay_delimiter);

        Intent intent = getIntent();
        assetName = intent.getStringExtra("assetName");
        wayName = intent.getStringExtra("wayName");
        balance = intent.getIntExtra("balance", 0);
        formatBalance = myFormatter.format(balance);

        mAssetName.setText(assetName);
        mWayName.setText(wayName);
        mBalance.setText(formatBalance + "");

        mBalance.addTextChangedListener(new AddActivity.NumberTextWatcher(mBalance));
    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.tv_save_editWay:
                Toast.makeText(this, "저장이 될거임~", Toast.LENGTH_SHORT).show();

                break;
        }
    }






}