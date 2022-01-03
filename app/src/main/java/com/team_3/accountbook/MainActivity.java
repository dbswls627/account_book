package com.team_3.accountbook;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
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



public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;

    ArrayList<item> arrayList = new ArrayList<>();
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setAdapter(new adapter(arrayList));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        callPermission();
        readSMSMessage();
    }


    public int readSMSMessage() {
        Uri allMessage = Uri.parse("content://sms");   //문자 접근

        String where = "address = 15881688";

        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,    // .query(from / select / ? / where / order by);
                new String[]{"body", "date"},
                where, null,
                "date DESC");



        Date timeInDate;

        while (c.moveToNext()) {
            SimpleDateFormat sdf = new SimpleDateFormat("< yyyy년 MM월 dd일 HH:mm >");

            String body = c.getString(0);      //문자에 날짜 나오는데 연도가 안나옴
            long timestamp = c.getLong(1);

            timeInDate = new Date(timestamp);
            String date = sdf.format(timeInDate);

            int regexAmount = parsingAmount(body);

            arrayList.add(new item(date, body, regexAmount));
        }
        return 0;
    }


    private int parsingAmount(String body){
        String amount;     // 추출한 가격(~원)
        int int_amount;    // int 형으로 변환한 가격(only 숫자)

        Pattern p = Pattern.compile("\\S*(원)"); // 가격을 뽑기 위한 정규식(\S: 공백이 아닌 모든 문자, *: 앞 문자 0개 이상)
        Matcher m;         // 패턴 p와 matching 되는 문자들을 저장할 Matcher 클래스 객체 m 생성
        m = p.matcher(body);     // 정규식으로 가격(~원)을 파싱 후 매칭되는 문자들을 Matcher 객체에 저장

        if(m.find()){ amount = m.group(); }   // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else{ amount = null; }                // 매칭되는 문자가 없으면 null

        // '~원' 형식으로 추출된 가격을 정수형으로 2차 가공 및 반환
        try {
            int_amount = Integer.parseInt(amount.replaceAll("[,]|[원]", ""));
            return int_amount;
        }
        catch (Exception e) {
            return -1;
        }
    }


    private void callPermission() {     //문자 권한 얻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSIONS_REQUEST_READ_SMS);
        } else {
            // 해당 로직으로 이동
        }
    }
}
