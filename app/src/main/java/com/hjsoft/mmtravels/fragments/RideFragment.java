package com.hjsoft.mmtravels.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.GPSTracker;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.activity.HomeActivity;
import com.hjsoft.mmtravels.activity.ResultActivity;
import com.hjsoft.mmtravels.activity.SignatureActivity;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.model.Distance;
import com.hjsoft.mmtravels.model.DistancePojo;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.Leg;
import com.hjsoft.mmtravels.model.OTPPojo;
import com.hjsoft.mmtravels.model.Route;
import com.hjsoft.mmtravels.model.UpdatePojo;
import com.hjsoft.mmtravels.webservices.API;
import com.hjsoft.mmtravels.webservices.RestClient;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 28/2/17.
 */
public class RideFragment extends Fragment implements OnMapReadyCallback {

    View rootView;
    private GoogleMap mMap;
    protected Location mLastLocation;
    double latitude1,current_lat=0.0;
    double longitude1,current_long=0.0;
    SupportMapFragment mapFragment;
    protected LocationRequest mLocationRequest;
    LatLng lastloc,curntloc;
    Marker marker;
    final static int REQUEST_LOCATION = 199;
    protected NetworkInfo n;
    String startingTime,endingTime,pauseTime,moveTime,currentTime;
    protected boolean mRequestingLocationUpdates;
    float[] results=new float[3];
    ConnectivityManager connMgr;
    Button btStart,btFinish,btPickUp,btDrop,btValidate;
    ImageButton ibStop;
    Geocoder geocoder;
    List<Address> addresses;
    String complete_address="";
    boolean btStartState,btStopState;
    long diff=0;
    TextView tvPlace,tvNetwork;
    SimpleDateFormat dateFormat;
    String timeUpdated,startTimeUpdated="",pickTimeUpdated="",dropTimeUpdated="",finishTimeUpdated="";
    String stBeforePickUp="",stAfterPickUp="",stAfterDrop="";
    int count=0;
    API REST_CLIENT;
    Animation animAlpha;
    String signature=null;
    //String timelap="guest board";
    //boolean isPicked=false,enter=true;
    String pickTime;
    ImageButton ibCall;
    String gNumber;
    boolean entered=false;
    ArrayList<DutyData> myList;
    DutyData data;
    DBAdapter dbAdapter;
    String diff_times=" ";
    TextView tvBtype,tvRdate,tvRtime,tvGname,tvGmobile;
    boolean isMarkerRotating=false;
    LatLng startPosition,finalPosition,currentPosition;
    double cpLat,cpLng;
    String stDSNo,stDriverId,stRdate,stRTime,stTravelType,stGuestName,stGuestMobile;
    String tot_time;
    Handler h1,h2;
    Runnable r1,r2;
    LatLng lastLocDist;
    Handler h,hDist,g;
    Runnable r,rDist,gR;
    float[] dist = new float[3];
    long res = 0, resDist = 0;
    float inAccurate = 10;
    String pickupLat,pickupLong,dropLat,dropLong;
    float finalDistance,distance=0;
    SharedPreferences pref;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    boolean first=true;
    SharedPreferences.Editor editor;
    String stStartDate,pause,stWaypoints,date3,jsondata;
    Button btStoreData;
    ProgressDialog progressDialog;
    int position;
    LinearLayout llOTPValidate;
    GPSTracker gps;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //mapFragment.getMapAsync(this);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        gps=new GPSTracker(getActivity());

        mRequestingLocationUpdates=false;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.fragment_track_ride, container,false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ibStop=(ImageButton)rootView.findViewById(R.id.ftr_ib_stop);
        ibCall=(ImageButton)rootView.findViewById(R.id.ftr_ib_call);
        btPickUp=(Button)rootView.findViewById(R.id.ftr_bt_otp);
        btValidate=(Button)rootView.findViewById(R.id.ftr_bt_validate);
        btDrop=(Button)rootView.findViewById(R.id.ftr_bt_drop);
        btFinish=(Button)rootView.findViewById(R.id.ftr_bt_finish);
        tvPlace=(TextView)rootView.findViewById(R.id.ftr_tv_place);
        // tvDsno=(TextView)rootView.findViewById(R.id.ftr_tv_dsno);
        tvBtype=(TextView)rootView.findViewById(R.id.ftr_tv_btype);
        tvRdate=(TextView)rootView.findViewById(R.id.ftr_tv_rdate);
        tvRtime=(TextView)rootView.findViewById(R.id.ftr_tv_rtime);
        tvGname=(TextView)rootView.findViewById(R.id.ftr_tv_gname);
        tvGmobile=(TextView)rootView.findViewById(R.id.ftr_tv_gmobile);
        tvNetwork=(TextView)rootView.findViewById(R.id.ftr_tv_ntwrk);
        btStoreData=(Button)rootView.findViewById(R.id.ftr_bt_store_offline);
        llOTPValidate=(LinearLayout)rootView.findViewById(R.id.ftr_ll_otp_validate);

