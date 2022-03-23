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

    @Query("INSERT INTO Way(wayName, wayBalance, FK_assetId, wayMemo, phoneNumber, delimiter) VALUES(:name, :balance, :id, :memo, :pn, :delimiter)")
    void insertWayAll(String name, int balance, int id, String memo, String pn, String delimiter);

    @Query("INSERT INTO Sort(sortName, sortDivision) VALUES(:name, :division)")
    void insertSort(String name, String division);

    @Query("INSERT INTO Cost(useDate, FK_wayName, sortName, amount, content, balance, division, ms) " +
            "VALUES(:date, :FK_wayName, :sortName, :amount, :content, :balance, :division, :ms)")
    void insertCost(String date, String FK_wayName, String sortName, int amount, String content, int balance, String division, long ms);

    @Query("INSERT INTO AutoSave(state) VALUES(:state)")
    void insertAutoState(Boolean state);



    @Update
    void update(Asset asset);



    // AddActivity 에서 사용
    @Query("UPDATE Way SET wayBalance = :bal  WHERE wayName = :wayName")
    void updateWayBal(int bal, String wayName);

    @Query("UPDATE Way SET wayBalance = wayBalance + :amount  WHERE wayName = :wayName")
    void updateWayBal2(int amount, String wayName);

    @Query("UPDATE Cost SET balance = balance + :amount  WHERE costId = :costId")
    void update_NextCostBal(int amount, int costId);

    @Query("UPDATE Cost SET useDate = :useDate, FK_wayName = :wayName, sortName = :sortName, " +
                            "amount = :amount, balance = :balance, content = :content, division = :division, ms = :ms WHERE costId = :costId")
    void update_CostData(String useDate, String wayName, String sortName, int amount, int balance, String content, String division, long ms, int costId);


    @Query("UPDATE Way " +
            "SET FK_assetId = :id, wayName = :name, wayBalance = :balance, wayMemo = :memo, phoneNumber = :phoneNumber, delimiter = :delimiter " +
            "WHERE wayName = :wayName")
    void updateWayData(int id, String name, int balance, String memo, String phoneNumber, String delimiter, String wayName);

    @Query("UPDATE Cost SET FK_wayName = :afterName WHERE FK_wayName = :beforeName")
    void updateCostWayName(String beforeName, String afterName);


    @Query("UPDATE AutoSave SET state = :stateFlag")
    void updateAutoState(Boolean stateFlag);






    @Delete
    void delete(Asset asset);


    @Query("DELETE FROM Asset")
    void deleteAssetAll();

    @Query("DELETE FROM Cost")
    void deleteCostAll();

    @Query("DELETE FROM Cost WHERE costId = :costId")
    void deleteCostData(int costId);

    @Query("DELETE FROM Way WHERE wayName = :wayName")
    void deleteWayData(String wayName);



    @Query("SELECT c.balance FROM Cost c WHERE c.costId = :costId")
    Integer getCostBalance(int costId);

    @Query("SELECT c.amount FROM Cost c WHERE c.costId = :costId")
    Integer getCostAmount(int costId);

    @Query("SELECT c.division FROM Cost c WHERE c.costId = :costId")
    String getCostDivision(int costId);

    @Query("SELECT w.wayBalance FROM Way w WHERE w.wayName = :wayName")
    Integer getWayBalance(String wayName);

    @Query("SELECT c.balance FROM Cost c WHERE c.FK_wayName = :wayName AND usedate = :useDate AND c.content = :content ")
    Integer getCostBal(String wayName, String useDate, String content);



    // 내 이후 날짜(내 날짜 제외)의 데이터들 가져오기
    @Query("SELECT * FROM Cost c " +
            "WHERE :date < c.useDate AND c.FK_wayName = :wayName " +
            "ORDER BY c.useDate ASC, c.content DESC, c.costId DESC")
    List<Cost> getCostDataAfter(String date, String wayName);

    // 같은 날짜의 내 이후(내 내용 포함) 데이터들 가져오기
    @Query("SELECT * FROM Cost c " +
            "WHERE :date = c.useDate AND :content >= c.content AND c.FK_wayName = :wayName " +
            "ORDER BY c.useDate ASC, c.content DESC, c.costId DESC")
    List<Cost> getNowAfter(String date, String content, String wayName);

    // 같은 날짜의 내 이후(내 내용 제외) 데이터들 가져오기
    @Query("SELECT * FROM Cost c " +
            "WHERE :date = c.useDate AND :content > c.content AND c.FK_wayName = :wayName " +
            "ORDER BY c.useDate ASC, c.content DESC, c.costId DESC")
    List<Cost> getNowAfter2(String date, String content, String wayName);

    // 같은 날짜, 같은 내용의 내 이후 데이터들 가져오기
    @Query("SELECT * FROM Cost c " +
            "WHERE :date = c.useDate AND :content = c.content AND :costId > c.costId AND c.FK_wayName = :wayName " +
            "ORDER BY c.useDate ASC, c.content DESC, c.costId DESC")
    List<Cost> getNowAfter_hard(String date, String content, int costId, String wayName);


    // 내 이전 날짜(내 날짜 제외)의 데이터들 가져오기
    @Query("SELECT * FROM Cost c " +
            "WHERE :date > c.useDate AND c.FK_wayName = :wayName " +
            "ORDER BY c.useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getCostDataPre(String date, String wayName);

    // 같은 날짜의 내 이전(내 내용 제외) 데이터들 가져오기
    @Query("SELECT * FROM Cost c " +
            "WHERE :date = c.useDate AND :content < c.content AND c.FK_wayName = :wayName " +
            "ORDER BY c.useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getNowPre(String date, String content, String wayName);

    // 같은 날짜, 같은 내용의 내 이전(내 내용 포함) 데이터들 가져오기
    @Query("SELECT * FROM Cost c " +
            "WHERE :date = c.useDate AND :content = c.content AND :costId < c.costId AND c.FK_wayName = :wayName " +
            "ORDER BY c.useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getNowPre_hard(String date, String content, int costId, String wayName);


    @Query("SELECT * FROM Cost c " +
            "WHERE :date < c.useDate AND c.FK_wayName = :wayName AND c.costId <> :costId " +
            "ORDER BY c.useDate ASC, c.content DESC, c.costId DESC")
    List<Cost> getCostDataAfter_forChange(String date, String wayName, int costId);

    @Query("SELECT * FROM Cost c " +
            "WHERE :date = c.useDate AND :content > c.content AND c.FK_wayName = :wayName AND c.costId <> :costId " +
            "ORDER BY c.useDate ASC, c.content DESC, c.costId DESC")
    List<Cost> getNowAfter2_forChange(String date, String content, String wayName, int costId);


    @Query("SELECT * FROM Cost c " +
            "WHERE :date > c.useDate AND c.FK_wayName = :wayName AND c.costId <> :costId " +
            "ORDER BY c.useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getCostDataPre_forChange(String date, String wayName, int costId);

    @Query("SELECT * FROM Cost c " +
            "WHERE :date = c.useDate AND :content < c.content AND c.FK_wayName = :wayName AND c.costId <> :costId " +
            "ORDER BY c.useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getNowPre_forChange(String date, String content, String wayName, int costId);




    @Query("SELECT * FROM Asset")
    List<Asset> getAssetAll();

    @Query("SELECT * FROM Way")
    List<Way> getWayAll();

    @Query("SELECT wayName FROM way")
    List<String> getWayNames();

    @Query("SELECT sortName FROM Sort WHERE sortDivision = :division")
    List<String> getSortNames(String division);

    @Query("SELECT * FROM Cost c ORDER BY useDate desc, c.content ASC, c.costId ASC")
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

    @Query("SELECT * FROM Cost c  WHERE substr(useDate,0,14) = :date ORDER BY useDate DESC, c.content ASC, c.costId ASC")        // 날짜에 맞는 값을 정렬하여 리턴
    List<Cost> getItemList(String date);

    @Query("SELECT * FROM Cost c  where substr(useDate,0,14) = :date ORDER BY useDate DESC, c.content ASC, c.costId ASC")        // 날짜에 맞는 값을 정렬하여 리턴
    List<Cost> getDate(String date);

    @Query("SELECT sum(amount) FROM Cost c  WHERE substr(useDate, 0, 14) = :date AND division = :division")    // 날짜에 맞는  amount 값의 합
    String getAmountOfDay(String date, String division);

    @Query("SELECT sum(amount) FROM Cost c  WHERE substr(useDate, 0, 10) = :date AND division = :division")    // 날짜에 맞는  amount 값의 합
    String getAmountOfMonth(String date, String division);


    @Query("SELECT a.assetName, w.wayName, w.wayBalance FROM asset a INNER JOIN Way w ON a.assetId = w.FK_assetId")
    List<AssetNameWayNameAndBalance> getAnWnWb();

    @Query("SELECT ms FROM Cost c")    //모든 ms 값 배열
    List<Long> getMs();

    @Query("SELECT * FROM Cost c WHERE c.FK_wayName = :wayName ORDER BY c.useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getCostInWayName(String wayName);

    @Query("SELECT sum(amount) amount,sortName FROM Cost c WHERE substr(c.useDate,0,10) = :date  and division = :division group by sortName order by sum(amount)")
    List<graphDate> getGraphDate(String date,String division);

    @Query("SELECT * FROM Cost c WHERE substr(c.useDate,0,10) = :date and division = :division ORDER BY useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getMDate(String date,String division);

    @Query("SELECT * FROM Cost c WHERE substr(c.useDate,0,10) = :date and sortName = :sortName  and division = :division ORDER BY useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getMDate(String date,String sortName,String division);

    @Query("SELECT * FROM Cost c WHERE substr(c.useDate,0,10) = :date AND c.FK_wayName = :wayName ORDER BY c.useDate DESC, c.content ASC, c.costId ASC")
    List<Cost> getCostOfMonthAndWayName(String date, String wayName);

    @Query("SELECT sum(c.amount) FROM Cost c WHERE substr(c.useDate,0,10) = :date AND c.FK_wayName = :wayName AND c.division = :division")
    Integer getAmountOfMonth(String date, String wayName, String division);

    @Query("SELECT * FROM Way w WHERE w.wayName = :wayName")
    Way getWayData(String wayName);

    @Query("SELECT a.assetName FROM Asset a WHERE a.assetId = :assetId")
    String getAssetName(int assetId);

    @Query("SELECT a.assetName FROM Asset a")
    List<String> getAssetNameAll();

    @Query("SELECT a.assetId FROM Asset a WHERE a.assetName = :name")
    Integer getAssetId(String name);

    @Query("SELECT w.wayName FROM Way w WHERE w.phoneNumber = :phoneNumber")
    List<String> getWayName(String phoneNumber);

    @Query("SELECT w.wayName FROM Way w WHERE w.phoneNumber = :phoneNumber AND w.delimiter = :delimiter")
    String getWayNameDetail(String phoneNumber, String delimiter);

    @Query("SELECT w.delimiter FROM Way w WHERE w.phoneNumber = :phoneNumber")
    List<String> getWayDelimiter(String phoneNumber);

    @Query("SELECT a.state FROM AutoSave a")
    Boolean getAutoState();


    @Transaction
    @Query("SELECT * FROM Asset a INNER JOIN Way w ON a.assetId = w.FK_assetId")
    List<AssetWithWay> getAssetWithWays();

    @Transaction
    @Query("SELECT * FROM Cost c INNER JOIN Way w ON c.FK_wayName = w.wayName")
    List<WayWithCost> getWayWithCosts();

}
