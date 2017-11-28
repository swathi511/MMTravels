package com.hjsoft.mmtravels.webservices;

import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.model.DistancePojo;
import com.hjsoft.mmtravels.model.HomePojo;
import com.hjsoft.mmtravels.model.OTPPojo;
import com.hjsoft.mmtravels.model.Pojo;
import com.hjsoft.mmtravels.model.UpdatePojo;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hjsoft on 27/2/17.
 */
public interface API {

    @POST("Login/Checklogin")
    Call<Pojo> validate(@Body JsonObject v);

    @GET("dslip/GetdslipInfo")
    Call<List<HomePojo>> getInfo(@Query("login") String login,
                                 @Query("pwd") String pwd);

    @PUT("dslip/UpdateDslip")
    Call<UpdatePojo> sendAcceptStatus(@Body JsonObject v);

    @POST("OTP/GenerateOTP")
    Call<OTPPojo> putOTPRequest(@Body JsonObject v);

    @PUT("OTP/MatchOTP")
    Call<UpdatePojo> validateOTPRequest(@Body JsonObject v);

    @POST("Journey/UpdateJourneyInfo")
    Call<UpdatePojo> sendJourneyDetails(@Body JsonObject v);

    @POST("Upload/PostAsync")
    Call<List<String>> sendImages(@Query("dslipid") String dSlipId,
                                  @Query("driverid") String driverId,
                                  @Query("startdate") String startDate,
                                  @Body RequestBody b);

    @GET
    Call<DistancePojo> getDistanceDetails(@Url String urlString);
}
