package com.team_3.accountbook;

import org.jetbrains.annotations.NotNull;


class GoalProgress {
    int current;
    int goal;
    public GoalProgress(int current, int goal) {
        this.current = current;
        this.goal = goal;
    }
    public float percentage() {
        return (float)this.current / (float)this.goal;
    }
}

public  class GoalsRepository {
    @NotNull
    private static final GoalProgress goalProgress = new GoalProgress(0, 8000);

    @NotNull
    public static final GoalProgress getGoalProgress() {
        return goalProgress;
    }
}

