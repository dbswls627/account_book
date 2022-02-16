package com.team_3.accountbook;


public class AssetNameWayNameAndBalance {
    private String assetName;       // 자산명
    private String wayName;         // 수단명
    private int wayBalance;         // 수단 잔액


    public AssetNameWayNameAndBalance(String assetName, String wayName, int wayBalance) {
        this.assetName = assetName;
        this.wayName = wayName;
        this.wayBalance = wayBalance;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

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
}
