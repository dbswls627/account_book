package com.team_3.accountbook;

public class item {
    private int costId;         // Cost 테이블 PK
    private int amount;         // 금액
    private String content;     // 내용
    private String useDate;     // 날짜
    private long ms;            //

    public item(String useDate, String content, int amount, long ms , int costId){
        this.useDate = useDate;
        this.content = content;
        this.amount = amount;
        this.ms = ms;
        this.costId = costId;
    }

    public String getUseDate(){
        return useDate;
    }

    public void setUseDate(){
        this.useDate = useDate;
    }

    public String getContent(){
        return content;
    }

    public void setContent(){ this.content = content; }

    public int getAmount(){ return amount; }

    public void setAmount(){ this.amount = amount; }

    public long getMs() { return ms; }

    public void setMs(long ms) { this.ms = ms; }

    public int getCostId() { return costId; }

    public void setCostId(int pk) { this.costId = pk; }
}
