package com.hjsoft.mmtravels.model;

/**
 * Created by hjsoft on 28/10/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class HomePojo {

    @SerializedName("dslipid")
    @Expose
    private String dslipid;
    @SerializedName("uddsno")
    @Expose
    private String uddsno;
    @SerializedName("bookingtype")
    @Expose
    private String bookingtype;
    @SerializedName("startdate")
    @Expose
    private String startdate;
    @SerializedName("starttime")
    @Expose
    private Float starttime;
    @SerializedName("bookedvehicleid")
    @Expose
    private String bookedvehicleid;
    @SerializedName("bookedvehicletype")
    @Expose
    private String bookedvehicletype;
    @SerializedName("traveltype")
    @Expose
    private String traveltype;
    @SerializedName("pointpointid")
    @Expose
    private String pointpointid;
    @SerializedName("pickuplocation")
    @Expose
    private String pickuplocation;
    @SerializedName("slabname")
    @Expose
    private String slabname;
    @SerializedName("guestname")
    @Expose
    private String guestname;
    @SerializedName("guestmobile")
    @Expose
    private String guestmobile;
    @SerializedName("driverid")
    @Expose
    private String driverid;
    @SerializedName("acceptancestatus")
    @Expose
    private String acceptancestatus;
    @SerializedName("totkms")
    @Expose
    private Integer totkms;
    @SerializedName("tothrs")
    @Expose
    private Integer tothrs;
    @SerializedName("jdetails")
    @Expose
    private Object jdetails;
    @SerializedName("sjdetails")
    @Expose
    private Object sjdetails;
    @SerializedName("cjdetails")
    @Expose
    private Object cjdetails;
    @SerializedName("otp")
    @Expose
    private Object otp;
    @SerializedName("signature")
    @Expose
    private Object signature;
    @SerializedName("file1")
    @Expose
    private Object file1;
    @SerializedName("file2")
    @Expose
    private Object file2;
    @SerializedName("file3")
    @Expose
    private Object file3;
    @SerializedName("file4")
    @Expose
    private Object file4;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("Garagestatus")
    @Expose
    private String Garagestatus;
    @SerializedName("Latitude")
    @Expose
    private String Latitude;
    @SerializedName("Longitude")
    @Expose
    private String Longitude;

    /**
     *
     * @return
     * The dslipid
     */
    public String getDslipid() {
        return dslipid;
    }

    /**
     *
     * @param dslipid
     * The dslipid
     */
    public void setDslipid(String dslipid) {
        this.dslipid = dslipid;
    }

    /**
     *
     * @return
     * The uddsno
     */
    public String getUddsno() {
        return uddsno;
    }

    /**
     *
     * @param uddsno
     * The uddsno
     */
    public void setUddsno(String uddsno) {
        this.uddsno = uddsno;
    }

    /**
     *
     * @return
     * The bookingtype
     */
    public String getBookingtype() {
        return bookingtype;
    }

    /**
     *
     * @param bookingtype
     * The bookingtype
     */
    public void setBookingtype(String bookingtype) {
        this.bookingtype = bookingtype;
    }

    /**
     *
     * @return
     * The startdate
     */
    public String getStartdate() {
        return startdate;
    }

    /**
     *
     * @param startdate
     * The startdate
     */
    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    /**
     *
     * @return
     * The starttime
     */
    public Float getStarttime() {
        return starttime;
    }

    /**
     *
     * @param starttime
     * The starttime
     */
    public void setStarttime(Float starttime) {
        this.starttime = starttime;
    }

    /**
     *
     * @return
     * The bookedvehicleid
     */
    public String getBookedvehicleid() {
        return bookedvehicleid;
    }

    /**
     *
     * @param bookedvehicleid
     * The bookedvehicleid
     */
    public void setBookedvehicleid(String bookedvehicleid) {
        this.bookedvehicleid = bookedvehicleid;
    }

    /**
     *
     * @return
     * The bookedvehicletype
     */
    public String getBookedvehicletype() {
        return bookedvehicletype;
    }

    /**
     *
     * @param bookedvehicletype
     * The bookedvehicletype
     */
    public void setBookedvehicletype(String bookedvehicletype) {
        this.bookedvehicletype = bookedvehicletype;
    }

    /**
     *
     * @return
     * The traveltype
     */
    public String getTraveltype() {
        return traveltype;
    }

    /**
     *
     * @param traveltype
     * The traveltype
     */
    public void setTraveltype(String traveltype) {
        this.traveltype = traveltype;
    }

    /**
     *
     * @return
     * The pointpointid
     */
    public String getPointpointid() {
        return pointpointid;
    }

    /**
     *
     * @param pointpointid
     * The pointpointid
     */
    public void setPointpointid(String pointpointid) {
        this.pointpointid = pointpointid;
    }

    /**
     *
     * @return
     * The pickuplocation
     */
    public String getPickuplocation() {
        return pickuplocation;
    }

    /**
     *
     * @param pickuplocation
     * The pickuplocation
     */
    public void setPickuplocation(String pickuplocation) {
        this.pickuplocation = pickuplocation;
    }

    /**
     *
     * @return
     * The slabname
     */
    public String getSlabname() {
        return slabname;
    }

    /**
     *
     * @param slabname
     * The slabname
     */
    public void setSlabname(String slabname) {
        this.slabname = slabname;
    }

    /**
     *
     * @return
     * The guestname
     */
    public String getGuestname() {
        return guestname;
    }

    /**
     *
     * @param guestname
     * The guestname
     */
    public void setGuestname(String guestname) {
        this.guestname = guestname;
    }

    /**
     *
     * @return
     * The guestmobile
     */
    public String getGuestmobile() {
        return guestmobile;
    }

    /**
     *
     * @param guestmobile
     * The guestmobile
     */
    public void setGuestmobile(String guestmobile) {
        this.guestmobile = guestmobile;
    }

    /**
     *
     * @return
     * The driverid
     */
    public String getDriverid() {
        return driverid;
    }

    /**
     *
     * @param driverid
     * The driverid
     */
    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    /**
     *
     * @return
     * The acceptancestatus
     */
    public String getAcceptancestatus() {
        return acceptancestatus;
    }

    /**
     *
     * @param acceptancestatus
     * The acceptancestatus
     */
    public void setAcceptancestatus(String acceptancestatus) {
        this.acceptancestatus = acceptancestatus;
    }

    /**
     *
     * @return
     * The totkms
     */
    public Integer getTotkms() {
        return totkms;
    }

    /**
     *
     * @param totkms
     * The totkms
     */
    public void setTotkms(Integer totkms) {
        this.totkms = totkms;
    }

    /**
     *
     * @return
     * The tothrs
     */
    public Integer getTothrs() {
        return tothrs;
    }

    /**
     *
     * @param tothrs
     * The tothrs
     */
    public void setTothrs(Integer tothrs) {
        this.tothrs = tothrs;
    }

    /**
     *
     * @return
     * The jdetails
     */
    public Object getJdetails() {
        return jdetails;
    }

    /**
     *
     * @param jdetails
     * The jdetails
     */
    public void setJdetails(Object jdetails) {
        this.jdetails = jdetails;
    }

    /**
     *
     * @return
     * The sjdetails
     */
    public Object getSjdetails() {
        return sjdetails;
    }

    /**
     *
     * @param sjdetails
     * The sjdetails
     */
    public void setSjdetails(Object sjdetails) {
        this.sjdetails = sjdetails;
    }

    /**
     *
     * @return
     * The cjdetails
     */
    public Object getCjdetails() {
        return cjdetails;
    }

    /**
     *
     * @param cjdetails
     * The cjdetails
     */
    public void setCjdetails(Object cjdetails) {
        this.cjdetails = cjdetails;
    }

    /**
     *
     * @return
     * The otp
     */
    public Object getOtp() {
        return otp;
    }

    /**
     *
     * @param otp
     * The otp
     */
    public void setOtp(Object otp) {
        this.otp = otp;
    }

    /**
     *
     * @return
     * The signature
     */
    public Object getSignature() {
        return signature;
    }

    /**
     *
     * @param signature
     * The signature
     */
    public void setSignature(Object signature) {
        this.signature = signature;
    }

    /**
     *
     * @return
     * The file1
     */
    public Object getFile1() {
        return file1;
    }

    /**
     *
     * @param file1
     * The file1
     */
    public void setFile1(Object file1) {
        this.file1 = file1;
    }

    /**
     *
     * @return
     * The file2
     */
    public Object getFile2() {
        return file2;
    }

    /**
     *
     * @param file2
     * The file2
     */
    public void setFile2(Object file2) {
        this.file2 = file2;
    }

    /**
     *
     * @return
     * The file3
     */
    public Object getFile3() {
        return file3;
    }

    /**
     *
     * @param file3
     * The file3
     */
    public void setFile3(Object file3) {
        this.file3 = file3;
    }

    /**
     *
     * @return
     * The file4
     */
    public Object getFile4() {
        return file4;
    }

    /**
     *
     * @param file4
     * The file4
     */
    public void setFile4(Object file4) {
        this.file4 = file4;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGaragestatus() {
        return Garagestatus;
    }

    public void setGaragestatus(String garagestatus) {
        Garagestatus = garagestatus;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
