package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AssetForEditActivity extends AppCompatActivity implements AssetInAdapter.OnItemClickInAssetAc{
    private BottomNavigationView mBottom_menu;
    private RecyclerView mAssetRV;
    private AppDatabase db;
    private ArrayList<String> assetNameList = new ArrayList<>();
    private Intent intent;
    private String wayName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_for_edit);

        mAssetRV = findViewById(R.id.rv_AssetAndWay2);

        db = AppDatabase.getInstance(this);

        buildList();
        settingBottomMenu();
    }


    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_assetFotEdit:
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.addWay:
                Intent intent = new Intent(this, EditWayActivity.class);
                intent.putExtra("flag", "new");
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.bottom_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;
        }
    }



    @Override
    public void listItemClick(String name) {
        this.wayName = name;

        Intent intent = new Intent(this, EditWayActivity.class);
        intent.putExtra("wayName", wayName);
        intent.putExtra("flag", "modify_AFE");
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                buildList();
            }
        }
    }



    private void buildList(){
        List<AssetNameWayNameAndBalance> ANWNList = db.dao().getAnWnWb();

        assetNameList.clear();
        for (int i = 0; i < ANWNList.size(); i++) {
            if(!ANWNList.get(i).getAssetName().equals(getResources().getString(R.string.auto_assetName))){  // 자산명 '자동저장' 제거
                if (!assetNameList.contains(ANWNList.get(i).getAssetName())) {
                    assetNameList.add(ANWNList.get(i).getAssetName());
                }
            }
        }

        mAssetRV.setAdapter(new AssetOutAdapter(ANWNList, assetNameList, this));
        mAssetRV.setLayoutManager(new LinearLayoutManager(this));


    }



    private void settingBottomMenu(){
        mBottom_menu = findViewById(R.id.bottom_menu);
        mBottom_menu.setSelectedItemId(R.id.setting);
        mBottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {

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


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }
}