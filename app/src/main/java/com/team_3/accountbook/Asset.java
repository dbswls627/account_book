package com.team_3.accountbook;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Asset {
    @PrimaryKey(autoGenerate = true) private int assetId;
    private String assetName;     // 자산명


    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
