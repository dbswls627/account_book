package com.team_3.accountbook;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Asset.class, Way.class, Sort.class, Cost.class, AutoSave.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract sqlDao dao();

    //Room Database는 싱글톤 패턴을 해야한다.
    private static AppDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if(INSTANCE==null) {
                INSTANCE= Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "AccountBook_DB")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }
}