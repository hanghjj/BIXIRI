package com.bixiri.gproject.database.subwaystation;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface SubwayStationDAO {
    @Query("SELECT * FROM SubwayStationEntity")
    List<SubwayStationEntity> getAll();

    @Query("SELECT DISTINCT statnNm FROM SubwayStationEntity")
    List<String> getAllNames();

    @Query("SELECT * FROM SubwayStationEntity WHERE statnNm = :name")
    List<SubwayStationEntity> getAllWithName(String name);

    @Query("SELECT DISTINCT subwayId FROM SubwayStationEntity WHERE statnNm = :name")
    List<Integer> getAllLinesWithName(String name);

    @Query("SELECT DISTINCT statnNm FROM SubwayStationEntity WHERE subwayId IN (:lines)")
    List<String> getAllNamesWithLines(List<Integer> lines);

    @Query("SELECT statnId FROM SubwayStationEntity WHERE statnNm = :name AND subwayId = :line")
    int getIdWithNameAndLine(String name, int line);
}
