package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditItemNameActivity extends AppCompatActivity {
    private TextView mAssetOrSort;
    private EditText mItemName;
    private AppDatabase db;
    private String itemName;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_name);

        mAssetOrSort = findViewById(R.id.assetOrSort);
        mItemName = findViewById(R.id.name_editItemName);
        db = AppDatabase.getInstance(this);

        itemName = getIntent().getStringExtra("itemName");
        flag = getIntent().getStringExtra("flag");

        if(flag.equals("modify_assetName")){ mAssetOrSort.setText("자산 수정"); }
        else if(flag.equals("new_assetName")){ mAssetOrSort.setText("자산 추가"); }
        mItemName.setText(itemName);

    }


    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_editItemName:
                setResult(RESULT_OK);
                finish();
                if(flag.equals("modify_assetName")){
                    overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }
                else if(flag.equals("new_assetName")){
                    overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }

                break;

            case R.id.save_editItemName:
                if(mItemName.getText().length() > 0){
                    List<String> assetName = db.dao().getAssetName();
                    boolean notExist = true;

                    for(String str : assetName){
                        if(str.equals(mItemName.getText().toString())){ notExist = false; }
                    }

                    if(notExist){       // 존재하지 않는 이름이면 실행
                        if(flag.equals("modify_assetName")){
                            db.dao().updateAsset(mItemName.getText().toString(), itemName);
                            closeAnimation();
                        }
                        else if(flag.equals("new_assetName")){
                            db.dao().insertAsset(mItemName.getText().toString());
                            closeAnimation();
                        }
                    }
                    else{
                        Toast.makeText(this, "중복된 자산명은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "한글자 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }



    private void closeAnimation(){
        setResult(RESULT_OK);
        finish();
        if(flag.equals("modify_assetName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else if(flag.equals("new_assetName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

        closeAnimation();
    }
}