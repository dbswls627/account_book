package com.team_3.accountbook;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;


public class AssetWithWays {
    @Embedded public Asset asset;
    @Relation(parentColumn = "assetId",
            entityColumn = "FK_assetId")
    public List<Way> way;



    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public List<Way> getWay() {
        return way;
    }

    public void setWay(List<Way> way) {
        this.way = way;
    }



}
