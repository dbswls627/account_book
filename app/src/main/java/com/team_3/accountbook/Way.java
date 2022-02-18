package com.team_3.accountbook;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(foreignKeys =
@ForeignKey(entity = Asset.class,
        parentColumns = "assetId",
        childColumns = "FK_assetId",
        onDelete = CASCADE,
        onUpdate = CASCADE))
public class Way {
    @NonNull
    @PrimaryKey private String wayName;        // 수단명
    private int wayBalance;        // 잔액
    private int FK_assetId;        // 자산 외래키



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

    public void setFK_assetId(int FK_assetsId) {
        this.FK_assetId = FK_assetsId;
    }
}
