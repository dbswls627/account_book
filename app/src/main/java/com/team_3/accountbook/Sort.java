package com.team_3.accountbook;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Sort {
    @PrimaryKey(autoGenerate = true) private int sortId;    // 분류 id
    private String sortName;                                // 분류명
    private String sortDivision;                            // 구분(expense, income)


    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortDivision() {
        return sortDivision;
    }

    public void setSortDivision(String sortDivision) {
        this.sortDivision = sortDivision;
    }
}
