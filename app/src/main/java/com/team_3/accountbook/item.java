package com.team_3.accountbook;

public class item {
    private String msgDate;
    private String msgBody;
    private int msgAmount;
    private long ms;
    private int costId;

    public item(String date, String body, int amount, long ms , int costId){
        this.msgDate = date;
        this.msgBody = body;
        this.msgAmount = amount;
        this.ms = ms;
        this.costId = costId;
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

    public long getMs() { return ms; }

    public void setMs(long ms) { this.ms = ms; }

    public int getCostId() { return costId; }

    public void setCostId(int pk) { this.costId = pk; }
}
