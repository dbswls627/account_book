package com.team_3.accountbook;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class AutoSave {
    @PrimaryKey private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
