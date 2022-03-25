package com.team_3.accountbook;

import org.jetbrains.annotations.NotNull;


class GoalProgress {
    int current;    //현재값
    int goal;       //프로그래스 바 목표치
    public GoalProgress(int current, int goal) {
        this.current = current;
        this.goal = goal;
    }
    public float percentage() {
        float per = (float)this.current / (float)this.goal;
        if (per > 1) per = 1;
        return per;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}

public  class GoalsRepository {
    @NotNull
    private static final GoalProgress amountProgress = new GoalProgress(0, 300000);
    private static final GoalProgress dateProgress = new GoalProgress(0, 30);

    @NotNull
    public static final GoalProgress getAmountProgress() {
        return amountProgress;
    }
    public static final GoalProgress getDateProgress() {
        return dateProgress;
    }
}

