package com.team_3.accountbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class smsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSms(bundle);    // sms 정보를 담을 객체 선언과 동시에 함수로 정보 저장

        if(messages.length > 0){
            String sender = messages[0].getOriginatingAddress();
            String content = messages[0].getMessageBody().toString();
            Date date = new Date(messages[0].getTimestampMillis());

            sendToMainActivity(context, sender, content, date);
        }
    }


    private SmsMessage[] parseSms(Bundle bundle){
        // PDU: Protocol Data Units. 안드로이드는 SMS 를 PDU 형태로 전달한다.
        Object[] objs = (Object[]) bundle.get("pdus");                  // bundle 로부터 메시지들을 받아온 후,
        SmsMessage[] messages = new SmsMessage[objs.length];            // SmsMessage 배열을 그 크기만큼 만들어 준 뒤,

        for(int i=0; i<objs.length; i++){                               // 반복문을 통해 하나씩 PDU 를 SmsMessage 객체로 변환해주어야 하는데
            messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);   // 이는 내장 함수 createFromPdu()를 사용한다.
        }

        return messages;
    }


    private void sendToMainActivity(Context context, String sender, String content, Date date){
        MainActivity ma = new MainActivity();

        Intent intent = new Intent(context, MainActivity.class);   // MainActivity 를 호출할 intent 생성. 우선 형식상으로 정해놨다.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  // 실행한 액티비티와 관련된 태스크가 존재하면 동일한 태스크 내에서 실행하고, 그렇지 않으면 새로운 태스크에서 액티비티를 실행하는 플래그
                |Intent.FLAG_ACTIVITY_SINGLE_TOP       // 실행할 액티비티가 태스크 스택 최상단에 이미 있다면 액티비티를 다시 실행하지 않는 플래그
                |Intent.FLAG_ACTIVITY_CLEAR_TOP);      // 실행할 액티비티가 태스크에 이미 있다면 태스크에 있는 동일한 액티비티부터 최상단의 액티비티까지 모두 제거하고 새로운 액티비티를 실행하는 플래그
        intent.putExtra("sender", sender);
        intent.putExtra("content", content);
        intent.putExtra("date", ma.sdf.format(date));

        if(sender.equals("1234")){          // 우선 송신 번호가 '1234' 일때만 토스트가 띄워지도록 함.
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
        //context.startActivity(intent);    // sms 정보를 가진 intent 로 액티비티 호출
    }
}