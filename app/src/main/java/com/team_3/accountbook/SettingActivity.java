package com.team_3.accountbook;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SettingActivity extends AppCompatActivity {
    TextView add,list,sqlTest,wear,amountGoal_text;
    Intent intent;

    BottomNavigationView bottom_menu;
    @Override
    protected void onStart() {

        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.setting);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        wear = findViewById(R.id.wear);
        add = findViewById(R.id.add);
        list = findViewById(R.id.list);
        sqlTest = findViewById(R.id.sqlTest);
        amountGoal_text = findViewById(R.id.amountGoal_text);


        list.setOnClickListener((view)->{
            intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });

        sqlTest.setOnClickListener((view)->{
            intent = new Intent(this, SqlTestActivity.class);
            startActivity(intent);
        });
        wear.setOnClickListener((view)->{
            intent = new Intent(this, WearActivity.class);
            startActivity(intent);
        });
        amountGoal_text.setOnClickListener((view)->{
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_watch_amountgoal);

            showDialogOfAmountGoal(dialog);
        });
        bottom_menu = findViewById(R.id.bottom_menu);

        bottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {

            switch (menuItem.getItemId()) {
                case R.id.home:
                    intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.graph:
                    intent = new Intent(this, GraphActivity.class);
                    startActivity(intent);
                    break;
                case R.id.assets:
                    intent = new Intent(this, AssetsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting:
                    intent = new Intent(this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }

    private void showDialogOfAmountGoal(Dialog dialog) {
        dialog.show();
        EditText amountGoal;
        TextView cancel,save;
        amountGoal = dialog.findViewById(R.id.amountGoal);
        cancel = dialog.findViewById(R.id.cancel);
        save = dialog.findViewById(R.id.save);

       cancel.setOnClickListener((view)->{
           dialog.dismiss();
       });
        save.setOnClickListener((view)->{

            dialog.dismiss();
            Log.d("test",amountGoal.getText()+"!");
            new SendThread("/message_path", amountGoal.getText()+"!").start();
        });
    }
    class SendThread extends Thread {
        String path;
        String message;

        //constructor
        SendThread(String p, String msg) {
            path = p;
            message = msg;
        }

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, no problem.
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
                            Wearable.getMessageClient(SettingActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        Integer result = Tasks.await(sendMessageTask);



                    } catch (ExecutionException exception) {


                    } catch (InterruptedException exception) {

                    }

                }

            } catch (ExecutionException exception) {



            } catch (InterruptedException exception) {

            }
        }
    }
}