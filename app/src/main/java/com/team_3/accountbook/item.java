package com.team_3.accountbook;

public class item {
    private String msgDate;
    private String msgBody;

    public item(String date, String body){
        this.msgDate = date;
        this.msgBody = body;
    }

    public String getMsgDate(){
        return msgDate;
    }

    public void setMsgDate(){
        this.msgDate = msgDate;
    }

    public String getMsgBody(){
        return msgBody;
    }

    public void setMsgBody(){
        this.msgBody = msgBody;
    }
}