        btValidate.setVisibility(View.GONE);
        btDrop.setVisibility(View.GONE);
        btFinish.setVisibility(View.GONE);
        tvNetwork.setVisibility(View.GONE);
        btStartState=true;
        btStopState=false;

        REST_CLIENT= RestClient.get();

        Bundle d=getActivity().getIntent().getExtras();
        position = d.getInt("position");


        myList = (ArrayList<DutyData>) getActivity().getIntent().getSerializableExtra("list");
        data=myList.get(position);

        //tvDsno.setText(data.getUddsno());
        stDSNo=data.getDslipid();

        // System.out.println("**************** dsno is "+stDSNo);
        tvBtype.setText(data.getUddsno());
        stTravelType=data.getBookingtype();
        stDriverId=data.getDriverid();
        //tvRdate.setText(data.getStartdate());
        tvRtime.setText(String.valueOf(data.getStarttime()));
        stRTime=String.valueOf(data.getStarttime());
        tvGname.setText(data.getGuestname());
        stGuestName=data.getGuestname();
        tvGmobile.setText(data.getGuestmobile());
        stGuestMobile=data.getGuestmobile();
        gNumber=data.getGuestmobile();
        stStartDate=data.getStartdate();

        String s=data.getStartdate();
        try {
            SimpleDateFormat newformat = new SimpleDateFormat("dd-MM-yyyy");
            String datestring = s.split("T")[0];
            SimpleDateFormat oldformat = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = newformat.format(oldformat.parse(datestring));

            DecimalFormat formatter=new DecimalFormat("00.00");
            String aFormatter=formatter.format(data.getStarttime());

            tvRdate.setText(reformattedStr+" "+aFormatter);
            stRdate=reformattedStr;

        }catch(ParseException e){e.printStackTrace();}

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        tvPlace.setText("Searching Location ...");
        startingTime=getCurrentTime();

        final Date date = new Date();
        startTimeUpdated=dateFormat.format(date);

        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        dbAdapter.insertTimes("startTimeUpdated",startTimeUpdated,stDSNo);
        dbAdapter.insertTimes("startingTime",startingTime,stDSNo);
        dbAdapter.insertStatus(stDSNo,"started");


        if(Build.VERSION.SDK_INT<23)
        {
            //System.out.println("Sdk_int is"+Build.VERSION.SDK_INT);
            //System.out.println("the enetred values is "+entered);
            establishConnection();
        }
        else
        {
            if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                establishConnection();
            }
            else
            {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(getActivity(),"Location Permission is required for this app to run!",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }

        ibStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btStartState=false;
                btStopState=true;
                pauseTime=getCurrentTime();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_continue_duty, null);
                dialogBuilder.setView(dialogView);

                Button ok=(Button)dialogView.findViewById(R.id.acd_bt_ok);
                Button cancel=(Button)dialogView.findViewById(R.id.acd_bt_cancel);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //dialogView.dismiss();
                        /*

                        btStartState = true;
                        btStopState = false;

                        if (pauseTime != null) {
                            moveTime = java.text.DateFormat.getTimeInstance().format(new Date());
                            //SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                            try {
                                Date date1 = timeFormat.parse(pauseTime);
                                Date date2 = timeFormat.parse(moveTime);

                                diff = diff + (date2.getTime() - date1.getTime());
                                diff_times=diff_times+" {[stopped: "+pauseTime+"][resumed: "+moveTime+"]} ";
                                dbAdapter.updateDutyIdleTime(stDSNo,diff,diff_times);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                            startLocationUpdates();
                        }

                        Toast.makeText(getActivity(),"Duty Started !",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                        */



                        //currentTime=getCurrentTime();
                        if (current_lat != 0 && current_long != 0) {
                            dbAdapter.insertEntry(stDSNo, current_lat, current_long, complete_address, resDist, timeUpdated);
                        }

                        dbAdapter.insertTimes("pauseTime",pauseTime,stDSNo);

                        final JsonObject v=new JsonObject();
                        v.addProperty("dslipid",data.getDslipid());
                        v.addProperty("driverid",data.getDriverid());
                        v.addProperty("startdate",data.getStartdate());
                        v.addProperty("totkms"," ");
                        v.addProperty("jdetails"," ");
                        v.addProperty("sjdetails"," ");
                        v.addProperty("cjdetails"," ");
                        v.addProperty("tothrs"," ");
                        v.addProperty("signature"," ");
                        v.addProperty("idletime"," ");
                        v.addProperty("status","P");

