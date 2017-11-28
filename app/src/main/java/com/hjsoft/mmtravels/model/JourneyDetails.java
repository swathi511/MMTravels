package com.hjsoft.mmtravels.model;

/**
 * Created by hjsoft on 8/11/16.
 */
public class JourneyDetails {

    String dsno,pickupLat,pickupLng,dropLat,dropLng,waypoints,driverId,startDate,allJdetails,jDetails,sJdetails,cJdetails,totHrs,idleTime,pause,gname,gmobile,startingTime,endingTime;


    public JourneyDetails(String dsno,String pickupLat,String pickupLng,String dropLat,String dropLng,String waypoints,String driverId,
                          String startDate,String allJdetails,String jDetails,String sJdetails,String cJdetails,String totHrs,String idleTime,
                          String pause,String gname,String gmobile,String startingTime,String endingTime)
    {
        this.dsno=dsno;
        this.pickupLat=pickupLat;
        this.pickupLng=pickupLng;
        this.dropLat=dropLat;
        this.dropLng=dropLng;
        this.waypoints=waypoints;
        this.driverId=driverId;
        this.startDate=startDate;
        this.allJdetails=allJdetails;
        this.jDetails=jDetails;
        this.sJdetails=sJdetails;
        this.cJdetails=cJdetails;
        this.totHrs=totHrs;
        this.idleTime=idleTime;
        this.pause=pause;
        this.gname=gname;
        this.gmobile=gmobile;
        this.startingTime=startingTime;
        this.endingTime=endingTime;
    }

    public String getDsno() {
        return dsno;
    }

    public void setDsno(String dsno) {
        this.dsno = dsno;
    }

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getDropLat() {
        return dropLat;
    }

    public void setDropLat(String dropLat) {
        this.dropLat = dropLat;
    }

    public String getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(String pickupLng) {
        this.pickupLng = pickupLng;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDropLng() {
        return dropLng;
    }

    public void setDropLng(String dropLng) {
        this.dropLng = dropLng;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getAllJdetails() {
        return allJdetails;
    }

    public void setAllJdetails(String allJdetails) {
        this.allJdetails = allJdetails;
    }

    public String getjDetails() {
        return jDetails;
    }

    public void setjDetails(String jDetails) {
        this.jDetails = jDetails;
    }

    public String getsJdetails() {
        return sJdetails;
    }

    public void setsJdetails(String sJdetails) {
        this.sJdetails = sJdetails;
    }

    public String getcJdetails() {
        return cJdetails;
    }

    public void setcJdetails(String cJdetails) {
        this.cJdetails = cJdetails;
    }

    public String getTotHrs() {
        return totHrs;
    }

    public void setTotHrs(String totHrs) {
        this.totHrs = totHrs;
    }

    public String getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(String idleTime) {
        this.idleTime = idleTime;
    }

    public String getPause() {
        return pause;
    }

    public void setPause(String pause) {
        this.pause = pause;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGmobile() {
        return gmobile;
    }

    public void setGmobile(String gmobile) {
        this.gmobile = gmobile;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }
}
