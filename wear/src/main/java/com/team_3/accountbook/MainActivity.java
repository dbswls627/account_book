package com.team_3.accountbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Wear Device code  so I can kept this straight.
 *
 * This will receive messages (from a device/phone) via the datalayer (through the listener code)
 * and display them to the wear device.  There is also a button to send a message
 * to the device/phone as well.
 *
 * if the wear device receives a message from the phone/device it will then send a message back
 * via the button on the wear device, it can also send a message to the device/phone as well.
 *    There is no auto response from the phone/device otherwise we would get caught in a loop!
 *
 * debuging over bluetooth.
 * https://developer.android.com/training/wearables/apps/debugging.html
 */


public class MainActivity extends WearableActivity {
    private final static String TAG = "Wear MainActivity";
    private TextView mTextView;

    Button myButton;
    String datapath = "/message_path";
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView =  findViewById(R.id.text);
        myButton =  findViewById(R.id.wrbutton);

        db = AppDatabase.getInstance(this);

        //send a message from the wear.  This one will not have response.
        new SendThread(datapath, "amount").start();

        if (db.dao().get("test")!=null) {
            mTextView.setText(db.dao().get("test"));
        }
        else {
            mTextView.setText("0원");
        }

        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Enables Always-on
        setAmbientEnabled();
        myButton.setOnClickListener((view -> {
            //Requires a new thread to avoid blocking the UI
            new SendThread(datapath, mTextView.getText().toString()).start();
        }));
    }



    // 워치 Listener.java 파일에서 메세지를 받으면 실행되는 부분. ★필요 없을거같음.
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {     // 데이터를 받으면
            String message = intent.getStringExtra("message");
            Log.v(TAG, "Main activity received message: " + message);
            // Display message in UI
            mTextView.setText(message);

        }
    }



    //This actually sends the message to the wearable device.
    class SendThread extends Thread {
        String path;
        String message;

        //constructor
        SendThread(String p, String msg) {
            path = p;
            message = msg;
        }

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, so no problem.
        public void run() {
            //first get all the nodes, ie connected wearable devices.
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                List<Node> nodes = Tasks.await(nodeListTask);

                //Now send the message to each device.
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());   // 앱으로 메세지를 보냄.(아무거나 보내 sum 금액을 받음)

                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        Integer result = Tasks.await(sendMessageTask);
                        Log.v(TAG, "SendThread: message send to " + node.getDisplayName());

                    } catch (ExecutionException exception) {
                        Log.e(TAG, "Task failed: " + exception);

                    } catch (InterruptedException exception) {
                        Log.e(TAG, "Interrupt occurred: " + exception);
                    }

                }

            } catch (ExecutionException exception) {
                Log.e(TAG, "Task failed: " + exception);

            } catch (InterruptedException exception) {
                Log.e(TAG, "Interrupt occurred: " + exception);
            }
        }
    }


}
