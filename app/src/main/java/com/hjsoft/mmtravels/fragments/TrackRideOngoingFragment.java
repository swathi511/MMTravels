package com.hjsoft.mmtravels.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.activity.HomeActivity;
import com.hjsoft.mmtravels.activity.ResultActivity;
import com.hjsoft.mmtravels.activity.SignatureActivity;
import com.hjsoft.mmtravels.activity.TrackRideOngoing;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.model.Distance;
import com.hjsoft.mmtravels.model.DistancePojo;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.DutyUpdates;
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
 * Created by hjsoft on 2/3/17.
 */
public class TrackRideOngoingFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    View rootView;

    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    double latitude,current_lat=0.0;
    double longitude,current_long=0.0;
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
    Handler h,hDist;
    Runnable r,rDist;
    float[] dist = new float[3];
    long res = 0, resDist = 0;
    float inAccurate = 10;
    String pickupLat,pickupLong,dropLat,dropLong;
    float finalDistance,distance=0;
    SharedPreferences pref;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    TrackRideOngoing activity;
    Button btStoreData;
    String stWaypoints,stStartDate,pause,date3,jsondata;
    boolean first=true;
    ProgressDialog progressDialog;
    int position;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);


        mRequestingLocationUpdates=false;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.fragment_track_ride_ongoing, container,false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

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
                break;
            case "otp":btValidate.setVisibility(View.VISIBLE);
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
                if (mGoogleApiClient.isConnected()) {
                    stopLocationUpdates();
                }

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

                dbAdapter.updateStatus(stDSNo,"otp");

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

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                adBtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        if (current_lat != 0 && current_long != 0) {

                            dbAdapter.insertEntry(stDSNo, current_lat, current_long, complete_address, resDist, timeUpdated);

                        }

                            dbAdapter.updateStatus(stDSNo,"finished");

                        if (mGoogleApiClient.isConnected()) {


                            endingTime=getCurrentTime();

                            Date date = new Date();
                            finishTimeUpdated=dateFormat.format(date);
                            dbAdapter.insertTimes("finishTimeUpdated",finishTimeUpdated,stDSNo);

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

                            if(dropTimeUpdated=="")
                            {
                                dropTimeUpdated=dbAdapter.getTime(stDSNo,"dropTimeUpdated");
                                // System.out.println("drop time is"+dropTimeUpdated);
                            }

                            stAfterDrop=dbAdapter.getIntervalData(dropTimeUpdated,finishTimeUpdated,stDSNo);
                            dbAdapter.insertTimes("stAfterDrop",stAfterDrop,stDSNo);
                            // System.out.println("Json data after dropping is ");
                            // System.out.println(stAfterDrop);

                            if(stBeforePickUp=="")
                            {
                                stBeforePickUp=dbAdapter.getTime(stDSNo,"stBeforePickup");
                                // System.out.println("st before pickup is "+stBeforePickUp);
                            }

                            if(stAfterPickUp=="")
                            {
                                stAfterPickUp=dbAdapter.getTime(stDSNo,"stAfterPickup");
                                // System.out.println("st after pickup is "+stAfterPickUp);
                            }

                            if(stAfterDrop=="")
                            {
                                stAfterDrop=dbAdapter.getTime(stDSNo,"stAfterDrop");
                                // System.out.println("st after drop is "+stAfterDrop);
                            }



                            jsondata = dbAdapter.getTodaysValues();

                            // System.out.println("jsondata is "+jsondata);
                            // pickupLat=pref.getString("pickup_lat",null);
                            //pickupLong=pref.getString("pickup_long",null);
                            pickupLat=dbAdapter.getTime(stDSNo,"pickup_lat");
                            pickupLong=dbAdapter.getTime(stDSNo,"pickup_long");
                            dropLat=String.valueOf(current_lat);
                            dropLong=String.valueOf(current_long);


                            if(startingTime==null)
                            {
                                startingTime=dbAdapter.getTime(stDSNo,"startingTime");
                            }


                            if(!(pickupLat.equals(""))||!(pickupLong.equals(""))) {

                                if (!(dropLat.equals("0.0")) || !(dropLong.equals("0.0"))) {

                                    stopLocationUpdates();
                                    mGoogleApiClient.disconnect();

                                    stWaypoints = dbAdapter.getWaypoints(stDSNo);
                                    // System.out.println("waypoints " + stWaypoints);


                                    /// main functionality

                                    String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                                            "origin=" + pickupLat + "," + pickupLong + "&destination=" + dropLat + "," + dropLong + "&waypoints=" + stWaypoints + "&key=AIzaSyBGK303F1k8pU7ncriAcctGuju_tw-gTIs";

                                    // System.out.println(urlString);

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

                                            // Toast.makeText(MapsActivity.this,"Connectivity Error..Please Try Again!",Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();

                                            btStoreData.setVisibility(View.VISIBLE);
                                            // btFinish.setVisibility(View.GONE);

                                            // dbAdapter.insertDutyEntry(stDSNo,pickupLat,pickupLong,dropLat,dropLong,stDriverId,stStartDate,stWaypoints,jsondata,stBeforePickUp,stAfterPickUp,stAfterDrop,tot_time,pause,date3,stGuestName,stGuestMobile,startingTime,endingTime);
                                            Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                    ////main functionality

                                } else {

                                    Toast.makeText(getActivity(), "Fetching data...Please try again!", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {

                                Toast.makeText(getActivity(), "Fetching data...Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }

                        progressDialog.dismiss();
                        alertDialog.dismiss();


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

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        entered=true;
        gettingLocationUpdates();
        storeCoordinatesForDistance();
    }

    @Override
    public void onStart() {

        super.onStart();

        if(Build.VERSION.SDK_INT>=23)
        {
            if(!entered)
            {

            }
            else
            {
                mGoogleApiClient.connect();
                //super.onStart();
            }
        }
        else
        {
            mGoogleApiClient.connect();
            // super.onStart();
        }
    }

    @Override
    public void onStop() {

        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(entered)
        {
            if(mGoogleApiClient.isConnecting()||mGoogleApiClient.isConnected())
            {

            }
            else {
                mGoogleApiClient.connect();
            }
        }

        if(mGoogleApiClient!=null) {

            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            //System.out.println("in buildGoogleApiClient after 'if' ");
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    protected void buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        //Location Settings Satisfied
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to resolve it
                        break;
                }
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);//45 sec
        mLocationRequest.setFastestInterval(1000);//5 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        //System.out.println("Location Updates Started..");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mapFragment.getMapAsync(this);
        if (mLastLocation == null) {
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        if(mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
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

                    /*

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

                    */

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        lastloc = new LatLng(latitude, longitude);
        current_lat=lastloc.latitude;
        current_long=lastloc.longitude;

        if(marker!=null) {

            marker.setPosition(lastloc);
        }
        else
        {
            marker = mMap.addMarker(new MarkerOptions().position(lastloc).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
        }


        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
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
                        startLocationUpdates();
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

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude != 0.0 && lastLocDist.longitude != 0.0)
                {

                    curntloc = new LatLng(current_lat, current_long);

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

                hDist.postDelayed(rDist,300000);//5 min ~ 300 sec

                currentTime = getCurrentTime();

                if (lastLocDist != null && current_lat != 0.0 && current_long != 0.0 && lastLocDist.latitude != 0.0 && lastLocDist.longitude != 0.0) {

                    // System.out.println(lastLocDist.latitude + ":" + lastLocDist.longitude);
                    // System.out.println(current_lat + ":" + current_long);

                    Location.distanceBetween(lastLocDist.latitude, lastLocDist.longitude, current_lat, current_long,dist );

                    // System.out.println(lastLocDist.latitude + ":" + lastLocDist.longitude + ":" + current_lat + ":" + current_long + "::" + dist[0]);


                    dbAdapter.insertEntry(stDSNo,current_lat, current_long, complete_address, resDist, timeUpdated);
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

    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

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
    }


    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        this.activity=(TrackRideOngoing) activity;
    }


}
