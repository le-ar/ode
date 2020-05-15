package com.example.andrei.ode;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class User {
    public static Map<Long, User> Users = new HashMap<>();
    public String FName, LName, VkID, Image;
    public long CRating, Rating;
    @PrimaryKey public long ID;
}

