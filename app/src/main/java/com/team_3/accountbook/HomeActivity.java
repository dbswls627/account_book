package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private long firstBackPressedTime = 0;          // 뒤로가기 체크시간
    
    @Override
    public void onBackPressed() {
        // ↓ 기존 뒤로가기 버튼의 기능을 막기위해 주석처리
        //super.onBackPressed();

        // 뒤로가기를 누르고 2초가 지났다면~
        if(System.currentTimeMillis() > firstBackPressedTime + 2000) {
            firstBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        // 뒤로가기 버튼을 누른지 2초가 안지났다면~
        else if(System.currentTimeMillis() <= firstBackPressedTime + 2000) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.home);
        bottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {
            Intent intent;
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