                        Call<UpdatePojo> call=REST_CLIENT.sendJourneyDetails(v);
                        call.enqueue(new Callback<UpdatePojo>() {
                            @Override
                            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {

                                alertDialog.dismiss();

                                if(response.isSuccessful())
                                {
                                    Intent i=new Intent(getActivity(),HomeActivity.class);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                                alertDialog.dismiss();

                                Toast.makeText(getActivity(),"Internet Connection Error! Please try again.",Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                    }
                });
            }
        });

        btPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbAdapter.updateStatus(stDSNo,"otp");

                //btValidate code

                JsonObject j=new JsonObject();
                j.addProperty("dslipid",data.getDslipid());
                j.addProperty("driverid",data.getDriverid());
                j.addProperty("startdate",data.getStartdate());

                Call<OTPPojo> call=REST_CLIENT.putOTPRequest(j);
                call.enqueue(new Callback<OTPPojo>() {
                    @Override
                    public void onResponse(Call<OTPPojo> call, Response<OTPPojo> response) {

                        OTPPojo otpData;

                        if(response.isSuccessful())
                        {
                            if (btValidate.isShown()) {
                            }
                            else {
                                btValidate.setVisibility(View.VISIBLE);
                                // btValidate.startAnimation(animAlpha);
                            }

                            otpData=response.body();
                            if (otpData.getMessage().equals("3")) {

                                btPickUp.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),"OTP is being sent!Limit Exceeded",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"OTP is being sent! Please wait.",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {

                            Toast.makeText(getActivity(), "Limit Exceeded..", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OTPPojo> call, Throwable t) {

                        Toast.makeText(getActivity(),"Internet connection error! Please try again.",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

        btValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_validate_otp, null);
                dialogBuilder.setView(dialogView);

                final EditText adEtOtp=(EditText)dialogView.findViewById(R.id.ca_et_otp);
                Button adBtOtp=(Button)dialogView.findViewById(R.id.ca_bt_ok);
                Button adBtCancel=(Button)dialogView.findViewById(R.id.ca_bt_cancel);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                adBtOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        final String adStOtp = adEtOtp.getText().toString().trim();

                        JsonObject v=new JsonObject();
                        v.addProperty("dslipid",data.getDslipid());
                        v.addProperty("driverid",data.getDriverid());
                        v.addProperty("startdate",data.getStartdate());
                        v.addProperty("otp",adStOtp);

                        Call<UpdatePojo> call=REST_CLIENT.validateOTPRequest(v);
                        call.enqueue(new Callback<UpdatePojo>() {
                            @Override
                            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {

                                progressDialog.dismiss();

                                UpdatePojo u;

                                if(response.isSuccessful())
                                {
                                    u=response.body();
                                    dbAdapter.updateStatus(stDSNo,"validated");
                                    btPickUp.setVisibility(View.GONE);
                                    btValidate.setVisibility(View.GONE);
                                    btDrop.setVisibility(View.VISIBLE);
                                    //btDrop.startAnimation(animAlpha);

                                    Date date = new Date();
                                    pickTimeUpdated = dateFormat.format(date);
                                    pickTime=getCurrentTime();
                                    dbAdapter.insertTimes("pickTimeUpdated",pickTimeUpdated,stDSNo);
                                    //isPicked=true;
                                    stBeforePickUp = dbAdapter.getIntervalData(startTimeUpdated, pickTimeUpdated,stDSNo);
                                    dbAdapter.insertTimes("stBeforePickup",stBeforePickUp,stDSNo);
                                }
                                else{
                                    Toast.makeText(getActivity(),"OTP Authentication Failed!",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                                progressDialog.dismiss();

                                Toast.makeText(getActivity(),"Error in Network Connection",Toast.LENGTH_LONG).show();

                            }
                        });
                        alertDialog.dismiss();
                    }
                });

                adBtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                    }
                });
            }
        });

        btDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater =getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_drop_guest, null);
                dialogBuilder.setView(dialogView);

                Button adBtOk=(Button)dialogView.findViewById(R.id  .cad_bt_ok);
                Button adBtCancel=(Button)dialogView.findViewById(R.id.cad_bt_cancel);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                adBtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dbAdapter.updateStatus(stDSNo,"dropped");
                        alertDialog.dismiss();
                        Intent s=new Intent(getActivity(),SignatureActivity.class);
                        startActivityForResult(s,2);
                    }
                });

                adBtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                    }
                });
            }
        });

        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int Hours = (int) (diff/(1000 * 60 * 60));
                int Mins = (int) (diff/(1000*60)) % 60;
                long Secs = (int) (diff / 1000) % 60;

                DecimalFormat formatter = new DecimalFormat("00");
                String hFormatted = formatter.format(Hours);
                String mFormatted = formatter.format(Mins);
                String sFormatted = formatter.format(Secs);
                date3=hFormatted+":"+mFormatted+":"+sFormatted;

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_finish_duty, null);
                dialogBuilder.setView(dialogView);

                Button adBtOk = (Button) dialogView.findViewById(R.id.caf_bt_ok);
                Button adBtCancel = (Button) dialogView.findViewById(R.id.caf_bt_cancel);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                adBtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        //currentTime=getCurrentTime();
                        if (current_lat != 0 && current_long != 0) {
                            dbAdapter.insertEntry(stDSNo, current_lat, current_long, complete_address, resDist, timeUpdated);


                            dbAdapter.updateStatus(stDSNo, "finished");

                            String data;
                            data = dbAdapter.getTodaysValues();


                            endingTime = getCurrentTime();

                            Date date = new Date();
                            finishTimeUpdated = dateFormat.format(date);
                            dbAdapter.insertTimes("finishTimeUpdated", finishTimeUpdated, stDSNo);

                            stAfterDrop = dbAdapter.getIntervalData(dropTimeUpdated, finishTimeUpdated, stDSNo);
                            dbAdapter.insertTimes("stAfterDrop", stAfterDrop, stDSNo);
                            /*
                            System.out.println("Res"+res);
                            System.out.println("stime & endtime"+startingTime+" "+endingTime);
                            System.out.println("idle_time"+date3);
                            System.out.println("jdet"+data);
                            System.out.println("stBef"+stBeforePickUp+" "+stAfterPickUp+" "+stAfterDrop);
                            System.out.println("sign"+signature);
                            */


                            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

                            jsondata = dbAdapter.getTodaysValues();

                            // System.out.println("jsondata is "+jsondata);

                            //pickupLat=pref.getString("pickup_lat",null);
                            //pickupLong=pref.getString("pickup_long",null);
                            pickupLat = dbAdapter.getTime(stDSNo, "pickup_lat");
                            pickupLong = dbAdapter.getTime(stDSNo, "pickup_long");
                            dropLat = String.valueOf(current_lat);
                            dropLong = String.valueOf(current_long);

                            if (!(pickupLat.equals("")) || !(pickupLong.equals(""))) {

                                if (!(dropLat.equals("0.0")) || !(dropLong.equals("0.0"))) {

                                    stWaypoints = dbAdapter.getWaypoints(stDSNo);
                                    // System.out.println("waypoints " + stWaypoints);
//
//                                    String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
//                                            "origin=" + pickupLat + "," + pickupLong + "&destination=" + dropLat + "," + dropLong + "&waypoints=" + stWaypoints + "&key=AIzaSyBGK303F1k8pU7ncriAcctGuju_tw-gTIs";

                                    // System.out.println(urlString);


                                    String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                                            "origin=" + pickupLat + "," + pickupLong + "&destination=" + dropLat + "," + dropLong + "&waypoints=" + stWaypoints + "&key=AIzaSyCIx6j-T1Yd5UuQUgnuRY04ZdoDF4xCW0E";


                                    Call<DistancePojo> call = REST_CLIENT.getDistanceDetails(urlString);
                                    call.enqueue(new Callback<DistancePojo>() {
                                        @Override
                                        public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                                            DistancePojo data;
                                            Route rData;
                                            Leg lData;

                                            if (response.isSuccessful()) {
                                                data = response.body();

                                                // System.out.println(response.message() + "::" + response.code() + "::" + response.errorBody());

                                                // System.out.println("status is " + data.getStatus());
                                                List<Route> rDataList = data.getRoutes();
                                                // System.out.println("Route size "+rDataList.size());

                                                if (rDataList != null) {

                                                    for (int i = 0; i < rDataList.size(); i++) {
                                                        rData = rDataList.get(i);

                                                        List<Leg> lDataList = rData.getLegs();

                                                        for (int j = 0; j < lDataList.size(); j++) {
                                                            lData = lDataList.get(j);

                                                            Distance d = lData.getDistance();

                                                            distance = distance + d.getValue();

                                                            // System.out.println("dist and value is " + d.getValue() + ":::" + distance);
                                                        }

                                                    }

                                                    distance = distance / 1000;
                                                    finalDistance = distance;

                                                    //////////////////////

                                                    progressDialog.dismiss();
                                                    alertDialog.dismiss();

                                                    Bundle b = new Bundle();
                                                    b.putFloat("Result", finalDistance);
                                                    b.putString("starting_time", startingTime);
                                                    b.putString("ending_time", endingTime);
                                                    b.putString("idle_time", date3);
                                                    b.putInt("position", position);
                                                    b.putString("jDetails", jsondata);
                                                    b.putString("stBeforePickUp", stBeforePickUp);
                                                    b.putString("stAfterPickUp", stAfterPickUp);
                                                    b.putString("stAfterDrop", stAfterDrop);
                                                    b.putString("sign", signature);
                                                    String[] timeArray = date3.split(":");
                                                    String hh = timeArray[0];
                                                    String mm = timeArray[1];
                                                    final String p = hh + "." + mm;
                                                    pause = p + "" + diff_times;
                                                    // System.out.println("pause" + pause);
                                                    b.putString("pause", pause);
                                                    res = 0;
                                                    // Intent j=new Intent(getActivity(),ResultActivity.class);
                                                    Intent j = new Intent(getActivity(), ResultActivity.class);
                                                    j.putExtras(b);
                                                    j.putExtra("mylist", myList);
                                                    j.putExtra("position", position);

                                                    //j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(j);
                                                    getActivity().finish();

                                                    ///////////////////
                                                } else {

                                                    //  Toast.makeText(MapsActivity.this,data.getStatus(),Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                // System.out.println(response.message() + "::" + response.code() + "::" + response.isSuccessful());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DistancePojo> call, Throwable t) {

                                            progressDialog.dismiss();

                                            // Toast.makeText(MapsActivity.this,"Connectivity Error..Please Try Again!",Toast.LENGTH_LONG).show();
                                            btStoreData.setVisibility(View.VISIBLE);
                                            //  btFinish.setVisibility(View.GONE);
                                            // dbAdapter.insertDutyEntry(stDSNo,pickupLat,pickupLong,dropLat,dropLong,stDriverId,stStartDate,stWaypoints,jsondata,stBeforePickUp,stAfterPickUp,stAfterDrop,tot_time,pause,date3,stGuestName,stGuestMobile,startingTime,endingTime);
                                            Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                } else {

                                    Toast.makeText(getActivity(), "Fetching data...Please try again!", Toast.LENGTH_LONG).show();
                                }
                            } else {

                                Toast.makeText(getActivity(), "Fetching data...Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {

                            Toast.makeText(getActivity(),"Getting Location.. Please wait!",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            alertDialog.dismiss();
                        }


                    }
                });

                adBtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                    }
                });
            }
        });

        btStoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                try {
                    Date date1 = timeFormat.parse(startingTime);
                    Date date2 = timeFormat.parse(endingTime);
                    //Date date3=timeFormat.parse(idle_time);

                    diff = (date2.getTime() - date1.getTime());
                    int Hours = (int) (diff/(1000 * 60 * 60));
                    int Mins = (int) (diff/(1000*60)) % 60;
                    long Secs = (int) (diff / 1000) % 60;

                    DecimalFormat formatter = new DecimalFormat("00");
                    String hFormatted = formatter.format(Hours);
                    String mFormatted = formatter.format(Mins);
                    String sFormatted = formatter.format(Secs);
                    tot_time=hFormatted+":"+mFormatted+":"+sFormatted;

                    // System.out.println("Total time travelled is "+finalTimeTravelled);

                }catch (ParseException e) {e.printStackTrace();}

                String[] timeArray = date3.split(":");
                String hh = timeArray[0];
                String mm = timeArray[1];
                final String p = hh + "." + mm;
                pause = p + "" + diff_times;
                // System.out.println("pause" + pause);


                long result=dbAdapter.insertDutyEntry(stDSNo,pickupLat,pickupLong,dropLat,dropLong,stDriverId,stStartDate,stWaypoints,jsondata,stBeforePickUp,stAfterPickUp,stAfterDrop,tot_time,pause,date3,stGuestName,stGuestMobile,startingTime,endingTime);

                if(result!=-1)
                {
                    getActivity().finish();
                    Toast.makeText(getActivity(),"Data stored!",Toast.LENGTH_LONG).show();
                }
                else {

                    btFinish.setVisibility(View.GONE);

                    Toast.makeText(getActivity(),"Please try saving the data again!",Toast.LENGTH_LONG).show();
                }

            }
        });

        ibCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialNumber();
            }
        });


        return rootView;
    }

    protected void dialNumber(){

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+gNumber));//GUEST NUMBER HERE...
        startActivity(intent);
    }

    public void establishConnection(){

        entered=true;

        getGPSLocationUpdates();

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        }
        else {

            Toast.makeText(getActivity(),"GPS is not enabled... Please check!",Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

    }

//    public void firstHandler()
//    {
//        h1=new Handler();
//        r1=new Runnable() {
//            @Override
//            public void run() {
//
//                h1.postDelayed(r1,30000);
//
//                Toast.makeText(getActivity(),"handler1",Toast.LENGTH_LONG).show();
//
//            }
//        };
//        h1.post(r1);
//    }
//
//    public  void secondHandler()
//    {
//        h2=new Handler();
//        r2=new Runnable() {
//            @Override
//            public void run() {
//
//                h2.postDelayed(r2,600000);
//
//                Toast.makeText(getActivity(),"handler2",Toast.LENGTH_LONG).show();
//
//            }
//        };
//
//        h2.post(r2);
//    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {
        // System.out.println("Google API client in stop is "+mGoogleApiClient);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

   /* @Override
    public void onLocationChanged(Location location) {

        //to avoid error for the first time when mLastLoc is null
        if (mLastLocation == null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            lastloc = new LatLng(latitude, longitude);
            //lastLocDist=lastloc;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() != 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String add1 = addresses.get(0).getAddressLine(1);
                    String add2 = addresses.get(0).getAddressLine(2);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    complete_address = address + " " + add1 + " " + add2;
                    tvPlace.setText(complete_address);
                } else {
                    tvPlace.setText("-");
                    complete_address = "-";
                }
            } catch (IOException e) {
                e.printStackTrace();
                complete_address = "Unable to get the location details";
                tvPlace.setText(complete_address);
            }
            // marker = mMap.addMarker(new MarkerOptions().position(lastloc).title("Starting Location"));
            //  marker.setPosition(lastloc);

            startPosition = marker.getPosition();
            finalPosition = new LatLng(latitude, longitude);

            double toRotation = bearingBetweenLocations(startPosition, finalPosition);
            rotateMarker(marker, (float) toRotation);
            accelerateDecelerate();

//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastloc, 16));
//            mMap.getUiSettings().setMapToolbarEnabled(false);
        }

        if (mLastLocation != null) {

            // System.out.println("location.getAccuracy ..." + location.getAccuracy());
            // System.out.println("coordinates are "+location.getLatitude()+"::"+location.getLongitude());

            if (location != null && location.hasAccuracy()) {

                if (location.getAccuracy() <= inAccurate) {

                    if (location.getLatitude() != 0 && location.getLongitude() != 0) {

                        current_lat = location.getLatitude();
                        current_long = location.getLongitude();

                        // System.out.println("Current location lat long are " + current_lat + ":::" + current_long);


                        curntloc = new LatLng(current_lat, current_long);


                        if(first)
                        {
                            // editor.putString("pickup_lat",String.valueOf(current_lat));
                            //editor.putString("pickup_long",String.valueOf(current_long));
                            // editor.commit();
                            dbAdapter.insertTimes("pickup_lat",String.valueOf(current_lat),stDSNo);
                            dbAdapter.insertTimes("pickup_long",String.valueOf(current_long),stDSNo);
                            //currentTime=getCurrentTime();
                            Date date = new Date();
                            timeUpdated = dateFormat.format(date);
                            dbAdapter.insertEntry(stDSNo,current_lat, current_long, complete_address, resDist, timeUpdated);
                            first=false;
                        }


                        //////////////////////////////////////////

                    *//*

                    try {
                        addresses = geocoder.getFromLocation(current_lat, current_long, 1);
                        if (addresses.size() != 0) {
                            int l = addresses.get(0).getMaxAddressLineIndex();
                            String add = "", add1 = "", add2 = "";

                            for (int k = 0; k < l; k++) {
                                add = add + addresses.get(0).getAddressLine(k);
                                add = add + " ";

                                if (k == 1) {
                                    add1 = addresses.get(0).getAddressLine(k);
                                }
                                if (k == 2) {
                                    add2 = addresses.get(0).getAddressLine(k);
                                }
                            }
                            String address = addresses.get(0).getAddressLine(0);
                            String add_1 = addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                            String add_2 = addresses.get(0).getAddressLine(2);
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            //complete_address=address+" "+add1+" "+add2;
                            tvPlace.setText(add1 + " " + add2);
                            complete_address = add;
                        } else {
                            tvPlace.setText("-");
                            complete_address = "-";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        complete_address = "No response from server";
                        tvPlace.setText(complete_address);
                    }
                    //  marker = mMap.addMarker(new MarkerOptions().position(curntloc)
                    // .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_new)));
                    //marker.setPosition(curntloc);

                    startPosition = marker.getPosition();
                    finalPosition = new LatLng(current_lat, current_long);

                    double toRotation = bearingBetweenLocations(startPosition, finalPosition);
                    rotateMarker(marker, (float) toRotation);
                    accelerateDecelerate();

                    *//*

                        //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, 16));
                        //    mMap.getUiSettings().setMapToolbarEnabled(false);


                        /////////////////////////////////////////////
                    }
                }
            }
        }
        mLastLocation = location;
        Date date = new Date();
        timeUpdated = dateFormat.format(date);
    }*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        //Toast.makeText(MapsOngoingActivity.this, "Location enabled by user!!!", Toast.LENGTH_LONG).show();
                       /* if(sbMsg!=null) {
                            sbMsg.dismiss();
                        }*/
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // Toast.makeText(MapsOngoingActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
            case 2:
            {
                //check for data==null i.e., when guest doesnt sign in th app
                //System.out.println("data here is "+data);
                if(data!=null) {
                    signature = data.getStringExtra("Sign");
                    dbAdapter.insertTimes("sign",signature,stDSNo);
                    // System.out.println("Sign String here is "+signature);
                    btDrop.setVisibility(View.GONE);
                    btFinish.setVisibility(View.VISIBLE);
                    //  btFinish.startAnimation(animAlpha);

                    Date date = new Date();
                    dropTimeUpdated = dateFormat.format(date);
                    dbAdapter.insertTimes("dropTimeUpdated",dropTimeUpdated,stDSNo);

                    if(pickTimeUpdated=="")
                    {
                        pickTimeUpdated=dbAdapter.getTime(stDSNo,"pickTimeUpdated");
                        // System.out.println("pick Time Updated isssssssssssssss"+pickTimeUpdated);

                    }

                    stAfterPickUp = dbAdapter.getIntervalData(pickTimeUpdated, dropTimeUpdated,stDSNo);
                    dbAdapter.insertTimes("stAfterPickup",stAfterPickUp,stDSNo);
                    // System.out.println("Json data after pickup and before dropping is ");
                    // System.out.println(stAfterPickUp);
                }
                else
                {
                    Toast.makeText(getActivity(),"Guest Signature not captured!",Toast.LENGTH_LONG).show();
                    //System.out.println("Sign String here is "+signature);
                    btDrop.setVisibility(View.GONE);
                    btFinish.setVisibility(View.VISIBLE);
                    btFinish.startAnimation(animAlpha);

                    Date date = new Date();
                    dropTimeUpdated = dateFormat.format(date);
                    dbAdapter.insertTimes("dropTimeUpdated",dropTimeUpdated,stDSNo);

                    if(pickTimeUpdated=="")
                    {
                        pickTimeUpdated=dbAdapter.getTime(stDSNo,"pickTimeUpdated");
                        // System.out.println("pick Time Updated is"+pickTimeUpdated);

                    }

                    stAfterPickUp = dbAdapter.getIntervalData(pickTimeUpdated, dropTimeUpdated,stDSNo);
                    dbAdapter.insertTimes("stAfterPickup",stAfterPickUp,stDSNo);
                    //System.out.println("Json data after pickup and before dropping is " + stAfterPickUp);
                }

                break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                establishConnection();

                gps=new GPSTracker(getActivity());

            } else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void gettingLocationUpdates() {
        h = new Handler();
        r = new Runnable() {
            @Override
            public void run() {

                h.postDelayed(r,15000);
                //currentTime = java.text.DateFormat.getTimeInstance().format(new Date());

                Date date = new Date();
                timeUpdated = dateFormat.format(date);

                try {
                    addresses = geocoder.getFromLocation(current_lat, current_long, 1);
                    if (addresses.size() != 0) {
                        int l = addresses.get(0).getMaxAddressLineIndex();
                        String add = "", add1 = "", add2 = "";

                        for (int k = 0; k < l; k++) {
                            add = add + addresses.get(0).getAddressLine(k);
                            add = add + " ";

                            if (k == 1) {
                                add1 = addresses.get(0).getAddressLine(k);
                            }
                            if (k == 2) {
                                add2 = addresses.get(0).getAddressLine(k);
                            }
                        }
                        String address = addresses.get(0).getAddressLine(0);
                        String add_1 = addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                        String add_2 = addresses.get(0).getAddressLine(2);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        //complete_address=address+" "+add1+" "+add2;
                        tvPlace.setText(address+" "+add1 + " " + add2);
                        complete_address = add;
                    } else {
                        tvPlace.setText("-");
                        complete_address = "-";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    complete_address = "Unable to get the location details";
                    tvPlace.setText(complete_address);
                }

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 )
                {

                    curntloc = new LatLng(current_lat, current_long);

                    if(marker!=null){

                        // System.out.println("in if map ready.........");
                    }
                    else {

                        //  System.out.println("in else map ready...");
                        marker = mMap.addMarker(new MarkerOptions().position(curntloc)
                                .title("Current Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
                    }



                    // System.out.println("moving car");

                    startPosition = marker.getPosition();
                    finalPosition = new LatLng(current_lat, current_long);

                    double toRotation = bearingBetweenLocations(startPosition, finalPosition);

                    rotateMarker(marker, (float) toRotation);
                    accelerateDecelerate();

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, 16));
                    mMap.getUiSettings().setMapToolbarEnabled(false);


//                    CameraPosition oldPos = mMap.getCameraPosition();
//
//                    CameraPosition pos = CameraPosition.builder(oldPos).
//                            // zoom(16).
//                                    bearing((float)toRotation).
//                                    build();
//                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

                }

                lastLocDist = new LatLng(current_lat,current_long);

            }
        };

        h.post(r);
    }

    public void storeCoordinatesForDistance() {
        hDist = new Handler();
        rDist = new Runnable() {
            @Override
            public void run() {

                hDist.postDelayed(rDist, 300000);//5 min ~ 300 sec

                currentTime = getCurrentTime();

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude != 0.0 && lastLocDist.longitude != 0.0) {

                    // System.out.println(lastLocDist.latitude + ":" + lastLocDist.longitude);
                    // System.out.println(current_lat + ":" + current_long);

                    Location.distanceBetween(lastLocDist.latitude, lastLocDist.longitude, current_lat, current_long, dist);

                    // System.out.println(lastLocDist.latitude + ":" + lastLocDist.longitude + ":" + current_lat + ":" + current_long + "::" + dist[0]);


                    dbAdapter.insertEntry(stDSNo, current_lat, current_long, complete_address, resDist, timeUpdated);
                    //lastLocDist=new LatLng(current_lat,current_long);
                    // System.out.println(current_lat + ":" + current_long + ":" + complete_address + ":" + resDist + ":" + timeUpdated);

                    // startPosition = marker.getPosition();
                    //  finalPosition = new LatLng(current_lat, current_long);

                    //  double toRotation = bearingBetweenLocations(startPosition, finalPosition);
                    //  rotateMarker(marker, (float) toRotation);

                    //  accelerateDecelerate();
                } else {

                    //dbAdapter.insertEntry(0.0, 0.0, complete_address, resDist, timeUpdated);

                }

                // lastLocDist = new LatLng(current_lat, current_long);

                if (dbAdapter.findDSNo(stDSNo)) {

                    dbAdapter.updateDutyUpdates(stDSNo, currentTime, resDist, current_lat, current_long);

                } else {

                    dbAdapter.insertDutyUpdates(stDSNo, stTravelType, stDriverId, stRdate, stRTime, resDist, startingTime, currentTime, stGuestName, stGuestMobile, current_lat, current_long, diff, diff_times);
                }

            }
        };
        hDist.post(rDist);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        latitude1=gps.getLastKnownLatitude();
        longitude1=gps.getLastKnownLongitude();

        if(latitude1!=0.0 && longitude1!=0.0) {

            lastloc = new LatLng(latitude1, longitude1);
            lastLocDist=lastloc;

            if (marker != null) {

                marker.setPosition(lastloc);
            } else {
                marker = mMap.addMarker(new MarkerOptions().position(lastloc).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
            }


            try {
                addresses = geocoder.getFromLocation(latitude1, longitude1, 1);
                if (addresses.size() != 0) {
                    int l = addresses.get(0).getMaxAddressLineIndex();
                    String add = "", add1 = "", add2 = "";

                    for (int k = 0; k < l; k++) {
                        add = add + addresses.get(0).getAddressLine(k);
                        add = add + " ";

                        if (k == 1) {
                            add1 = addresses.get(0).getAddressLine(k);
                        }
                        if (k == 2) {
                            add2 = addresses.get(0).getAddressLine(k);
                        }
                    }
                    String address = addresses.get(0).getAddressLine(0);
                    String add_1 = addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                    String add_2 = addresses.get(0).getAddressLine(2);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    //complete_address=address+" "+add1+" "+add2;
                    tvPlace.setText(add1 + " " + add2);
                    complete_address = add;
                } else {
                    tvPlace.setText("-");
                    complete_address = "-";
                }
            } catch (IOException e) {
                e.printStackTrace();
                complete_address = "Unable to get the location details";
                tvPlace.setText(complete_address);
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastloc, 16));
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }
    }

    private void accelerateDecelerate()
    {
        final Handler handler = new Handler();

        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 5000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;

                cpLat=startPosition.latitude * (1 - t) + finalPosition.latitude * t;
                cpLng= startPosition.longitude * (1 - t) + finalPosition.longitude * t;

                currentPosition = new LatLng(cpLat,cpLng);

                marker.setPosition(currentPosition);
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,16));

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    public static String getCurrentTime() {
        //date output format
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if(h!=null)
        {
            h.removeCallbacks(r);
        }

        if(hDist!=null)
        {
            hDist.removeCallbacks(rDist);
        }

        if(g!=null)
        {
            g.removeCallbacks(gR);
        }

        gps.stopUsingGPS();


    }

    public void getGPSLocationUpdates()
    {

        g=new Handler();
        gR=new Runnable() {
            @Override
            public void run() {

                g.postDelayed(this,2000);

                System.out.println("gps calling....");

                current_lat = gps.getLatitude();
                current_long = gps.getLongitude();

                if (current_lat != 0.0 && current_long != 0.0) {

                    if (first) {

                        pickupLat = String.valueOf(current_lat);
                        pickupLong = String.valueOf(current_long);

                        dbAdapter.insertTimes("pickup_lat",String.valueOf(pickupLat),stDSNo);
                        dbAdapter.insertTimes("pickup_long",String.valueOf(pickupLong),stDSNo);

                        gettingLocationUpdates();
                        storeCoordinatesForDistance();

                        first = false;
                    }
                }
                else {

                    Toast.makeText(getActivity(),"Getting Location.. Please wait!",Toast.LENGTH_SHORT).show();
                }
            }
        };

        g.post(gR);
    }
}
