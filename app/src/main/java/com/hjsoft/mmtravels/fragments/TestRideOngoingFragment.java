package com.hjsoft.mmtravels.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.GPSTracker;
import com.hjsoft.mmtravels.KalmanLatLong;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.activity.HomeActivity;
import com.hjsoft.mmtravels.activity.ResultActivity;
import com.hjsoft.mmtravels.activity.SignatureActivity;
import com.hjsoft.mmtravels.activity.TrackRideOngoing;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.model.Distance;
import com.hjsoft.mmtravels.model.DistancePojo;
import com.hjsoft.mmtravels.model.Duration;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.DutyUpdates;
import com.hjsoft.mmtravels.model.Leg;
import com.hjsoft.mmtravels.model.OTPPojo;
import com.hjsoft.mmtravels.model.Pojo;
import com.hjsoft.mmtravels.model.Route;
import com.hjsoft.mmtravels.model.SnapDistance;
import com.hjsoft.mmtravels.model.SnappedPoint;
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
 * Created by hjsoft on 2/3/17.
 */
public class TestRideOngoingFragment extends Fragment implements OnMapReadyCallback {

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

    ConnectivityManager connMgr;
    Button btStart,btFinish,btPickUp,btDrop,btValidate;
    ImageButton ibStop;
    Geocoder geocoder;
    List<Address> addresses;
    String complete_address;
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

    DutyUpdates updates;
    ArrayList<DutyUpdates> updateList;
    String status;
    LatLng lastLocDist;
    Handler h,hDist,g;
    Runnable r,rDist,gR;
    float[] dist = new float[3];
    long resDist = 0;
    float inAccurate = 10;
    String pickupLat,pickupLong,dropLat,dropLong;
    float finalDistance=0,distance=0,approxFinalDist=0;
    SharedPreferences pref;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    TrackRideOngoing activity;
    Button btStoreData;
    String stWaypoints,stStartDate,pause,date3,jsondata;
    boolean first=true;
    ProgressDialog progressDialog;
    int position;
    GPSTracker gps;
    float startToReportKms=0,finishToGarageKms=0,startToReportHrs=0,finishToGarageHrs=0;
    String garageStatus,latitude,longitude;
    public static final int ACCURACY_DECAYS_TIME = 10;
    private KalmanLatLong kalmanLatLong;
    float accuracy;
    long getTime;
    double tempLat,tempLong;
    private Polyline runningPathPolyline;
    String crntPickupLat,crntPickupLng;
    boolean flagForFinishClicked=false;
    AlertDialog alertDialog;
    SharedPreferences.Editor editor;
    String snapToRoadWaypoints="";
    float[] results = new float[2];
    float res=0;
    ArrayList<com.hjsoft.mmtravels.model.Location> a1=new ArrayList<>();
    ArrayList<com.hjsoft.mmtravels.model.Location> a2=new ArrayList<>();
    ArrayList<com.hjsoft.mmtravels.model.Location> a3=new ArrayList<>();
    int iteration=1;
    int countForLocUpdate=0;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor=pref.edit();

        gps=new GPSTracker(getActivity());


        mRequestingLocationUpdates=false;

        kalmanLatLong = new KalmanLatLong(ACCURACY_DECAYS_TIME);

        res=pref.getFloat("res",res);

        //System.out.println("result till now iss"+res);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.fragment_track_ride_ongoing, container,false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // System.out.println("Track Ride Ongoing fragment callingggggggggggggggggggg");

        ibStop=(ImageButton)rootView.findViewById(R.id.ftro_ib_stop);
        ibCall=(ImageButton)rootView.findViewById(R.id.ftro_ib_call);
        btPickUp=(Button)rootView.findViewById(R.id.ftro_bt_otp);
        btValidate=(Button)rootView.findViewById(R.id.ftro_bt_validate);
        btDrop=(Button)rootView.findViewById(R.id.ftro_bt_drop);
        btFinish=(Button)rootView.findViewById(R.id.ftro_bt_finish);
        tvPlace=(TextView)rootView.findViewById(R.id.ftro_tv_place);
        // tvDsno=(TextView)rootView.findViewById(R.id.ftro_tv_dsno);
        tvBtype=(TextView)rootView.findViewById(R.id.ftro_tv_btype);
        tvRdate=(TextView)rootView.findViewById(R.id.ftro_tv_rdate);
        tvRtime=(TextView)rootView.findViewById(R.id.ftro_tv_rtime);
        tvGname=(TextView)rootView.findViewById(R.id.ftro_tv_gname);
        tvGmobile=(TextView)rootView.findViewById(R.id.ftro_tv_gmobile);
        tvNetwork=(TextView)rootView.findViewById(R.id.ftro_tv_ntwrk);
        btStoreData=(Button)rootView.findViewById(R.id.ftro_bt_store_offline);

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
        stStartDate=data.getStartdate();
        //tvRdate.setText(data.getStartdate());
        tvRtime.setText(String.valueOf(data.getStarttime()));
        stRTime=String.valueOf(data.getStarttime());
        tvGname.setText(data.getGuestname());
        stGuestName=data.getGuestname();
        tvGmobile.setText(data.getGuestmobile());
        stGuestMobile=data.getGuestmobile();
        gNumber=data.getGuestmobile();
        garageStatus=data.getGarageStatus();
        latitude=data.getLatitude();
        longitude=data.getLongitude();

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
        tvPlace.setText("Searching Location...");

        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        pauseTime=dbAdapter.getTime(stDSNo,"pauseTime");

        moveTime=getCurrentTime();

        // System.out.println("pause time,move time is "+pauseTime+"::"+moveTime);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date1 = timeFormat.parse(pauseTime);
            Date date2 = timeFormat.parse(moveTime);

            diff = diff + (date2.getTime() - date1.getTime());
            diff_times=diff_times+" {[stopped: "+pauseTime+"][resumed: "+moveTime+"]} ";

            diff=diff+dbAdapter.getDutyIdleTime(stDSNo);
            diff_times=diff_times+dbAdapter.getDutyIdleTimeDiff(stDSNo);

            // System.out.println("idle time data is "+diff+"::"+diff_times);

            dbAdapter.updateDutyIdleTime(stDSNo,diff,diff_times);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        updateList=dbAdapter.getAllDutyUpdates();
        updates=updateList.get(0);

        status=dbAdapter.getStatus(stDSNo);

