package com.bixiri.gproject.database.menu;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MenuDAO {
    @Query("SELECT * FROM MenuEntity")
    List<MenuEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MenuEntity db);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MenuEntity> db);

    @Update
    void update(MenuEntity db);

    @Delete
    void delete(MenuEntity db);

    @Query("SELECT menuItem FROM MenuEntity WHERE day = :day AND mealtime = :mealtime AND cafeteria = :cafeteria")
    List<String> findMenu(int day, int mealtime, int cafeteria);

    @Query("DELETE FROM MenuEntity")
    void deleteAll();
}
