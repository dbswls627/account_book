package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WatchSettingActivity extends AppCompatActivity {
    EditText amountGoal, warning;
    ImageView back;
    TextView save, mGoal, mWarning, mWon, mPercent;
    Switch mSwitch;
    AppDatabase db;
    LinearLayout layout;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_setting);
        db = AppDatabase.getInstance(this);
        amountGoal = findViewById(R.id.amountGoal);
        mSwitch = findViewById(R.id.onOff);
        back = findViewById(R.id.toBack);
        warning = findViewById(R.id.warning);
        save = findViewById(R.id.save);
        mGoal = findViewById(R.id.tv_goal);
        mWarning = findViewById(R.id.tv_warning);
        mWon = findViewById(R.id.won);
        mPercent = findViewById(R.id.tv_percent);
        layout = findViewById(R.id.layout);

        amountGoal.addTextChangedListener(new AddActivity.NumberTextWatcher(amountGoal));            // 금액 입력반응

        amountGoal.setText(db.dao().getAmountGoal());
        warning.setText(db.dao().getWarning());
        mSwitch.setChecked(db.dao().getWatchOnOff());

        setEnable();

        mSwitch.setOnClickListener((view)->{
           setEnable();
        });

        back.setOnClickListener((view) -> {
            finish();
            overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        });

        save.setOnClickListener((view) -> {
            if (mSwitch.isChecked()) {
                ListenerService LS = new ListenerService();
                LS.bluetooth(WatchSettingActivity.this, amountGoal.getText().toString().replace(",", "") + "!");
                LS.bluetooth(WatchSettingActivity.this, warning.getText() + "?");
            }
            db.dao().updateWatch(amountGoal.getText().toString().replace(",", ""), warning.getText().toString(), mSwitch.isChecked());
            finish();
            overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        });
    }



    @SuppressLint("ResourceAsColor")
    void setEnable(){
        if (mSwitch.isChecked()){
            layout.setBackgroundColor(getResources().getColor(R.color.white));

            mGoal.setTextColor(getResources().getColor(R.color.black));
            amountGoal.setTextColor(getResources().getColor(R.color.hardDarkGray));
            mWon.setTextColor(getResources().getColor(R.color.hardDarkGray));

            mWarning.setTextColor(getResources().getColor(R.color.black));
            warning.setTextColor(getResources().getColor(R.color.hardDarkGray));
            mPercent.setTextColor(getResources().getColor(R.color.hardDarkGray));

            amountGoal.setEnabled(true);
            warning.setEnabled(true);
        }
        else {
            layout.setBackgroundColor(getResources().getColor(R.color.hardLightGray));

            mGoal.setTextColor(getResources().getColor(R.color.gray));
            amountGoal.setTextColor(getResources().getColor(R.color.gray));
            mWon.setTextColor(getResources().getColor(R.color.gray));

            mWarning.setTextColor(getResources().getColor(R.color.gray));
            warning.setTextColor(getResources().getColor(R.color.gray));
            mPercent.setTextColor(getResources().getColor(R.color.gray));

            amountGoal.setEnabled(false);
            warning.setEnabled(false);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }
}