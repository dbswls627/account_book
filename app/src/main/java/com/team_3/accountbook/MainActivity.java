package com.team_3.accountbook;

import static java.lang.Integer.parseInt;

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



public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    public SimpleDateFormat sdf = new SimpleDateFormat("< yyyy년 MM월 dd일 HH:mm >");

    ArrayList<item> arrayList = new ArrayList<>();
    RecyclerView mRecyclerView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setAdapter(new adapter2(context,arrayList));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        callPermission();
        readSMSMessage();
    }


    public int readSMSMessage() {
        Uri allMessage = Uri.parse("content://sms");   // 문자 접근

        String where = "address = 15881688";

        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,    // .query(from / select / ? / where / order by);
                new String[]{"body", "date"},
                where, null,
                "date DESC");

        Date timeInDate;

        while (c.moveToNext()) {

            String body = c.getString(0);
            long timestamp = c.getLong(1);

            timeInDate = new Date(timestamp);
            String date = sdf.format(timeInDate);

            arrayList.add(parsing(body,date));    // 리턴 받은 값 바로 리스트에 저장
        }
        return 0;
    }


    private item parsing(String body,String date){
        String amount;       // 추출한 가격(~원)
        String place;        // 추출한 사용처
        int int_amount;      // int 형으로 변환한 가격(only 숫자)

        Pattern p = Pattern.compile("([0-9]*)(.*)([0-9]+)(원)");  // 원 앞에 있는 숫자들과 원을 파싱 적어도 앞에 숫자하나가 있어야함
        Matcher m;               // 패턴 p와 matching 되는 문자들을 저장할 Matcher 클래스 객체 m 생성
        m = p.matcher(body);     // 정규식으로 가격(~원)을 파싱 후 매칭되는 문자들을 Matcher 객체에 저장

        if(m.find()){ amount = m.group(); }       // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else{ amount = null; }                    // 매칭되는 문자가 없으면 null4


        p = Pattern.compile("(.*사용)");           // p객체 재활용 *** 사용 까지 parsing
        m = p.matcher(body);                      // m객체 재활용

        if(m.find()){ place = m.group(); }        // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else{ place = null; }                     // 매칭되는 문자가 없으면 null
        try {                                     // null 값을 받으면 에러가 나서 예외처리 사용
            place=place.replaceAll("사용$","");    // 정규식으로 끝에 있는 사용만 제거
        }
        catch (Exception e){place = "";}

        try {  //null 값을 받으면 에러가 나서 예외처리 사용
            int_amount = Integer.parseInt(amount.replaceAll("[,]|[원]", "")); // '~원' 형식으로 추출된 가격을 정수형으로 2차 가공 및 반환
        }
        catch (Exception e){int_amount=-1;}

        return new item(date,place,int_amount);   // item 형태의 객체 return
    }



    private void callPermission() {     // 문자 권한 얻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSIONS_REQUEST_READ_SMS);
        } else {
            // 해당 로직으로 이동
        }
        String[] permissions = {Manifest.permission.RECEIVE_SMS};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

}
