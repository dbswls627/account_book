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

    @Query("INSERT INTO Cost(useDate, FK_wayId, sortName, amount, content, balance, division, ms) VALUES(:date, :FK_wayID, :sortName, :amount, :content, :balance, :division, :ms)")
    void insertCost(String date, int FK_wayID, String sortName, int amount, String content, int balance, String division, long ms);



    @Update
    void update(Asset asset);


    @Delete
    void delete(Asset asset);


    @Query("DELETE FROM Asset")
    void deleteAssetAll();

    @Query("SELECT wayId FROM Way WHERE wayName = :name ")
    int getFk(String name);

    @Query("SELECT * FROM Asset")
    List<Asset> getAssetAll();

    @Query("SELECT * FROM Way")
    List<Way> getWayAll();

    @Query("SELECT * FROM Cost c ORDER BY useDate desc")
    List<Cost> getCostAll();

    @Query("SELECT * FROM Cost c  where substr(useDate,0,14) = :date ORDER BY useDate DESC")    //날짜에 맞는 값을 정렬하여 리턴
    List<Cost> getDate(String date);

    @Query("SELECT sum(amount) FROM Cost c  WHERE substr(useDate, 0, 14) = :date AND division = :division")    //날짜에 맞는  amount값의 합
    String getAmount(String date,String division);

    @Query("SELECT ms FROM Cost c")    //모든 ms 값 배열
    List<Long> getMs();

    @Transaction
    @Query("SELECT * FROM Asset a INNER JOIN Way w ON a.assetId = w.FK_assetId")
    List<AssetWithWay> getAssetWithWays();

    @Transaction
    @Query("SELECT * FROM Cost c INNER JOIN Way w ON c.FK_wayId = w.wayId")
    List<WayWithCost> getWayWithCosts();

}
