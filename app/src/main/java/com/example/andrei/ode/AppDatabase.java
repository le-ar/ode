package com.example.andrei.ode;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Event.class, User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao getEventDao();
    public abstract UserDao getUserDao();
}
