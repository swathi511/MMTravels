package com.hjsoft.mmtravels.model;

import java.io.Serializable;

/**
 * Created by hjsoft on 28/2/17.
 */
public class DutyData implements Serializable {


    String dslipid;
    String uddsno;
    String bookingtype;
    String startdate;
    Float starttime;
    String bookedvehicleid;
    String bookedvehicletype;
    String traveltype;
    String pointpointid;
    String pickuplocation;
    String slabname;
    String guestname;
    String guestmobile;
    String driverid;
    String acceptancestatus;
    Integer totkms;
    Integer tothrs;
    String jdetails;
    String sjdetails;
    String cjdetails;
    String otp;
    String signature;
    String file1;
    String file2;
    String file3;
    String file4;
    String status;
    boolean offlineBookingStatus;


    public DutyData(String dslipid,String uddsno,String bookingtype,String startdate,Float starttime,String bookedvehicleid,String bookedvehicletype,String traveltype,
                    String pointpointid,String pickuplocation,String slabname,String guestname,String guestmobile,String driverid,String acceptancestatus,String status,boolean offlineBookingStatus)
    {
        this.dslipid=dslipid;
        this.uddsno=uddsno;
        this.bookingtype=bookingtype;
        this.startdate=startdate;
        this.starttime=starttime;
        this.bookedvehicleid=bookedvehicleid;
        this.bookedvehicletype=bookedvehicletype;
        this.traveltype=traveltype;
        this.pointpointid=pointpointid;
        this.pickuplocation=pickuplocation;
        this.slabname=slabname;
        this.guestname=guestname;
        this.guestmobile=guestmobile;
        this.driverid=driverid;
        this.acceptancestatus=acceptancestatus;
        this.status=status;
        this.offlineBookingStatus=offlineBookingStatus;
    }

    public String getDslipid() {
        return dslipid;
    }

    public void setDslipid(String dslipid) {
        this.dslipid = dslipid;
    }

    public String getUddsno() {
        return uddsno;
    }

    public void setUddsno(String uddsno) {
        this.uddsno = uddsno;
    }

    public String getBookingtype() {
        return bookingtype;
    }

    public void setBookingtype(String bookingtype) {
        this.bookingtype = bookingtype;
    }

    public Float getStarttime() {
        return starttime;
    }

    public void setStarttime(Float starttime) {
        this.starttime = starttime;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getBookedvehicleid() {
        return bookedvehicleid;
    }

    public void setBookedvehicleid(String bookedvehicleid) {
        this.bookedvehicleid = bookedvehicleid;
    }

    public String getBookedvehicletype() {
        return bookedvehicletype;
    }

    public void setBookedvehicletype(String bookedvehicletype) {
        this.bookedvehicletype = bookedvehicletype;
    }

    public String getTraveltype() {
        return traveltype;
    }

    public void setTraveltype(String traveltype) {
        this.traveltype = traveltype;
    }

    public String getPointpointid() {
        return pointpointid;
    }

    public void setPointpointid(String pointpointid) {
        this.pointpointid = pointpointid;
    }

    public String getPickuplocation() {
        return pickuplocation;
    }

    public void setPickuplocation(String pickuplocation) {
        this.pickuplocation = pickuplocation;
    }

    public String getGuestname() {
        return guestname;
    }

    public void setGuestname(String guestname) {
        this.guestname = guestname;
    }

    public String getSlabname() {
        return slabname;
    }

    public void setSlabname(String slabname) {
        this.slabname = slabname;
    }

    public String getGuestmobile() {
        return guestmobile;
    }

    public void setGuestmobile(String guestmobile) {
        this.guestmobile = guestmobile;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getAcceptancestatus() {
        return acceptancestatus;
    }

    public void setAcceptancestatus(String acceptancestatus) {
        this.acceptancestatus = acceptancestatus;
    }

    public Integer getTotkms() {
        return totkms;
    }

    public void setTotkms(Integer totkms) {
        this.totkms = totkms;
    }

    public Integer getTothrs() {
        return tothrs;
    }

    public void setTothrs(Integer tothrs) {
        this.tothrs = tothrs;
    }

    public String getJdetails() {
        return jdetails;
    }

    public void setJdetails(String jdetails) {
        this.jdetails = jdetails;
    }

    public String getSjdetails() {
        return sjdetails;
    }

    public void setSjdetails(String sjdetails) {
        this.sjdetails = sjdetails;
    }

    public String getCjdetails() {
        return cjdetails;
    }

    public void setCjdetails(String cjdetails) {
        this.cjdetails = cjdetails;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getFile3() {
        return file3;
    }

    public void setFile3(String file3) {
        this.file3 = file3;
    }

    public String getFile4() {
        return file4;
    }

    public void setFile4(String file4) {
        this.file4 = file4;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOfflineBookingStatus() {
        return offlineBookingStatus;
    }

    public void setOfflineBookingStatus(boolean offlineBookingStatus) {
        this.offlineBookingStatus = offlineBookingStatus;
    }
}

