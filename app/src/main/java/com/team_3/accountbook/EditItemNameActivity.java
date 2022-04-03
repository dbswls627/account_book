package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemNameActivity extends AppCompatActivity {
    private EditText mItemName;
    private AppDatabase db;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_name);

        mItemName = findViewById(R.id.name_editItemName);
        db = AppDatabase.getInstance(this);

        String itemName = getIntent().getStringExtra("itemName");
        flag = getIntent().getStringExtra("flag");

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
                if(flag.equals("modify_assetName")){
                    Toast.makeText(this, "수정 자산명 저장", Toast.LENGTH_SHORT).show();
                }
                else if(flag.equals("new_assetName")){
                    Toast.makeText(this, "새로운 자산명 저장", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

        if(flag.equals("modify_assetName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else if(flag.equals("new_assetName")){
            overridePendingTransition(R.anim.hold_activity, R.anim.bottom_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
    }
}