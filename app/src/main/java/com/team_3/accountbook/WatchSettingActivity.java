package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;

public class WatchSettingActivity extends AppCompatActivity {
    private final DecimalFormat myFormatter = new DecimalFormat("###,###");
    EditText amountGoalEdit, warning;
    ImageView back;
    TextView save, mGoal, mWarning, mWon, mPercent,watchAmount, watchAmountGoal;
    CircularProgressIndicator day_progressbar,amount_progressbar;
    Switch mSwitch;
    AppDatabase db;
    LinearLayout layout;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_setting);
        db = AppDatabase.getInstance(this);
        amountGoalEdit = findViewById(R.id.amountGoal);
        watchAmount = findViewById(R.id.watchAmount);
        watchAmountGoal = findViewById(R.id.watchAmountGoal);
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

        LocalDate date = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(date);

        ListenerService LS = new ListenerService(); // monthYearFromDate 함수 불러다 쓰기 위함

        int amountGoal  = Integer.parseInt(db.dao().getAmountGoal());   //설정 목표값
        int amount = db.dao().getAmountOfMonthForWatch(LS.monthYearFromDate(date), "expense");  //이번달 사용금액



        amountGoalEdit.addTextChangedListener(new AddActivity.NumberTextWatcher(amountGoalEdit));            // 금액 입력반응
        amountGoalEdit.setText(String.valueOf(amountGoal));
        watchAmountGoal.setText(myFormatter.format(amountGoal)+"원");
        watchAmount.setText(myFormatter.format(amount));

        warning.setText(db.dao().getWarning());
        mSwitch.setChecked(db.dao().getWatchOnOff());


                                                            //오늘날짜 나누기 이번달 마지막 날짜
        day_progressbar.setProgress((int) ((Float.valueOf(date.getDayOfMonth())/yearMonth.lengthOfMonth())*120)); //날짜 게이지

        try{        //이번달 쓴돈이 없으면 null 불러와 팅기므로 예외처리 함
            int amountPercent = (int) (120*((float)amount /
                               amountGoal));

            if (amountPercent>120) {amountPercent = 120;}    //amountPercent 가 1을 넘으면 게이지가 넘쳐서 침범

            amount_progressbar.setProgress(amountPercent);
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
                LS.bluetooth(WatchSettingActivity.this, amountGoalEdit.getText().toString().replace(",", "") + "!");
                LS.bluetooth(WatchSettingActivity.this, warning.getText() + "?");
            }
            db.dao().updateWatch(amountGoalEdit.getText().toString().replace(",", ""), warning.getText().toString(), mSwitch.isChecked());
            finish();
            overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        });

    }



    @SuppressLint("ResourceAsColor")
    void setEnable(){
        if (mSwitch.isChecked()){
            layout.setBackgroundColor(getResources().getColor(R.color.white));

            mGoal.setTextColor(getResources().getColor(R.color.black));
            amountGoalEdit.setTextColor(getResources().getColor(R.color.hardDarkGray));
            mWon.setTextColor(getResources().getColor(R.color.hardDarkGray));

            mWarning.setTextColor(getResources().getColor(R.color.black));
            warning.setTextColor(getResources().getColor(R.color.hardDarkGray));
            mPercent.setTextColor(getResources().getColor(R.color.hardDarkGray));

            amountGoalEdit.setEnabled(true);
            warning.setEnabled(true);
        }
        else {
            layout.setBackgroundColor(getResources().getColor(R.color.hardLightGray));

            mGoal.setTextColor(getResources().getColor(R.color.gray));
            amountGoalEdit.setTextColor(getResources().getColor(R.color.gray));
            mWon.setTextColor(getResources().getColor(R.color.gray));

            mWarning.setTextColor(getResources().getColor(R.color.gray));
            warning.setTextColor(getResources().getColor(R.color.gray));
            mPercent.setTextColor(getResources().getColor(R.color.gray));

            amountGoalEdit.setEnabled(false);
            warning.setEnabled(false);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }
}