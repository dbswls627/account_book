package com.team_3.accountbook;

import static java.lang.Integer.parseInt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ListActivity extends AppCompatActivity {

    ArrayList<item> arrayList = new ArrayList<>();
    ArrayList<Cost> arrayList2 = new ArrayList<>();
    RecyclerView mRecyclerView;
    Context context;
    AppDatabase db;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db =AppDatabase.getInstance(this);

        arrayList2= (ArrayList<Cost>) db.dao().getCostAll();        // Cost 의 모든 값을 날짜로 내림차순 정렬해 받아옴.   ※부분적으로 받아오는게 효울적일거같음. data.java 생성필요
        arrayList2.forEach(it ->
                arrayList.add(new item(it.getUseDate(), it.getContent(), it.getAmount()))     // 받아온 Cost 데이터를 item 에 뿌려줌
        );
        ArrayList<String> dateArray = new ArrayList<>();            // 중복 제거한 날짜(yyyy년 MM월 dd일)만 담는 리스트.(adapter2로 넘겨주기 위함)
        for (item item : arrayList) {
            if(!dateArray.contains(item.getMsgDate().substring(0, 14))) {
                dateArray.add(item.getMsgDate().substring(0, 14));
            }
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setAdapter(new adapter2(context, arrayList, dateArray));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}
