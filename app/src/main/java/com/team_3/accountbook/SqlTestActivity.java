package com.team_3.accountbook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SqlTestActivity extends AppCompatActivity {
    private HomeActivity h = new HomeActivity();
    EditText mAssetsName, mWayName, mWayBalance, mFKAssetsId;
    AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_test);

        mAssetsName = findViewById(R.id.et_assetsName);
        mWayName = findViewById(R.id.et_wayName);
        mWayBalance = findViewById(R.id.et_wayBalance);
        mFKAssetsId = findViewById(R.id.et_FK_assetsId);

        db = AppDatabase.getInstance(this);

        //buildTableData();           // ★!!! <앱 첫 실행시에만 실행시킬것> !!!★    첫실행 후 주석처리 하기.
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.bt_insertAssets:
                Asset asset = new Asset();
                if (mAssetsName.length() > 0) {
                    asset.setAssetName(mAssetsName.getText().toString());
                    db.dao().insertAsset(asset);
                    Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show();
                    mAssetsName.setText("");
                }
                else {
                    Toast.makeText(this, "자산명을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.bt_insertWay:
                if (mWayName.length() > 0 && mFKAssetsId.length() > 0) {
                    db.dao().insertWay(mWayName.getText().toString(), Integer.parseInt(mWayBalance.getText().toString()), Integer.parseInt(mFKAssetsId.getText().toString()));
                    Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show();
                    mWayName.setText("");
                    mFKAssetsId.setText("");
                }
                else {
                    Toast.makeText(this, "수단명, 잔액, 자산 id를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.bt_deleteAssetsAll:
                db.dao().deleteAssetAll();
                Toast.makeText(this, "모두 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                break;


            case R.id.bt_referAssets:
                List<Asset> list_assets = db.dao().getAssetAll();
                for (int i = 0; i < list_assets.size(); i++) {
                    Log.d("Assets", "ID:" + list_assets.get(i).getAssetId() + ", name:" + list_assets.get(i).getAssetName());
                }
                break;


            case R.id.bt_referWay:
                List<Way> list_way = db.dao().getWayAll();
                for (int i = 0; i < list_way.size(); i++) {
                    Log.d("Way", "ID:" + list_way.get(i).getWayId() + ", name:" + list_way.get(i).getWayName()
                            + ", FK:" + list_way.get(i).getFK_assetId());
                }
                break;


            case R.id.bt_referCost:
                List<Cost> list_cost = db.dao().getCostAll();
                for (int i = 0; i < list_cost.size(); i++) {
                    Log.d("Cost", "ID:" + list_cost.get(i).getCostId() + ", " +
                            "amount:" + list_cost.get(i).getAmount() + ", " +
                            "content:" + list_cost.get(i).getContent() + ", " +
                            "date:" + list_cost.get(i).getUseDate() + ", " +
                            "balance:" + list_cost.get(i).getBalance() + ", " +
                            "FK_wayId:" + list_cost.get(i).getFK_wayId());
                }
                break;



//            case R.id.bt_referAssetWithWays:
//                List<AssetWithWays> AWW2 = db.dao().getAssetWithWays2();
//                for (int i = 0; i < AWW2.size(); i++) {
//                    int len = AWW2.get(i).getWay().size();
//                    Log.d("AWW2", String.valueOf(AWW2.size()) + ", " + len);
//                    for (int j = 0; j < len; j++) {
//                        Log.d("refer_Assets", AWW2.get(i).getAsset().getAssetId() + " " + AWW2.get(i).getAsset().getAssetName());
//                        Log.d("refer_Way", "- " + AWW2.get(i).getWay().get(j).getWayName());
//                    }
//                }
//                break;

            case R.id.bt_referAssetWithWays:
                List<AssetWithWay> list_AW = db.dao().getAssetWithWays();
                list_AW.forEach(it-> Log.d("ways :",
                        " wayID :"+it.getWayId()+
                                " wayName :"+it.getWayName()+
                                " wayBalance :"+it.getWayBalance()+
                                " FK_assetId :"+it.getFK_assetId()+
                                " assetID :"+it.getAssetId()+
                               " assetName :"+it.getAssetName()));
                break;


            case R.id.bt_referWayWithCosts:
                List<WayWithCost> list_WC = db.dao().getWayWithCosts();
                list_WC.forEach(it ->Log.d("WayWithCost", it.getUseDate() + " " + it.getWayName() + " " +
                        it.getAmount() + " " + it.getDivision()));
                break;
        }
    }



    private void buildTableData(){
        Asset asset = new Asset();

        String[] assetName = {"현금", "은행", "선불식카드"};

        String[] wayName = {"지갑", "나라사랑", "경기지역화폐", "노리(nori)", "현금"};
        int[] wayBalance = {43000, 99600, 3100, 1997500, 10000};
        int[] FK_assetId = {1 ,2 ,3, 2, 1};

        int[] amount = {-1000, -500, -500, -2000, 1100};
        String[] content = {"버스비", "샤프심", "볼펜", "파리바게트", "차액"};
        String[] date = {"01월 02일", "01월 10일", "01월 04일", "01월 03일", "01월 20일"};
        int[] balance = {99000, 98500, 1997500, 1998000, 99600};
        String[] sortName = {"교통/차량", "생활용품", "생활용품", "식비", "잔액수정"};
        String[] division = {"expense", "expense", "expense", "expense", null};
        int[] FK_wayId = {2, 2, 4, 4, 2};


        for (int i = 0; i < assetName.length; i++) {
            asset.setAssetName(assetName[i]);
            db.dao().insertAsset(asset);
        }
        for (int i = 0; i < wayName.length; i++) {
            db.dao().insertWay(wayName[i], wayBalance[i], FK_assetId[i]);
        }
        for (int i = 0; i < amount.length; i++) {
          //  db.dao().insertCost(amount[i], content[i], date[i], balance[i], sortName[i], division[i], FK_wayId[i]);
        }
    }
}