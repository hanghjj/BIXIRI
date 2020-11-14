package com.bixiri.gproject.database.busstop;

import androidx.room.Entity;

@Entity(primaryKeys = {"busRouteId", "org", "stId"})
public class BusStopEntity {
    private int busRouteId;
    private String busRouteNm;
    private int org;
    private int stId;
    private String busStopNm;

    public BusStopEntity(int busRouteId, String busRouteNm, int org, int stId, String busStopNm) {
        this.busRouteId = busRouteId;
        this.busRouteNm = busRouteNm;
        this.org = org;
        this.stId = stId;
        this.busStopNm = busStopNm;
    }

    public int getBusRouteId() {
        return busRouteId;
    }

    public String getBusRouteNm() {
        return busRouteNm;
    }

    public int getOrg() {
        return org;
    }

    public int getStId() {
        return stId;
    }

    public String getBusStopNm() {
        return busStopNm;
    }
}
