package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class AmountGoal {

    @NonNull
    @PrimaryKey private String amountGoal;

    public String getAmountGoal() {
        return amountGoal;
    }
    public void setAmountGoal(String amountGoal) {
        this.amountGoal = amountGoal;
    }

    public void setState(String amountGoal) {
        this.amountGoal = amountGoal;
    }
}
