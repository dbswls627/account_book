package com.team_3.accountbook;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {Asset.class, Way.class, Cost.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract sqlDao dao();
}
