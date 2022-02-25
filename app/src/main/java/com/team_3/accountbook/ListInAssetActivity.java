package com.team_3.accountbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ListInAssetActivity extends AppCompatActivity implements adapter.OnItemClickInListInAsset{
    List<Cost> costList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    RecyclerView mRV_listInAsset;
    adapter2 adapter2;
    AppDatabase db;
    String wayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_in_asset);

        mRV_listInAsset = findViewById(R.id.rv_listInAsset);
        db = AppDatabase.getInstance(this);


        wayName = getIntent().getStringExtra("wayName");
        setList();
    }



    private void setList(){
        costList = db.dao().getCostInWayName(wayName);
        dateList.clear();
        for (int i = 0; i < costList.size(); i++) {
            if(!dateList.contains(costList.get(i).getUseDate().substring(0, 14))){
                dateList.add(costList.get(i).getUseDate().substring(0, 14));
            }
        }

        adapter2 = new adapter2(this, (ArrayList<Cost>) costList, dateList);
        mRV_listInAsset.setAdapter(adapter2);
        mRV_listInAsset.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                setList();      // adapter2.NotifyDataSetChanged();를 해도 새로고침이 안되어있어 그냥 다시 연결해버림.
            }
        }
    }



    @Override
    public void onClick(Cost cost) {
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra("costId", cost.getCostId());
        intent.putExtra("flag", 1);
        startActivityForResult(intent, 0);
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}


