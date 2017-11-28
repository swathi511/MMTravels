package com.hjsoft.mmtravels.model;

/**
 * Created by hjsoft on 27/2/17.
 */
public class DutyUpdates {

    String dsNo,travelType,rDate,rTime,driverId,startTime,stopTime,gName,gMobile,idleTimeDiff;
    int totKms,idleTime;
    double lat,lng;

    public DutyUpdates(String dsNo,String travelType,String driverId,String rDate,String rTime,int totKms,String startTime,String stopTime,
                       String gName,String gMobile,double lat,double lng,int idleTime,String idleTimeDiff)
    {
        this.dsNo=dsNo;
        this.travelType=travelType;
        this.driverId=driverId;
        this.rDate=rDate;
        this.rTime=rTime;
        this.totKms=totKms;
        this.startTime=startTime;
        this.stopTime=stopTime;
        this.gName=gName;
        this.gMobile=gMobile;
        this.lat=lat;
        this.lng=lng;
        this.idleTime=idleTime;
        this.idleTimeDiff=idleTimeDiff;
    }

    public String getDsNo() {
        return dsNo;
    }

    public void setDsNo(String dsNo) {
        this.dsNo = dsNo;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }

    public String getrTime() {
        return rTime;
    }

    public void setrTime(String rTime) {
        this.rTime = rTime;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgMobile() {
        return gMobile;
    }

    public void setgMobile(String gMobile) {
        this.gMobile = gMobile;
    }

    public String getIdleTimeDiff() {
        return idleTimeDiff;
    }

    public void setIdleTimeDiff(String idleTimeDiff) {
        this.idleTimeDiff = idleTimeDiff;
    }

    public int getTotKms() {
        return totKms;
    }

    public void setTotKms(int totKms) {
        this.totKms = totKms;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

