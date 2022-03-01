package com.team_3.accountbook;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface sqlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Data data);


    @Delete
    void delete(Data data);


    @Query("SELECT amount from data where name = :name")
    String get(String name);



}
