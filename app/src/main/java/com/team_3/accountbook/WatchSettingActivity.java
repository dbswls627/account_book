package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WatchSettingActivity extends AppCompatActivity {
    EditText amountGoal, warning;
    ImageView back;
    TextView save;
    Switch mSwitch;
    AppDatabase db;

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

        amountGoal.addTextChangedListener(new AddActivity.NumberTextWatcher(amountGoal));            // 금액 입력반응

        amountGoal.setText(db.dao().getAmountGoal());
        warning.setText(db.dao().getWarning());
        mSwitch.setChecked(db.dao().getWatchOnOff());



        back.setOnClickListener((view) -> {
            finish();
        });

        save.setOnClickListener((view) -> {
            ListenerService LS = new ListenerService();
            LS.bluetooth(WatchSettingActivity.this, amountGoal.getText().toString().replace(",","") + "!");
            LS.bluetooth(WatchSettingActivity.this, warning.getText() + "?");
            db.dao().updateWatch(amountGoal.getText().toString().replace(",",""),warning.getText().toString(),mSwitch.isChecked());
            finish();
        });
    }
}