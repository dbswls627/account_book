package com.team_3.accountbook;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;


@Dao
public interface sqlDao {
    @Insert
    void insertAsset(Asset asset);

    @Query("INSERT INTO Way(wayName, wayBalance, FK_assetId) VALUES(:name, :balance, :id)")
    void insertWay(String name, int balance, int id);

    @Query("INSERT INTO Cost(amount, content, useDate, balance, sortName, division,FK_wayId) VALUES(:amount, :content, :date, :balance, :sortName, :division,:FK_wayID)")
    void insertCost(int amount, String content, String date, int balance, String sortName, String division,int FK_wayID);



    @Update
    void update(Asset asset);


    @Delete
    void delete(Asset asset);


    @Query("DELETE FROM Asset")
    void deleteAssetAll();



    @Query("SELECT * FROM Asset")
    List<Asset> getAssetAll();

    @Query("SELECT * FROM Way")
    List<Way> getWayAll();

    @Query("SELECT * FROM Cost c ORDER BY c.costId")
    List<Cost> getCostAll();



    @Transaction
    @Query("SELECT * FROM Asset a INNER JOIN Way w ON a.assetId = w.FK_assetId")
    List<AssetWithWays> getAssetWithWays();

    @Transaction
    @Query("SELECT * FROM Cost c INNER JOIN Way w ON c.FK_wayId = w.wayId")
    List<WayWithCosts> getWayWithCosts();


}
