package com.team_3.accountbook;

public class WayWithCost {
    private String wayName;        // 수단명
    private int wayBalance;        // 잔액
    private int FK_assetId;        // 자산 외래키
    private int costId;
    private int amount;             // 금액
    private String content;         // 내용
    private String useDate;         // 날짜
    private int balance;            // 잔액
    private String sortName;        // 분류명
    private String division;        // 구분
    private int FK_wayName;           // 수단 외래키


    public String getWayName() {
        return wayName;
    }

    public void setWayName(String wayName) {
        this.wayName = wayName;
    }

    public int getWayBalance() {
        return wayBalance;
    }

    public void setWayBalance(int wayBalance) {
        this.wayBalance = wayBalance;
    }

    public int getFK_assetId() {
        return FK_assetId;
    }

    public void setFK_assetId(int FK_assetId) {
        this.FK_assetId = FK_assetId;
    }

    public int getCostId() {
        return costId;
    }

    public void setCostId(int costId) {
        this.costId = costId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getFK_wayName() {
        return FK_wayName;
    }

    public void setFK_wayName(int FK_wayName) {
        this.FK_wayName = FK_wayName;
    }

    //private int FK_sortId;          // 분류 외래키
}
