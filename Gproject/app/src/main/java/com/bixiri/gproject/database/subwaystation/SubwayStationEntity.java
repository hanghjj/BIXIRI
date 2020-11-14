package com.bixiri.gproject.database.subwaystation;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"subwayId", "statnId", "statnNm"})
public class SubwayStationEntity {
    private int subwayId; // 노선 정보 ex) 1호선 : 1001
    private int statnId; // 역 정보, 앞의 4자리는 subwayId값인걸로 추정 ex) 서울역 : 1001000133
    @NonNull
    private String statnNm; // 역 이름, ~역 이라는 어미는 안붙는다 ex) 서울역 : 서울

    public SubwayStationEntity(int subwayId, int statnId, String statnNm) {
        this.subwayId = subwayId;
        this.statnId = statnId;
        this.statnNm = statnNm;
    }

    public int getSubwayId() {
        return subwayId;
    }

    public int getStatnId() {
        return statnId;
    }

    public String getStatnNm() {
        return statnNm;
    }
}