package com.example.gproject.database.busstop;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface BusStopDAO {
    @Query("SELECT DISTINCT busRouteNm FROM BusStopEntity")
    List<String> getAllBusRoute();

    @Query("SELECT busStopNm FROM BusStopEntity WHERE busRouteNm = :name")
    List<String> getAllBusStopWithRouteName(String name);

    @Query("SELECT * FROM BusStopEntity WHERE busRouteNm = :busRoute AND busStopNm = :busStop")
    BusStopEntity getWithRouteAndStop(String busRoute, String busStop);
}