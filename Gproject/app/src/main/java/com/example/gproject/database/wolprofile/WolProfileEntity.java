package com.example.gproject.database.wolprofile;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"name"})
public class WolProfileEntity {
    @NonNull
    private String name; // 프로필 이름
    private int favorite; // 프로필 즐겨찾기 여부, 0이면 false, 1: true, 즐겨찾기 되어있으면 메인화면에 띄워짐
    private String macAddress; // mac 주소
    private String ipAddress; // ip 주소
    private int port; // 포트번호

    public WolProfileEntity(String name, int favorite, String ipAddress, String macAddress, int port) {
        this.name = name;
        this.favorite = favorite;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.port = port;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
