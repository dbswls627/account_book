package com.team_3.accountbook;

public class item {
    private String msgDate;
    private String msgBody;
    private int msgAmount;

    public item(String date, String body, int amount){
        this.msgDate = date;
        this.msgBody = body;
        this.msgAmount = amount;
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

    public void setMsgBody(){ this.msgBody = msgBody; }

    public int getMsgAmount(){ return msgAmount; }

    public void setMsgAmount(){ this.msgAmount = msgAmount; }
}
