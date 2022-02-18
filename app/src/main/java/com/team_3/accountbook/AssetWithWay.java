package com.team_3.accountbook;

public class AssetWithWay {
    private String wayName;        // 수단명
    private int wayBalance;        // 잔액
    private int FK_assetId;        // 자산 외래키
    private int assetId;
    private String assetName;     // 자산명


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

    public void setFK_assetId(int FK_assetId) {
        this.FK_assetId = FK_assetId;
    }

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
