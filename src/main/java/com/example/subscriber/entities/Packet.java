package com.example.subscriber.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("parsedPackets")
public class Packet {
    @Id
    private String id;
    private long timestamp;
    private String snifferId;
    private String snifferMac;
    private String snifferName;
    private String snifferBuilding;
    private String snifferBuildingId;
    private String snifferRoom;
    private String snifferRoomId;
    private String deviceMac;
    private boolean global;
    private int sequenceNumber;
    private int rssi;
    private String fcs;
    private String ssid;
    private int ssidLen;
    private int year;
    private int month;
    private int weekOfYear; //week of year
    private int dayOfMonth;
    private int dayOfWeek;
    private int hour;
    private int quarter;
    private int tenMinute;
    private int fiveMinute;
    private int minute;

    public Packet(long timestamp, String snifferMac, String deviceMac, boolean global, int sequenceNumber, String ssid, int ssidLen) {
        this.timestamp = timestamp;
        this.snifferMac = snifferMac;
        this.deviceMac = deviceMac;
        this.global = global;
        this.sequenceNumber = sequenceNumber;
        this.ssid = ssid;
        this.ssidLen = ssidLen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSnifferMac() {
        return snifferMac;
    }

    public void setSnifferMac(String snifferMac) {
        this.snifferMac = snifferMac;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getSsidLen() {
        return ssidLen;
    }

    public void setSsidLen(int ssidLen) {
        this.ssidLen = ssidLen;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public int getTenMinute() {
        return tenMinute;
    }

    public void setTenMinute(int tenMinute) {
        this.tenMinute = tenMinute;
    }

    public int getFiveMinute() {
        return fiveMinute;
    }

    public void setFiveMinute(int fiveMinute) {
        this.fiveMinute = fiveMinute;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getSnifferName() {
        return snifferName;
    }

    public void setSnifferName(String snifferName) {
        this.snifferName = snifferName;
    }

    public String getSnifferBuilding() {
        return snifferBuilding;
    }

    public void setSnifferBuilding(String snifferBuilding) {
        this.snifferBuilding = snifferBuilding;
    }

    public String getSnifferRoom() {
        return snifferRoom;
    }

    public void setSnifferRoom(String snifferRoom) {
        this.snifferRoom = snifferRoom;
    }

    public String getSnifferId() {
        return snifferId;
    }

    public void setSnifferId(String snifferId) {
        this.snifferId = snifferId;
    }

    public String getSnifferBuildingId() {
        return snifferBuildingId;
    }

    public void setSnifferBuildingId(String snifferBuildingId) {
        this.snifferBuildingId = snifferBuildingId;
    }

    public String getSnifferRoomId() {
        return snifferRoomId;
    }

    public void setSnifferRoomId(String snifferRoomId) {
        this.snifferRoomId = snifferRoomId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getFcs() {
        return fcs;
    }

    public void setFcs(String fcs) {
        this.fcs = fcs;
    }
}
