package com.example.gproject.database.wolprofile;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WolProfileDAO {
    @Query("SELECT * FROM WolProfileEntity")
    List<WolProfileEntity> getAll();

    @Query("SELECT * FROM WolProfileEntity WHERE favorite = 1")
    List<WolProfileEntity> getAllFavorite();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WolProfileEntity db);

    @Update
    void update(WolProfileEntity db);

    @Delete
    void delete(WolProfileEntity db);
}
