package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {
    TextView add,sms,list,sqlTest,wear;
    Intent intent;
    BottomNavigationView bottom_menu;
    @Override
    protected void onStart() {
        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.setting);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        wear = findViewById(R.id.wear);
        add = findViewById(R.id.add);
        sms = findViewById(R.id.sms);
        list = findViewById(R.id.list);
        sqlTest = findViewById(R.id.sqlTest);
        
        sms.setOnClickListener((view)->{
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        list.setOnClickListener((view)->{
            intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });

        sqlTest.setOnClickListener((view)->{
            intent = new Intent(this, SqlTestActivity.class);
            startActivity(intent);
        });
        wear.setOnClickListener((view)->{
            intent = new Intent(this, WearActivity.class);
            startActivity(intent);
        });
        bottom_menu = findViewById(R.id.bottom_menu);

        bottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {

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
}