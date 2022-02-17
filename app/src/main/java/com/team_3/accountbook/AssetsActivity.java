package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AssetsActivity extends AppCompatActivity {
    private List<AssetNameWayNameAndBalance> ANWNList;
    private ArrayList<String> assetNameList = new ArrayList<>();
    RecyclerView mAssetAndWay;
    AppDatabase db;

    BottomNavigationView bottom_menu;

    protected void onStart() {
        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.assets);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);

        bottom_menu = findViewById(R.id.bottom_menu);
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

        mAssetAndWay = findViewById(R.id.rv_AssetAndWay);
        db = AppDatabase.getInstance(this);


        ANWNList = db.dao().getAnWnWb();
        for (int i = 0; i < ANWNList.size(); i++) {
            if (!assetNameList.contains(ANWNList.get(i).getAssetName())) {
                assetNameList.add(ANWNList.get(i).getAssetName());
                Log.d("LEEhj", ANWNList.get(i).getAssetName());
            }
        }

        mAssetAndWay.setAdapter(new AssetOutAdapter(ANWNList, assetNameList, this));
        mAssetAndWay.setLayoutManager(new LinearLayoutManager(this));
    }



}