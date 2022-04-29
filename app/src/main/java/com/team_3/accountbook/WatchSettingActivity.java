package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.YearMonth;

public class WatchSettingActivity extends AppCompatActivity {
    EditText amountGoal, warning;
    ImageView back;
    TextView save, mGoal, mWarning, mWon, mPercent;
    ProgressBar day_progressbar,amount_progressbar;
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
        day_progressbar = findViewById(R.id.day_progressbar);
        amount_progressbar = findViewById(R.id.amount_progressbar);
        mSwitch = findViewById(R.id.onOff);
        back = findViewById(R.id.toBack);
        warning = findViewById(R.id.warning);
        save = findViewById(R.id.save);
        mGoal = findViewById(R.id.tv_goal);
        mWarning = findViewById(R.id.tv_warning);
        mWon = findViewById(R.id.won);
        mPercent = findViewById(R.id.tv_percent);
        layout = findViewById(R.id.layout);

        ListenerService LS = new ListenerService(); // monthYearFromDate 함수 불러다 쓰기 위함


        amountGoal.addTextChangedListener(new AddActivity.NumberTextWatcher(amountGoal));            // 금액 입력반응
        amountGoal.setText(db.dao().getAmountGoal());

        warning.setText(db.dao().getWarning());
        mSwitch.setChecked(db.dao().getWatchOnOff());

        LocalDate date = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(date);
                                                            //오늘날짜 나누기 이번달 마지막 날짜
        day_progressbar.setProgress((int) ((Float.valueOf(date.getDayOfMonth())/yearMonth.lengthOfMonth())*140)); //날짜 게이지

        try{        //이번달 쓴돈이 없으면 null 불러와 팅기므로 예외처리 함
            int amountPercent = (int) ((float)db.dao().getAmountOfMonthForWatch(LS.monthYearFromDate(date), "expense") /
                    Integer.parseInt(db.dao().getAmountGoal()));

            if (amountPercent>1) {amountPercent = 1;}    //amountPercent 가 1을 넘으면 게이지가 넘쳐서 침범

            amount_progressbar.setProgress(amountPercent* 140);
        }
        catch (Exception e){
            amount_progressbar.setProgress(0);
        }







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