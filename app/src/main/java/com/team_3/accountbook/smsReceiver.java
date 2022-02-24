package com.team_3.accountbook;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.RemoteViews;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class smsReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "1";           // Channel 에 대한 id 생성: Channel 을 구부하기 위한 ID.
    private NotificationManager mNotificationManager;       // Channel 을 생성 및 전달해 줄 수 있는 Manager
    private static final int NOTIFICATION_ID = 0;           // Notification 에 대한 ID
    private NotificationCompat.Builder notifyBuilder;               // Notification Builder: Notification 을 생성

    private String useDate, useHour, useAmount;
    private Pattern p;
    private Matcher m;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSms(bundle);    // sms 정보를 담을 객체 선언과 동시에 함수로 정보 저장

        if (messages.length > 0) {
            String sender = messages[0].getOriginatingAddress();
            String content = messages[0].getMessageBody().toString();
            Date date = new Date(messages[0].getTimestampMillis());

            sendToMainActivity(context, sender, content, date);
        }

    }


    private SmsMessage[] parseSms(Bundle bundle) {
        // PDU: Protocol Data Units. 안드로이드는 SMS 를 PDU 형태로 전달한다.
        Object[] objs = (Object[]) bundle.get("pdus");                  // bundle 로부터 메시지들을 받아온 후,
        SmsMessage[] messages = new SmsMessage[objs.length];            // SmsMessage 배열을 그 크기만큼 만들어 준 뒤,

        for (int i = 0; i < objs.length; i++) {                         // 반복문을 통해 하나씩 PDU 를 SmsMessage 객체로 변환해주어야 하는데
            messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);   // 이는 내장 함수 createFromPdu()를 사용한다.
        }

        return messages;
    }


    private void sendToMainActivity(Context context, String sender, String content, Date date) {
        MainActivity ma = new MainActivity();

        Intent intent = new Intent(context, MainActivity.class);   // MainActivity 를 호출할 intent 생성. 우선 형식상으로 정해놨다.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK   // 실행한 액티비티와 관련된 태스크가 존재하면 동일한 태스크 내에서 실행하고, 그렇지 않으면 새로운 태스크에서 액티비티를 실행하는 플래그
                | Intent.FLAG_ACTIVITY_SINGLE_TOP       // 실행할 액티비티가 태스크 스택 최상단에 이미 있다면 액티비티를 다시 실행하지 않는 플래그
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);      // 실행할 액티비티가 태스크에 이미 있다면 태스크에 있는 동일한 액티비티부터 최상단의 액티비티까지 모두 제거하고 새로운 액티비티를 실행하는 플래그
        intent.putExtra("sender", sender);
        intent.putExtra("content", content);
        intent.putExtra("date", ma.sdf.format(date));

        if (sender.equals("1234")) {          // ★우선 송신 번호가 '1234' 일때만 토스트가 띄워지도록 함.
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
        //context.startActivity(intent);      // sms 정보를 가진 intent 로 액티비티 호출

        smsNotification(context, content);    // ←-- ★그만할 때 이부분을 주석처리해서 알림이 안뜨게 할 것(선택)
    }


    private void smsNotification(Context context, String content) {
        p = Pattern.compile("[0-9]{2}[/][0-9]{2}");           // ex) 02/09
        m = p.matcher(content);
        if (m.find()) { useDate = m.group(); }                // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else { useDate = null; }                              // 매칭되는 문자가 없으면 null

        p = Pattern.compile("[0-9]{2}[:][0-9]{2}");           // ex) 15:32
        m = p.matcher(content);
        if (m.find()) { useHour = m.group(); }
        else { useHour = null; }

        p = Pattern.compile("([0-9])\\S*(원)");               // ex) 13,500원
        m = p.matcher(content);
        if (m.find()) { useAmount = m.group(); }
        else { useAmount = null; }

        if (useDate != null && useHour != null && useAmount != null) {      // 결제문자의 형식이 갖춰졌을 때~
            mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Channel 정의 생성자( construct 이용 )
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
                //Channel 에 대한 기본 설정
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription("Notification from Mascot");
                // Manager 을 이용하여 Channel 생성
                mNotificationManager.createNotificationChannel(notificationChannel);

                Intent notiIntent = new Intent(context, MainActivity.class);    // 알림 클릭시 MainActivity 로 이동하게 설정
                PendingIntent notiPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notiIntent, PendingIntent.FLAG_IMMUTABLE);   // Notification 에선 PendingIntent 라는걸 써야한다고 함

                notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("새로운 결제 알림")                   // 알림 제목
                        .setContentText("결제 정보를 등록하세요.")              // 알림 내용
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)    // 알림 아이콘
                        .setContentIntent(notiPendingIntent)                // 알림 클릭시 실행할 액티비티 인텐트
                        .setAutoCancel(true);                               // notification 을 탭 했을경우 notification 을 없앤다.
//                RemoteViews notificationLayout = new RemoteViews("com.team_3.accountbook", R.layout.notification_small);
//                @SuppressLint("RemoteViewLayout") RemoteViews notificationLayoutExpanded = new RemoteViews("com.team_3.accountbook", R.layout.notification_large);
//                Notification notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)    // 알림 아이콘
//                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                        .setCustomContentView(notificationLayout)
//                        .setCustomBigContentView(notificationLayoutExpanded)
//                        .setContentIntent(notiPendingIntent)                // 알림 클릭시 실행할 액티비티 인텐트
//                        .setAutoCancel(true)                                // notification 을 탭 했을경우 notification 을 없앤다.
//                        .build();

                mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());        // 최종적으로 알림 띄우기
            }
        }

    }


}