package com.team_3.accountbook;

public class graphDate {
    private int amount;             // 금액
    private String sortName;        // 분류명

    public graphDate(int amount, String sortName) {
        this.amount = amount;
        this.sortName = sortName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }
}