        switch (status)
        {
            case "started":btPickUp.setVisibility(View.VISIBLE);
                if(data.getGuestmobile().equals(""))
                {
                    btPickUp.setVisibility(View.GONE);
                    btValidate.setVisibility(View.GONE);
                    tvGmobile.setText("NA");
                    btDrop.setVisibility(View.VISIBLE);
                }
                break;
            case "otp":btValidate.setVisibility(View.VISIBLE);
                if(data.getGuestmobile().equals(""))
                {
                    btPickUp.setVisibility(View.GONE);
                    btValidate.setVisibility(View.GONE);
                    tvGmobile.setText("NA");
                    btDrop.setVisibility(View.VISIBLE);
                }
                break;
            case "validated":btPickUp.setVisibility(View.GONE);
                btDrop.setVisibility(View.VISIBLE);
                break;
            case "dropped":btPickUp.setVisibility(View.GONE);
                btFinish.setVisibility(View.VISIBLE);
                break;
            case "finished":btPickUp.setVisibility(View.GONE);
                btFinish.setVisibility(View.VISIBLE);
                break;
        }

        /*

        her check if the status of the row(ride) is paused: Then here getting lat,long is not necessary ....
        in case if the status is not paused.. then it is considered ongoing... and here get the lat,long from the db.

        latitude=updates.getLat();
        longitude=updates.getLng();
        diff=updates.getIdleTime();
        diff_times=updates.getIdleTimeDiff();
        res=updates.getTotKms();
        */


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

                        if (current_lat != 0 && current_long != 0) {
                            dbAdapter.insertEntry(stDSNo, current_lat, current_long, complete_address, resDist, timeUpdated);

                        }

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
                        v.addProperty("startToReportKms",startToReportKms);
                        v.addProperty("startToReportHrs",startToReportHrs);
                        v.addProperty("finishToGarageKms",finishToGarageKms);
                        v.addProperty("fiishToGarageHrs",finishToGarageHrs);

