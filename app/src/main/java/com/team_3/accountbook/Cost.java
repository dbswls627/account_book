package com.team_3.accountbook;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(
        foreignKeys = {
                @ForeignKey(entity = Way.class,
                        parentColumns = "wayId",
                        childColumns = "FK_wayId",
                        onUpdate = CASCADE)
        }
)
public class Cost {
    @PrimaryKey(autoGenerate = true) private int costId;
    private int amount;             // 금액
    private String content;         // 내용
    private String useDate;         // 날짜
    private int balance;            // 잔액
    private String sortName;        // 분류명
    private String division;        // 구분
    private int FK_wayId;           // 수단 외래키
    //private int FK_sortId;          // 분류 외래키


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

    public int getFK_wayId() {
        return FK_wayId;
    }

    public void setFK_wayId(int FK_wayId) {
        this.FK_wayId = FK_wayId;
    }



}
