package com.team_3.accountbook;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface sqlDao {
    @Insert
    void insertAsset(Asset asset);

    @Query("INSERT INTO Way(wayName, wayBalance, FK_assetId) VALUES(:name, :balance, :id)")
    void insertWay(String name, int balance, int id);

    @Query("INSERT INTO Sort(sortName, sortDivision) VALUES(:name, :division)")
    void insertSort(String name, String division);

    @Query("INSERT INTO Cost(useDate, FK_wayName, sortName, amount, content, balance, division, ms) VALUES(:date, :FK_wayName, :sortName, :amount, :content, :balance, :division, :ms)")
    void insertCost(String date, String FK_wayName, String sortName, int amount, String content, int balance, String division, long ms);



    @Update
    void update(Asset asset);

    @Query("UPDATE Way SET wayBalance = wayBalance - :amount  WHERE wayName = :wayName")
    void updateWayBalanceOfEx(String wayName, int amount);

    @Query("UPDATE Way SET wayBalance = wayBalance + :amount  WHERE wayName = :wayName")
    void updateWayBalanceOfIn(String wayName, int amount);

    @Query("UPDATE Way SET wayBalance = :balance  WHERE wayName = :wayName")
    void updateWayBal(int balance, String wayName);

    @Query("UPDATE Cost " +
            "SET division = :division, useDate = :useDate, FK_wayName = :wayName, sortName = :sortName, amount = :amount, content = :content " +
            "WHERE costId = :costId")
    void updateCostInfo(int costId, String division, String useDate, String wayName, String sortName, int amount, String content);

    @Query("UPDATE Cost SET balance = balance + :margin WHERE FK_wayName = :wayName AND useDate >= :date")
    void updateCostUnderBalanceOfEx(String date, String wayName, int margin);

    @Query("UPDATE Cost SET balance = balance - :margin WHERE FK_wayName = :wayName AND useDate >= :date")
    void updateCostOverBalanceOfEx(String date, String wayName, int margin);

    @Query("UPDATE Cost SET balance = balance - :margin WHERE FK_wayName = :wayName AND useDate >= :date")
    void updateCostUnderBalanceOfIn(String date, String wayName, int margin);

    @Query("UPDATE Cost SET balance = balance + :margin WHERE FK_wayName = :wayName AND useDate >= :date")
    void updateCostOverBalanceOfIn(String date, String wayName, int margin);

    @Query("UPDATE Cost SET balance = balance - :margin WHERE FK_wayName = :wayName AND useDate > :date")
    void updateCostOverBalanceOfExNew(String date, String wayName, int margin);

    @Query("UPDATE Cost SET balance = balance + :margin WHERE FK_wayName = :wayName AND useDate > :date")
    void updateCostOverBalanceOfInNew(String date, String wayName, int margin);

    @Query("UPDATE Cost SET balance = :balance WHERE useDate = :useDate AND FK_wayName = :wayName")
    void updateNextBalance(int balance, String useDate, String wayName);

    @Query("UPDATE Cost SET balance = balance - :balance WHERE useDate = :useDate AND FK_wayName = :wayName")
    void updateNextBalance2(int balance, String useDate, String wayName);

//    @Query("UPDATE Cost SET balance = :balance WHERE FK_wayName = :wayName AND ")
//    void updateTest(int balance, String wayName, );




    @Delete
    void delete(Asset asset);


    @Query("DELETE FROM Asset")
    void deleteAssetAll();

    @Query("DELETE FROM Cost")
    void deleteCostAll();



    @Query("SELECT c.balance FROM Cost c WHERE c.useDate = :useDate")
    Integer getBalance(String useDate);

    @Query("SELECT c.balance FROM Cost c WHERE c.FK_wayName = :wayName AND usedate = :useDate AND c.content = :content ")
    Integer getCostBal(String wayName, String useDate, String content);

    @Query("SELECT * FROM Cost c WHERE c.useDate > :date AND c.FK_wayName = :wayName ORDER BY c.useDate ASC, c.content DESC")
    List<Cost> getUseDateAfter(String date, String wayName);

    @Query("SELECT * FROM Cost c WHERE c.useDate <= :date AND c.FK_wayName = :wayName ORDER BY c.useDate DESC, c.content ASC")
    List<Cost> getUseDatePre(String date, String wayName);

    @Query("SELECT * FROM Asset")
    List<Asset> getAssetAll();

    @Query("SELECT * FROM Way")
    List<Way> getWayAll();

    @Query("SELECT wayName FROM way")
    List<String> getWayNames();

    @Query("SELECT sortName FROM Sort WHERE sortDivision = :division")
    List<String> getSortNames(String division);

    @Query("SELECT * FROM Cost c ORDER BY useDate desc, c.content ASC")
    List<Cost> getCostAll();

    @Query("SELECT * FROM Cost c WHERE c.costId = :costId")
    Cost getCostAllOfCostId(int costId);

    @Query("SELECT (w.wayBalance - :amount) FROM Way w WHERE w.wayName = :wayName")
    Integer getAfterBalanceOfEx(int amount, String wayName);

    @Query("SELECT (w.wayBalance + :amount) FROM Way w WHERE w.wayName = :wayName")
    Integer getAfterBalanceOfIn(int amount, String wayName);

    @Query("SELECT sum(w.wayBalance) FROM Way w")
    Integer getTotalBalance();

    @Query("SELECT * FROM Cost c ORDER BY useDate desc, c.content ASC")
    List<Cost> getItemList();

    @Query("SELECT * FROM Cost c  WHERE substr(useDate,0,14) = :date ORDER BY useDate DESC, c.content ASC")        // 날짜에 맞는 값을 정렬하여 리턴
    List<Cost> getItemList(String date);

    @Query("SELECT * FROM Cost c  where substr(useDate,0,14) = :date ORDER BY useDate DESC, c.content ASC")        // 날짜에 맞는 값을 정렬하여 리턴
    List<Cost> getDate(String date);

    @Query("SELECT sum(amount) FROM Cost c  WHERE substr(useDate, 0, 14) = :date AND division = :division")    // 날짜에 맞는  amount 값의 합
    String getAmount(String date, String division);

    @Query("SELECT a.assetName, w.wayName, w.wayBalance FROM asset a INNER JOIN Way w ON a.assetId = w.FK_assetId")
    List<AssetNameWayNameAndBalance> getAnWnWb();

    @Query("SELECT ms FROM Cost c")    //모든 ms 값 배열
    List<Long> getMs();

    @Query("SELECT * FROM Cost c WHERE c.FK_wayName = :wayName ORDER BY c.useDate DESC, c.content ASC")
    List<Cost> getCostInWayName(String wayName);

    @Query("SELECT sum(amount) amount,sortName FROM Cost c WHERE substr(c.useDate,0,10) = :date group by sortName  ")
    List<graphDate> getGraphDate(String date);


    @Transaction
    @Query("SELECT * FROM Asset a INNER JOIN Way w ON a.assetId = w.FK_assetId")
    List<AssetWithWay> getAssetWithWays();

    @Transaction
    @Query("SELECT * FROM Cost c INNER JOIN Way w ON c.FK_wayName = w.wayName")
    List<WayWithCost> getWayWithCosts();

}