                        Call<UpdatePojo> call=REST_CLIENT.sendJourneyDetails(v);
                        call.enqueue(new Callback<UpdatePojo>() {
                            @Override
                            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {

                                if(response.isSuccessful())
                                {
                                    dbAdapter.updateTimes("pauseTime",pauseTime,stDSNo);
                                    Intent i=new Intent(activity,HomeActivity.class);
                                    startActivity(i);
                                    getActivity().finish();

                                }
                            }

                            @Override
                            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                                Toast.makeText(getActivity(),"Connectivity Error! Please try again.",Toast.LENGTH_LONG).show();

                            }
                        });

                        alertDialog.dismiss();

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

                //dbAdapter.updateStatus(stDSNo,"otp");

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

                            dbAdapter.updateStatus(stDSNo,"otp");

                            if (btValidate.isShown()) {
                            }
                            else {
                                btValidate.setVisibility(View.VISIBLE);
                                // btValidate.startAnimation(animAlpha);
                            }

                            otpData=response.body();
                            if (otpData.getMessage().equals("3")) {

                                btPickUp.setVisibility(View.GONE);

                                Toast.makeText(getActivity(),"OTP is being sent!\nCannot request for OTP anymore.",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"OTP is being sent! Please wait.",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {

                            Toast.makeText(getActivity(), "OTP Limit Exceeded..", Toast.LENGTH_LONG).show();
                            btPickUp.setClickable(false);
                            btPickUp.setEnabled(false);
                            btValidate.setVisibility(View.VISIBLE);

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

                        //System.out.println("OTP Validations::::::");
                        //System.out.println(data.getDslipid()+"::"+data.getDriverid()+"::"+data.getStartdate()+"::"+adStOtp);

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
                                    if(startTimeUpdated=="")
                                    {
                                        startTimeUpdated=dbAdapter.getTime(stDSNo,"startTimeUpdated");
                                        // System.out.println("start Time updated"+startTimeUpdated);
                                    }

                                    stBeforePickUp = dbAdapter.getIntervalData(startTimeUpdated, pickTimeUpdated,stDSNo);
                                    dbAdapter.insertTimes("stBeforePickup",stBeforePickUp,stDSNo);
                                }
                                else{
                                    Toast.makeText(getActivity(),"OTP Authentication Failed!",Toast.LENGTH_LONG).show();
                                    //System.out.println("Log"+response.message());

                                }
                            }

                            @Override
                            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                                progressDialog.dismiss();

                                Toast.makeText(getActivity(),"Internet connection error! Please try again.",Toast.LENGTH_LONG).show();

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
                        //Intent s=new Intent(getActivity(),HomeActivity.class);
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

                alertDialog = dialogBuilder.create();
                alertDialog.show();

                adBtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //System.out.println("DISTANCE FROM ALGO IS(meters)"+res);

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        if (current_lat != 0 && current_long != 0) {

                            dbAdapter.insertEntry(stDSNo, current_lat, current_long, complete_address, resDist, timeUpdated);


                            dbAdapter.updateStatus(stDSNo, "finished");


                            endingTime = getCurrentTime();

                            Date date = new Date();
                            finishTimeUpdated = dateFormat.format(date);
                            dbAdapter.insertTimes("finishTimeUpdated", finishTimeUpdated, stDSNo);

                            //stAfterDrop=dbAdapter.getIntervalData(dropTimeUpdated,finishTimeUpdated);
                            //dbAdapter.insertTimes("stAfterDrop",stAfterDrop,stDSNo);
                            /*
                            System.out.println("Res"+res);
                            System.out.println("stime & endtime"+startingTime+" "+endingTime);
                            System.out.println("idle_time"+date3);
                            System.out.println("jdet"+data);
                            System.out.println("stBef"+stBeforePickUp+" "+stAfterPickUp+" "+stAfterDrop);
                            System.out.println("sign"+signature);
                            */

                            if (dropTimeUpdated == "") {
                                dropTimeUpdated = dbAdapter.getTime(stDSNo, "dropTimeUpdated");
                                // System.out.println("drop time is"+dropTimeUpdated);
                            }

                            stAfterDrop = dbAdapter.getIntervalData(dropTimeUpdated, finishTimeUpdated, stDSNo);
                            dbAdapter.insertTimes("stAfterDrop", stAfterDrop, stDSNo);
                            // System.out.println("Json data after dropping is ");
                            // System.out.println(stAfterDrop);

                            if (stBeforePickUp == "") {
                                stBeforePickUp = dbAdapter.getTime(stDSNo, "stBeforePickup");
                                // System.out.println("st before pickup is "+stBeforePickUp);
                            }

                            if (stAfterPickUp == "") {
                                stAfterPickUp = dbAdapter.getTime(stDSNo, "stAfterPickup");
                                // System.out.println("st after pickup is "+stAfterPickUp);
                            }

                            if (stAfterDrop == "") {
                                stAfterDrop = dbAdapter.getTime(stDSNo, "stAfterDrop");
                                // System.out.println("st after drop is "+stAfterDrop);
                            }


                            jsondata = dbAdapter.getTodaysValues();

                            // System.out.println("jsondata is "+jsondata);
                            // pickupLat=pref.getString("pickup_lat",null);
                            //pickupLong=pref.getString("pickup_long",null);
                            pickupLat = dbAdapter.getTime(stDSNo, "pickup_lat");
                            pickupLong = dbAdapter.getTime(stDSNo, "pickup_long");
                            dropLat = String.valueOf(current_lat);
                            dropLong = String.valueOf(current_long);


                            if (startingTime == null) {
                                startingTime = dbAdapter.getTime(stDSNo, "startingTime");
                            }


                            if (!(pickupLat.equals("")) || !(pickupLong.equals(""))) {

                                if (!(dropLat.equals("0.0")) || !(dropLong.equals("0.0"))) {

                                    flagForFinishClicked=true;

                                    //8 km
                                    //getWaypointsTillNow("17.4183,78.5439|17.4183,78.544|17.4172,78.5439|17.4194,78.541|17.4221,78.5384|17.4253,78.5337|17.4258,78.5297|17.4219,78.526|17.4174,78.5272|17.4147,78.5247|17.4103,78.521|17.4068,78.5177|17.4053,78.5157|17.4042,78.5117|17.4036,78.5109|17.4013,78.5088|17.3994,78.5064|17.3967,78.5038|17.3951,78.5022|17.3945,78.4994|17.394,78.4987|17.3924,78.497|17.3905,78.4963|17.3896,78.4962|17.3894,78.4961|17.3894,78.4961");
                                    //12 km
                                    //getWaypointsTillNow("17.4186,78.5443|17.4183,78.5443|17.4183,78.5443|17.4178,78.5444|17.4172,78.544|17.4155,78.5458|17.4133,78.5482|17.4108,78.551|17.4082,78.5536|17.4059,78.5556|17.4035,78.5578|17.4025,78.5593|17.4015,78.5604|17.4004,78.5603|17.3975,78.56|17.3953,78.5592|17.3922,78.5588|17.3891,78.5587|17.3871,78.5585|17.3836,78.5583|17.3805,78.5581|17.3781,78.5579|17.38,78.558|17.383,78.5583|17.3856,78.5584|17.388,78.5587|17.3904,78.5587|17.3933,78.5589|17.3964,78.5593|17.3987,78.5599|17.4012,78.5601|17.4026,78.5588|17.4037,78.5575|17.4052,78.5559|17.4071,78.5544|17.4089,78.5528|17.4106,78.551|17.4125,78.5489|17.4137,78.5475|17.4146,78.5465|17.417,78.5436|17.4187,78.5418|17.4197,78.5408|17.4212,78.5397|17.4221,78.5385|17.4208,78.5401|17.4188,78.5418|17.4175,78.5431|17.4177,78.5436|17.4181,78.544|17.4181,78.544");
                                    //4km
                                    //getWaypointsTillNow("17.4183,78.544|17.4183,78.5441|17.418,78.5445|17.4162,78.545|17.4162,78.545|17.4162,78.545|17.4162,78.545|17.406,78.5558|17.406,78.5558|17.4137,78.5475|17.4173,78.5434|17.4188,78.5417|17.4181,78.5426");

                                    getWaypointsTillNow(dbAdapter.getWaypointsForSnapToRoad(stDSNo));

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


//                            Bundle b=new Bundle();
//                            b.putLong("Result",res);
//                            b.putString("starting_time",startingTime);
//                            b.putString("ending_time",endingTime);
//                            b.putString("idle_time",date3);
//                            b.putString("jDetails",data);
//                            b.putString("stBeforePickUp",stBeforePickUp);
//                            b.putString("stAfterPickUp",stAfterPickUp);
//                            b.putString("stAfterDrop",stAfterDrop);
//                            b.putString("sign",signature);
//                            String[] timeArray = date3.split(":");
//                            String hh=timeArray[0];
//                            String mm=timeArray[1];
//                            final String  p=hh+"."+mm;
//                            String pause=p+""+diff_times;
//                            System.out.println("pause"+pause);
//                            b.putString("pause",pause);
//                            res=0;
//                            // Intent j=new Intent(getActivity(),ResultActivity.class);
//                            Intent j=new Intent(getActivity(),HomeActivity.class);
//                            j.putExtras(b);
//                            j.putExtra("mylist",myList);
//                            //j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(j);
//                            getActivity().finish();
//                        }
//                        alertDialog.dismiss();
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


        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getGPSLocationUpdates();

        }
        else {

            Toast.makeText(getActivity(),"GPS is not enabled... Please Turn ON!",Toast.LENGTH_LONG).show();
            Intent i=new Intent(getActivity(),HomeActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }

    @Override
    public void onStart() {

        super.onStart();

        //System.out.println("Google API client in start is "+mGoogleApiClient);
        // System.out.println("the enetred values in onStart is "+entered);

    }

    @Override
    public void onStop() {

        super.onStop();
        // System.out.println("Google API client in stop is "+mGoogleApiClient);

    }

    @Override
    public void onPause() {
        super.onPause();

        // System.out.println("Google API client in pause is "+mGoogleApiClient);
    }

    @Override
    public void onResume() {
        super.onResume();

        // System.out.println("Google API client in resume is "+mGoogleApiClient);
        //System.out.println("the enetred values in onResume is "+entered);

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
            // System.out.println("coordinates are... "+location.getLatitude()+"::"+location.getLongitude());


            if (location != null && location.hasAccuracy()) {

                if (location.getAccuracy() <= inAccurate) {

                    if (location.getLatitude() != 0 && location.getLongitude() != 0) {

                        current_lat = location.getLatitude();
                        current_long = location.getLongitude();

                        // System.out.println("Current location lat long are " + current_lat + ":::" + current_long);

                        curntloc = new LatLng(current_lat, current_long);

                        if(first) {

                            String pickUpLat="",pickupLng="";
                            pickUpLat=dbAdapter.getTime(stDSNo,"pickup_lat");
                            pickupLng=dbAdapter.getTime(stDSNo,"pickup_long");

                            if (pickUpLat.equals("") || pickupLng.equals("")) {

                                dbAdapter.insertTimes("pickup_lat", String.valueOf(current_lat), stDSNo);
                                dbAdapter.insertTimes("pickup_long", String.valueOf(current_long), stDSNo);
                                // first = false;
                            }

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
    }
*/

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        latitude1=gps.getLastKnownLatitude();
        longitude1=gps.getLastKnownLongitude();

        //System.out.println("OnMapReady is "+current_lat+"::"+current_long);


        if(latitude1!=0.0 && longitude1!=0.0) {

            lastloc = new LatLng(latitude1,longitude1);
            //lastLocDist = lastloc;

            if (marker != null) {

                marker.setPosition(lastloc);
            } else {
                marker = mMap.addMarker(new MarkerOptions().position(lastloc).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_new)));
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
                    tvPlace.setText(address + " " + add1 + " " + add2);
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
                        // System.out.println("pick Time updated is"+pickTimeUpdated);

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
                        // System.out.println("pick Time updated is"+pickTimeUpdated);

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
                Toast.makeText(getActivity(), "Location Permission is required for app to run!", Toast.LENGTH_LONG).show();
                getActivity().finish();
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

                h.postDelayed(r,5000);
                //currentTime = java.text.DateFormat.getTimeInstance().format(new Date());

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

                    if (marker != null) {

                    } else {

                        marker = mMap.addMarker(new MarkerOptions().position(curntloc).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_new)));
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
//                    CameraPosition pos = CameraPosition.builder(oldPos).
//                            zoom(16).
//                            bearing((float)toRotation).
//                            build();
//                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

                    /*if (runningPathPolyline == null) {

                        LatLng from = new LatLng(lastLocDist.latitude,lastLocDist.longitude);

                        LatLng to = new LatLng(current_lat,current_long);

                        runningPathPolyline = mMap.addPolyline(new PolylineOptions()
                                .add(from, to)
                                .width(10).color(Color.parseColor("#801B60FE")).geodesic(true));

                    } else {
                        LatLng to = new LatLng(current_lat,current_long);

                        List<LatLng> points = runningPathPolyline.getPoints();
                        points.add(to);

                        runningPathPolyline.setPoints(points);
                    }*/

                    Location.distanceBetween(lastLocDist.latitude, lastLocDist.longitude, current_lat, current_long, results);

                    //System.out.println("RESULT ISS GNG"+results[0]);

                    if(results!=null) {
                        res = res + results[0];
                    }

                    //System.out.println("FINAL RESULT ISS GNG"+res);


                }
                else {

                    if (current_lat != 0.0 && current_long != 0.0 ) {

                        curntloc = new LatLng(current_lat, current_long);

                        if (marker != null) {

                        } else {

                            marker = mMap.addMarker(new MarkerOptions().position(curntloc).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_new)));
                        }

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, 16));
                        mMap.getUiSettings().setMapToolbarEnabled(false);
                    }


                }

                lastLocDist = new LatLng(current_lat,current_long);

            }
        };

        h.post(r);
    }

    public void storeCoordinatesForDistance()
    {
        hDist=new Handler();
        rDist=new Runnable() {
            @Override
            public void run() {

                hDist.postDelayed(rDist,60000);//5 min ~ 300 sec //changed to 1 min ~ 60 sec

                currentTime = getCurrentTime();
                Date date = new Date();
                timeUpdated = dateFormat.format(date);


                //System.out.println("timeupdated.."+timeUpdated+" &count"+countForLocUpdate);

                //if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude != 0.0 && lastLocDist.longitude != 0.0) {
                if (current_lat != 0.0 && current_long != 0.0) {

                    //Location.distanceBetween(lastLocDist.latitude, lastLocDist.longitude, current_lat, current_long,dist );

                    dbAdapter.insertEntry(stDSNo,current_lat, current_long, complete_address, resDist, timeUpdated);
                    dbAdapter.insertLatLngEntry(stDSNo,current_lat,current_long,timeUpdated);

                    //System.out.println("ProfileId isss "+pref.getString("profileId",""));
                    //System.out.println("Coordinates"+current_lat+"::"+current_long);

                    if(countForLocUpdate==0||countForLocUpdate==5) {

                        JsonObject v = new JsonObject();
                        v.addProperty("profileid", pref.getString("profileId", ""));
                        v.addProperty("longitude", current_long);
                        v.addProperty("latittude", current_lat);

                        Call<Pojo> call = REST_CLIENT.updateCoordinates(v);
                        call.enqueue(new Callback<Pojo>() {
                            @Override
                            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                if (response.isSuccessful()) {

                                    //System.out.println("latlng updated.............");


                                }
                            }

                            @Override
                            public void onFailure(Call<Pojo> call, Throwable t) {

                                Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_SHORT).show();

                            }
                        });

                        countForLocUpdate=0;
                    }

                    countForLocUpdate++;

                    //System.out.println("Waypoint count..."+dbAdapter.getWaypointsCount(stDSNo));

                    if(dbAdapter.getWaypointsCount(stDSNo)>=100)
                    {
                        //System.out.println("Waypoints for snap to road are.."+dbAdapter.getWaypointsForSnapToRoad(stDSNo));
                        getWaypointsTillNow(dbAdapter.getWaypointsForSnapToRoad(stDSNo));
                        //dbAdapter.deleteLatLng(stDSNo);
                        //System.out.println("Waypoint count after delete.."+dbAdapter.getWaypointsCount(stDSNo));


                    }

                } else {

                    //dbAdapter.insertEntry(0.0, 0.0, complete_address, resDist, timeUpdated);

                }

                // lastLocDist = new LatLng(current_lat, current_long);

                if (dbAdapter.findDSNo(stDSNo)) {
                    dbAdapter.updateDutyUpdates(stDSNo, currentTime, resDist, current_lat, current_long);

                } else {

                    dbAdapter.insertDutyUpdates(stDSNo, stTravelType, stDriverId, stRdate, stRTime,resDist, startingTime, currentTime, stGuestName, stGuestMobile, current_lat, current_long, diff, diff_times);
                }

            }
        };
        hDist.post(rDist);
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


    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        this.activity=(TrackRideOngoing) activity;
    }


    public void getGPSLocationUpdates()
    {
        g=new Handler();
        gR=new Runnable() {
            @Override
            public void run() {

                g.postDelayed(this, 2000);

                //System.out.println("gps calling....");

                tempLat = gps.getLatitude();
                tempLong = gps.getLongitude();
                accuracy=gps.getAccuracy();
                getTime=gps.getTime();

                /*System.out.println("Temp values are...");
                System.out.println("1.."+tempLat);
                System.out.println("2.."+tempLong);
                System.out.println("3.."+accuracy);
                System.out.println("4.."+getTime);*/

                if (tempLat != 0.0 && tempLong != 0.0) {

                    if (first) {

                        current_lat = gps.getLatitude();
                        current_long = gps.getLongitude();

                        /*pickupLat = String.valueOf(current_lat);
                        pickupLong = String.valueOf(current_long);

                        crntPickupLat= String.valueOf(current_lat);
                        crntPickupLng = String.valueOf(current_long);

                        dbAdapter.insertTimes("pickup_lat", String.valueOf(pickupLat), stDSNo);
                        dbAdapter.insertTimes("pickup_long", String.valueOf(pickupLong), stDSNo);

                        */
                        gettingLocationUpdates();
                        storeCoordinatesForDistance();

                        first = false;

                    } else {

                        //System.out.println("Kalman processing....location.Accuracy"+accuracy);
                        //System.out.println("Coordinates..."+tempLat+"::"+tempLong);

                        // if(accuracy<=10) {

                        kalmanLatLong.Process(
                                tempLat,
                                tempLong,
                                accuracy,
                                getTime);

                        //System.out.println("Kalman accuracy.."+kalmanLatLong.get_accuracy());
                        //System.out.println("Kalman Coordinates"+kalmanLatLong.get_lat()+"::"+kalmanLatLong.get_lng());

                        //if (kalmanLatLong.get_accuracy() <= 10) {

                        current_lat = kalmanLatLong.get_lat();
                        current_long = kalmanLatLong.get_lng();


                        // }

                        /*if (kalmanLatLong.get_accuracy() <= 10) {

                        }
                        else {

                            Toast.makeText(getActivity(),"s***"+kalmanLatLong.get_accuracy(),Toast.LENGTH_SHORT).show();
                        }*/


                        // }
                    }
                }
                else {

                    Toast.makeText(getActivity(), "Getting Location.. Please wait!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        g.post(gR);
    }

    public void getDistanceAndTime(final String latitude,final String longitude)
    {
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + latitude + "," + longitude + "&destination=" + pickupLat + "," + pickupLong + "&key=AIzaSyCIx6j-T1Yd5UuQUgnuRY04ZdoDF4xCW0E";


        //System.out.println("!1 "+urlString);

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

                                startToReportKms = startToReportKms + d.getValue();

                                Duration t=lData.getDuration();

                                startToReportHrs=startToReportHrs+t.getValue();

                                // System.out.println("dist and value is " + d.getValue() + ":::" + distance);
                            }

                        }

                        startToReportKms = startToReportKms / 1000;


                        int Hours = (int) (startToReportHrs/(3600));
                        int Mins = (int) (startToReportHrs % 3600) / 60;
                        int Secs = (int) (startToReportHrs) % 60;

                        //System.out.println("*********"+startToReportHrs+"@"+Hours+"@"+Mins+"@"+Secs);

                        /*DecimalFormat formatter = new DecimalFormat("00");
                        String hFormatted = formatter.format(Hours);
                        String mFormatted = formatter.format(Mins);
                        String sFormatted = formatter.format(Secs);*/

                        startToReportHrs=Float.parseFloat(twoDigitString(Hours) + "." + twoDigitString(Mins));

                        //System.out.println("*********"+startToReportHrs);


                        //////////////////////

                        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                                "origin=" + dropLat + "," + dropLong + "&destination=" + latitude + "," + longitude + "&key=AIzaSyCIx6j-T1Yd5UuQUgnuRY04ZdoDF4xCW0E";


                        //System.out.println("!2 "+urlString);

                        Call<DistancePojo> call1 = REST_CLIENT.getDistanceDetails(urlString);
                        call1.enqueue(new Callback<DistancePojo>() {
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

                                                finishToGarageKms = finishToGarageKms + d.getValue();

                                                Duration t=lData.getDuration();

                                                finishToGarageHrs=finishToGarageHrs+t.getValue();

                                                // System.out.println("dist and value is " + d.getValue() + ":::" + distance);
                                            }

                                        }

                                        finishToGarageKms = finishToGarageKms / 1000;

                                        int Hours = (int) (finishToGarageHrs/3600);
                                        int Mins = (int) (finishToGarageHrs % 3600) / 60;
                                        long Secs = (int) (finishToGarageHrs) % 60;


                                        finishToGarageHrs=Float.parseFloat(twoDigitString(Hours) + "." + twoDigitString(Mins));


                                        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                        //System.out.println(startToReportKms+":"+startToReportHrs+":"+finishToGarageKms+":"+finishToGarageHrs);
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
                                        b.putString("stAfterDrop", stAfterDrop+"@DistanceAlgo(m):"+res+"@URL:"+dbAdapter.getLocUrl(stDSNo));
                                        b.putString("sign", signature);
                                        b.putFloat("startToReportKms",startToReportKms);
                                        b.putFloat("startToReportHrs",startToReportHrs);
                                        b.putFloat("finishToGarageKms",finishToGarageKms);
                                        b.putFloat("finishToGarageHrs",finishToGarageHrs);

                                        String[] timeArray = date3.split(":");
                                        String hh = timeArray[0];
                                        String mm = timeArray[1];
                                        final String p = hh + "." + mm;
                                        pause = p + "" + diff_times;
                                        // System.out.println("pause" + pause);
                                        b.putString("pause", pause);

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

                                if(progressDialog!=null) {
                                    progressDialog.dismiss();
                                }

                                if(alertDialog!=null) {
                                    alertDialog.dismiss();
                                }

                                // Toast.makeText(MapsActivity.this,"Connectivity Error..Please Try Again!",Toast.LENGTH_LONG).show();
                                btStoreData.setVisibility(View.VISIBLE);
                                //  btFinish.setVisibility(View.GONE);
                                // dbAdapter.insertDutyEntry(stDSNo,pickupLat,pickupLong,dropLat,dropLong,stDriverId,stStartDate,stWaypoints,jsondata,stBeforePickUp,stAfterPickUp,stAfterDrop,tot_time,pause,date3,stGuestName,stGuestMobile,startingTime,endingTime);
                                Toast.makeText(getActivity(), "Internet connection issue..Please try again!", Toast.LENGTH_LONG).show();
                            }
                        });


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
                Toast.makeText(getActivity(), "Internet connection issue..Please try again!", Toast.LENGTH_LONG).show();

            }
        });
    }


    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    public void getWaypointsTillNow(String path)
    {
        String urlString="https://roads.googleapis.com/v1/snapToRoads?path="+path+"&interpolate=true&key=AIzaSyD_Z4C_OyZqrAU2prWEH_9-mZL2N3N_dZE";

        //System.out.println("urlSTribg issss"+urlString);
        dbAdapter.insertLocUrl(stDSNo,"SnapToRoadAPI:"+urlString);

        Call<SnapDistance>call=REST_CLIENT.getSnapToRoadDetails(urlString);
        call.enqueue(new Callback<SnapDistance>() {
            @Override
            public void onResponse(Call<SnapDistance> call, Response<SnapDistance> response) {

                SnapDistance sd;
                List<SnappedPoint> aL;
                ArrayList<SnappedPoint> aS=new ArrayList<>();
                SnappedPoint s;
                ArrayList<com.hjsoft.mmtravels.model.Location> aLoc=new ArrayList<>();
                ArrayList<com.hjsoft.mmtravels.model.Location> aNewLoc=new ArrayList<>();

                com.hjsoft.mmtravels.model.Location loc;

                if(response.isSuccessful())
                {
                    sd=response.body();
                    aL=sd.getSnappedPoints();

                    //System.out.println("Location arraylist size"+aL.size());

                    for(int i=0;i< aL.size();i++)
                    {
                        s=aL.get(i);

                        aLoc.add(s.getLocation());

                        if(s.getOriginalIndex()!=null)
                        {
                            //System.out.println("Is s.originalIndex present inside..."+s.getOriginalIndex());
                            aNewLoc.add(s.getLocation());
                        }

                    }

                    snapToRoadWaypoints=stWaypoints;
                    //dbAdapter.deleteLatLng(stDSNo);

                    getDistanceInIntervals(aNewLoc);

                }
                else {

                    Toast.makeText(getActivity(),"Please try again!",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SnapDistance> call, Throwable t) {

                if(progressDialog!=null) {
                    progressDialog.dismiss();
                }

                if(alertDialog!=null) {
                    alertDialog.dismiss();
                }

                Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getDistanceInIntervals(ArrayList<com.hjsoft.mmtravels.model.Location> alist)
    {
        int n=alist.size()/3;

        a1.clear();
        a2.clear();
        a3.clear();

        //System.out.println("aList.size***"+alist.size());
        //System.out.println("n isss"+n);

        for(int i=0;i<alist.size();i++)
        {
            if(i<n)
            {
                a1.add(alist.get(i));
            }
            else if(i>=n&&i<=2*n)
            {
                if(i==n)
                {
                    a1.add(alist.get(i));
                }
                a2.add(alist.get(i));

                if(i==2*n)
                {
                    a3.add(alist.get(i));
                }
            }
            else {

                a3.add(alist.get(i));
            }
        }

        /*System.out.println("a1.size***"+a1.size());
        System.out.println("a2.size***"+a2.size());
        System.out.println("a3.size***"+a3.size());*/

        calculateIterativeDistance(a1);



    }

    public void calculateIterativeDistance(ArrayList<com.hjsoft.mmtravels.model.Location> a4)
    {
        if(a4.size()!=0) {

            String olat = "", olng = "", dlat = "", dlng = "", waypoints = "";
            int c=0;
            int last = a4.size();
            olat = String.valueOf(a4.get(0).getLatitude());
            olng = String.valueOf(a4.get(0).getLongitude());

            dlat = String.valueOf(a4.get(last - 1).getLatitude());
            dlng = String.valueOf(a4.get(last - 1).getLongitude());

            if(a4.size()<=19)
            {
                c=1;
            }
            else {
                c=2;
            }

            for (int i = 0; (c*i) < a4.size(); i++) {

                if (i == 0) {
                    waypoints = String.valueOf(a4.get(i).getLatitude()) + "," + String.valueOf(a4.get(i).getLongitude());
                } else {

                    waypoints = waypoints + "|" + String.valueOf(a4.get(c*i).getLatitude()) + "," + String.valueOf(a4.get(c*i).getLongitude());

                }
            }


            /*for (int i = 0; (2*i) < a4.size(); i++) {
                if (i == 0) {
                    waypoints = String.valueOf(a4.get(i).getLatitude()) + "," + String.valueOf(a4.get(i).getLongitude());
                } else {

                    waypoints = waypoints + "|" + String.valueOf(a4.get(2*i).getLatitude()) + "," + String.valueOf(a4.get(2*i).getLongitude());

                }

                //System.out.println("i issss"+i);
            }*/

            String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + olat + "," + olng + "&destination=" + dlat + "," + dlng + "&waypoints=" + waypoints + "&key=AIzaSyCIx6j-T1Yd5UuQUgnuRY04ZdoDF4xCW0E";

            //System.out.println("Distance urlString iss" + urlString);

            dbAdapter.insertLocUrl(stDSNo,"DirectionsMatrixAPI:"+urlString);

            Call<DistancePojo> call = REST_CLIENT.getDistanceDetails(urlString);
            call.enqueue(new Callback<DistancePojo>() {
                @Override
                public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                    DistancePojo data;
                    Route rData;
                    Leg lData;
                    float dist = 0;

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

                                    dist = dist + d.getValue();

                                    // System.out.println("dist and value is " + d.getValue() + ":::" + distance);
                                }

                            }

                            //System.out.println("dist isssssss "+dist);

                            float d = pref.getFloat("distance", 0);
                            d = d + dist;
                            System.out.println("dist from this api isss" + dist);
                            System.out.println("d value issss " + d);
                            editor.putFloat("distance", d);
                            editor.commit();

                            crntPickupLat = String.valueOf(current_lat);
                            crntPickupLng = String.valueOf(current_long);

                            editor.putString("from_lat", crntPickupLat);
                            editor.putString("from_lng", crntPickupLng);
                            editor.commit();

                            iteration++;

                            ///////////////////
                            if (iteration == 2) {
                                calculateIterativeDistance(a2);
                            }

                            if (iteration == 3) {

                                calculateIterativeDistance(a3);

                            }

                            if (iteration == 4) {
                                System.out.println("D VALUE FINALLY ISS::" + d);
                                iteration = 1;

                                //dbAdapter.deleteLatLng(stDSNo);

                                System.out.println("***All Details***");
                                System.out.println(stDSNo);
                                System.out.println(dbAdapter.getIntervalWaypoints(stDSNo));
                                System.out.println(dbAdapter.getLocUrl(stDSNo));
                                System.out.println(pref.getFloat("distance", 0));
                                System.out.println("************************");


                                //************************

                                JsonObject v=new JsonObject();
                                v.addProperty("dslipid",stDSNo);
                                v.addProperty("tripdetails",dbAdapter.getIntervalWaypoints(stDSNo));
                                v.addProperty("distancepoints",dbAdapter.getLocUrl(stDSNo));
                                v.addProperty("Totalkms",pref.getFloat("distance", 0));

                                Call<Pojo> call1=REST_CLIENT.sendDistanceForInterval(v);
                                call1.enqueue(new Callback<Pojo>() {
                                    @Override
                                    public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                                        if(response.isSuccessful())
                                        {
                                            dbAdapter.deleteLatLng(stDSNo);
                                            dbAdapter.deleteLocUrl(stDSNo);


                                            if (flagForFinishClicked) {
                                                finishClicked();
                                            }
                                            else {
                                                a1.clear();
                                                a2.clear();
                                                a3.clear();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<Pojo> call, Throwable t) {

                                        Toast.makeText(getActivity(),"Issue in Internet connection!!",Toast.LENGTH_SHORT).show();
                                    }
                                });

                                //**************************

                                /*dbAdapter.deleteLatLng(stDSNo);
                                dbAdapter.deleteLocUrl(stDSNo);

                                if (flagForFinishClicked) {
                                    finishClicked();
                                }
                                else {
                                    a1.clear();
                                    a2.clear();
                                    a3.clear();
                                }*/

                            }


                        } else {

                            //  Toast.makeText(MapsActivity.this,data.getStatus(),Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // System.out.println(response.message() + "::" + response.code() + "::" + response.isSuccessful());
                    }
                }

                @Override
                public void onFailure(Call<DistancePojo> call, Throwable t) {

                    //btStoreData.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {

            iteration++;

            if (iteration == 2) {
                calculateIterativeDistance(a2);
            }

            if (iteration == 3) {
                calculateIterativeDistance(a3);
            }

            if (iteration == 4) {

                iteration = 1;

                //dbAdapter.deleteLatLng(stDSNo);

                System.out.println("***All Details***");
                System.out.println(stDSNo);
                System.out.println(dbAdapter.getIntervalWaypoints(stDSNo));
                System.out.println(dbAdapter.getLocUrl(stDSNo));
                System.out.println(pref.getFloat("distance", 0));
                System.out.println("************************");


                //************************

                JsonObject v=new JsonObject();
                v.addProperty("dslipid",stDSNo);
                v.addProperty("tripdetails",dbAdapter.getIntervalWaypoints(stDSNo));
                v.addProperty("distancepoints",dbAdapter.getLocUrl(stDSNo));
                v.addProperty("Totalkms",pref.getFloat("distance", 0));

                Call<Pojo> call1=REST_CLIENT.sendDistanceForInterval(v);
                call1.enqueue(new Callback<Pojo>() {
                    @Override
                    public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                        if(response.isSuccessful())
                        {
                            dbAdapter.deleteLatLng(stDSNo);
                            dbAdapter.deleteLocUrl(stDSNo);


                            if (flagForFinishClicked) {
                                finishClicked();
                            }
                            else {
                                a1.clear();
                                a2.clear();
                                a3.clear();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Pojo> call, Throwable t) {

                        Toast.makeText(getActivity(),"Issue in Internet connection!!",Toast.LENGTH_SHORT).show();


                    }
                });

                //**************************

                /*dbAdapter.deleteLatLng(stDSNo);
                dbAdapter.deleteLocUrl(stDSNo);

                if (flagForFinishClicked) {
                    finishClicked();
                }
                else {
                    a1.clear();
                    a2.clear();
                    a3.clear();
                }*/

            }
        }
    }

    public void finishClicked()
    {
        float d=pref.getFloat("distance",0);

        //System.out.println("d finally is isssssss "+d);

        finalDistance=d/1000;

        //System.out.println("FINAL DISTANCE before isssssss "+finalDistance);

        DecimalFormat df = new DecimalFormat("#.00");

        finalDistance=Float.parseFloat(df.format(finalDistance));


        //System.out.println("FINAL DISTANCE after isssssss "+finalDistance);

        //System.out.println("Printing snapToRoadWaypoints..."+stAfterDrop+" snapToRoadWaypoints: "+snapToRoadWaypoints);


        //Toast.makeText(getActivity(),"Final distance isss"+finalDistance,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(),"DIstance from Algo is"+res,Toast.LENGTH_SHORT).show();

        if(garageStatus.equals("Applicable"))
        {
            getDistanceAndTime(latitude,longitude);
        }
        else {


            Bundle b = new Bundle();
            b.putFloat("Result", finalDistance);
            b.putString("starting_time", startingTime);
            b.putString("ending_time", endingTime);
            b.putString("idle_time", date3);
            b.putInt("position", position);
            b.putString("jDetails", jsondata);
            b.putString("stBeforePickUp", stBeforePickUp);
            b.putString("stAfterPickUp", stAfterPickUp);
            b.putString("stAfterDrop", stAfterDrop+"@DistanceAlgo(m):"+res+"@URL:"+dbAdapter.getLocUrl(stDSNo));
            b.putString("sign", signature);
            b.putFloat("startToReportKms", startToReportKms);
            b.putFloat("startToReportHrs", startToReportHrs);
            b.putFloat("finishToGarageKms", finishToGarageKms);
            b.putFloat("finishToGarageHrs", finishToGarageHrs);
            String[] timeArray = date3.split(":");
            String hh = timeArray[0];
            String mm = timeArray[1];
            final String p = hh + "." + mm;
            pause = p + "" + diff_times;
            // System.out.println("pause" + pause);
            b.putString("pause", pause);

            progressDialog.dismiss();
            alertDialog.dismiss();

            // Intent j=new Intent(getActivity(),ResultActivity.class);
            Intent j = new Intent(getActivity(), ResultActivity.class);
            j.putExtras(b);
            j.putExtra("mylist", myList);
            j.putExtra("position", position);

            //j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
            getActivity().finish();
        }
    }

    public void getDistanceTillNow(String lat,String lng,String waypoints)
    {
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + lat + "," + lng + "&destination=" + current_lat + "," + current_long + "&waypoints=" + waypoints + "&key=AIzaSyCIx6j-T1Yd5UuQUgnuRY04ZdoDF4xCW0E";
        //System.out.println("Distance urlString iss"+urlString);

        Call<DistancePojo> call = REST_CLIENT.getDistanceDetails(urlString);
        call.enqueue(new Callback<DistancePojo>() {
            @Override
            public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                DistancePojo data;
                Route rData;
                Leg lData;
                float dist=0;

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

                                dist = dist + d.getValue();

                                // System.out.println("dist and value is " + d.getValue() + ":::" + distance);
                            }

                        }

                        //System.out.println("dist isssssss "+dist);

                        float d=pref.getFloat("distance",0);
                        d=d+dist;
                        //System.out.println("d value issss "+d);
                        editor.putFloat("distance",d);
                        editor.commit();

                        crntPickupLat=String.valueOf(current_lat);
                        crntPickupLng=String.valueOf(current_long);

                        editor.putString("from_lat",crntPickupLat);
                        editor.putString("from_lng",crntPickupLng);
                        editor.commit();

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

                //progressDialog.dismiss();

                // Toast.makeText(MapsActivity.this,"Connectivity Error..Please Try Again!",Toast.LENGTH_LONG).show();
                btStoreData.setVisibility(View.VISIBLE);
                //  btFinish.setVisibility(View.GONE);
                // dbAdapter.insertDutyEntry(stDSNo,pickupLat,pickupLong,dropLat,dropLong,stDriverId,stStartDate,stWaypoints,jsondata,stBeforePickUp,stAfterPickUp,stAfterDrop,tot_time,pause,date3,stGuestName,stGuestMobile,startingTime,endingTime);
                Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getFinalDistance(String lat,String lng,String waypoints)
    {
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + lat + "," + lng + "&destination=" + current_lat + "," + current_long + "&waypoints=" + waypoints + "&key=AIzaSyCIx6j-T1Yd5UuQUgnuRY04ZdoDF4xCW0E";


        //System.out.println("Distance urlString iss"+urlString);

        Call<DistancePojo> call = REST_CLIENT.getDistanceDetails(urlString);
        call.enqueue(new Callback<DistancePojo>() {
            @Override
            public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                DistancePojo data;
                Route rData;
                Leg lData;
                float dist=0;

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

                                dist = dist + d.getValue();

                                // System.out.println("dist and value is " + d.getValue() + ":::" + distance);
                            }

                        }

                        progressDialog.dismiss();
                        alertDialog.dismiss();

                        float d=pref.getFloat("distance",0);
                        d=d+dist;

                        /*approxFinalDist=(d*5)/100;

                        System.out.println("d issss"+d);

                        System.out.println("approx final distisssssss "+approxFinalDist);

                        d=d+approxFinalDist;

                        System.out.println("d finally is isssssss "+d);*/

                        finalDistance=d/1000;

                        System.out.println("FINAL DISTANCE before isssssss "+finalDistance);


                        DecimalFormat df = new DecimalFormat("#.00");

                        finalDistance=Float.parseFloat(df.format(finalDistance));

                        System.out.println("FINAL DISTANCE after isssssss "+finalDistance);

                        System.out.println("Printing snapToRoadWaypoints..."+stAfterDrop+" snapToRoadWaypoints: "+snapToRoadWaypoints);


                        //System.out.println("FINAL DISTANCE after isssssss "+finalDistance);

                        if(garageStatus.equals("Applicable"))
                        {
                            getDistanceAndTime(latitude,longitude);
                        }
                        else {

                            Bundle b = new Bundle();
                            b.putFloat("Result", finalDistance);
                            b.putString("starting_time", startingTime);
                            b.putString("ending_time", endingTime);
                            b.putString("idle_time", date3);
                            b.putInt("position", position);
                            b.putString("jDetails", jsondata);
                            b.putString("stBeforePickUp", stBeforePickUp);
                            b.putString("stAfterPickUp", stAfterPickUp);
                            b.putString("stAfterDrop", "NR"+stAfterDrop+"@DistanceAlgo(m):"+res);
                            b.putString("sign", signature);
                            b.putFloat("startToReportKms", startToReportKms);
                            b.putFloat("startToReportHrs", startToReportHrs);
                            b.putFloat("finishToGarageKms", finishToGarageKms);
                            b.putFloat("finishToGarageHrs", finishToGarageHrs);
                            String[] timeArray = date3.split(":");
                            String hh = timeArray[0];
                            String mm = timeArray[1];
                            final String p = hh + "." + mm;
                            pause = p + "" + diff_times;
                            // System.out.println("pause" + pause);
                            b.putString("pause", pause);

                            // Intent j=new Intent(getActivity(),ResultActivity.class);
                            Intent j = new Intent(getActivity(), ResultActivity.class);
                            j.putExtras(b);
                            j.putExtra("mylist", myList);
                            j.putExtra("position", position);

                            //j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(j);
                            getActivity().finish();
                        }

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

                if(progressDialog!=null) {
                    progressDialog.dismiss();
                }

                if(alertDialog!=null) {
                    alertDialog.dismiss();
                }

                // Toast.makeText(MapsActivity.this,"Connectivity Error..Please Try Again!",Toast.LENGTH_LONG).show();
                btStoreData.setVisibility(View.VISIBLE);
                //  btFinish.setVisibility(View.GONE);
                // dbAdapter.insertDutyEntry(stDSNo,pickupLat,pickupLong,dropLat,dropLong,stDriverId,stStartDate,stWaypoints,jsondata,stBeforePickUp,stAfterPickUp,stAfterDrop,tot_time,pause,date3,stGuestName,stGuestMobile,startingTime,endingTime);
                Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }


}
