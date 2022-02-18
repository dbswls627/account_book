package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ListInAssetActivity extends AppCompatActivity {
    List<Cost> costList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    RecyclerView mRV_listInAsset;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_in_asset);

        mRV_listInAsset = findViewById(R.id.rv_listInAsset);
        db = AppDatabase.getInstance(this);


        String wayName = getIntent().getStringExtra("wayName");
        costList = db.dao().getCostInWayName(wayName);
        for (int i = 0; i < costList.size(); i++) {
            if(!dateList.contains(costList.get(i).getUseDate().substring(0, 14))){
                dateList.add(costList.get(i).getUseDate().substring(0, 14));
            }
        }

        mRV_listInAsset.setAdapter(new adapter2(this, (ArrayList<Cost>) costList, dateList));
        mRV_listInAsset.setLayoutManager(new LinearLayoutManager(this));
    }
}