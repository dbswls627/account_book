package com.team_3.accountbook;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.wear.tiles.TileService;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/*
  *  This is a listener on the mobile device to get messages via
  *  the datalayer and then pass it to the main activity so it can be
  *  displayed.   the messages should be coming from the device/phone.
 */
public class ListenerService extends WearableListenerService {
    String TAG = "wear listener";
    AppDatabase db;
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {  // 백그라운드
        db = AppDatabase.getInstance(this);

        if (messageEvent.getPath().equals("/message_path")) {
            final String message = new String(messageEvent.getData());      // message -> 'n,nnn원'(실시간 반영된 sum 금액)
            Log.v(TAG, "Message path received is: " + messageEvent.getPath());
            Log.v(TAG, "Message received is: " + message);

            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);   // MainAc 의 onReceive()함수가 실행됨

            db.dao().insert(new Data("test",message));      // 데이터를 받으면 db에 추가. name은 고정이라 amount 값만 바뀜
            TileService.getUpdater(this).requestUpdate(MyTileService.class);    //타일 업데이트
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

}
