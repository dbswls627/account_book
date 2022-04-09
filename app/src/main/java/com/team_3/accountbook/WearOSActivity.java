package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WearOSActivity extends AppCompatActivity {
    EditText amountGoal, warning;
    ImageView back;
    TextView save;
    AppDatabase db;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_osactivity);
        db = AppDatabase.getInstance(this);
        amountGoal = findViewById(R.id.amountGoal);
        back = findViewById(R.id.toBack);
        warning = findViewById(R.id.warning);
        save = findViewById(R.id.save);

        amountGoal.setText(db.dao().getAmountGoal());

        back.setOnClickListener((view) -> {
            finish();
        });
        save.setOnClickListener((view) -> {
            ListenerService LS = new ListenerService();
            LS.bluetooth(WearOSActivity.this, amountGoal.getText() + "!");
            db.dao().updateAmountGoal(amountGoal.getText().toString());
            finish();
        });
    }
}