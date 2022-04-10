package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Watch {

    @NonNull
    @PrimaryKey
    private String amountGoal;
    private String warning;
    private Boolean watchOnOff;

    public Watch(String amountGoal, String warning, boolean watchOnOff) {
        this.amountGoal = amountGoal;
        this.warning = warning;
        this.watchOnOff = watchOnOff;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public Boolean getWatchOnOff() {
        return watchOnOff;
    }

    public void setWatchOnOff(Boolean watchOnOff) {
        watchOnOff = watchOnOff;
    }

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
