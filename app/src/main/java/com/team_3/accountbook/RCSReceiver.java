package com.team_3.accountbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RCSReceiver extends BroadcastReceiver
{
    private static final String RECEIVE_CHAT_INVITATION = "com.samsung.rcs.framework.instantmessaging.action.RECEIVE_CHAT_INVITATION";
    private static final String RECEIVE_PARTICIPANT_UPDATED = "com.samsung.rcs.framework.instantmessaging.action.RECEIVE_PARTICIPANT_UPDATED";
    private static final String RECEIVE_PARTICIPANT_INSERTED = "com.samsung.rcs.framework.instantmessaging.action.RECEIVE_PARTICIPANT_INSERTED";


    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("RCS Receiver","1");  //얘만 찍힘
        String action = intent.getAction();

        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            if (RECEIVE_PARTICIPANT_UPDATED.equals(action) || RECEIVE_PARTICIPANT_INSERTED.equals(action))
            {
                String participant = bundle.getString("participant");
                if (participant != null)
                {
                    String number = participant.substring(4); // get the string after "tel:"
                    Log.d("Chat number is: " , number);
                }
            }
            else if (RECEIVE_CHAT_INVITATION.equals(action))
            {
                String subject = bundle.getString("subject");
                if(subject != null)
                {
                    Log.d("Chat subject: " , subject);
                }
            }
        }
    }
}
