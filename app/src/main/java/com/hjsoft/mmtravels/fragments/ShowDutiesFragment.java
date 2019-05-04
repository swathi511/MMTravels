package com.hjsoft.mmtravels.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.GPSTracker;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.SessionManager;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.adapters.RecyclerAdapter;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.HomePojo;
import com.hjsoft.mmtravels.model.Pojo;
import com.hjsoft.mmtravels.webservices.API;
import com.hjsoft.mmtravels.webservices.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 28/2/17.
 */
public class ShowDutiesFragment extends Fragment {

    Call<List<HomePojo>> call;
    API REST_CLIENT;
    ArrayList<DutyData> dutyData;
    HashMap<String, String> user;
    SessionManager session;
    String stName,stPwd,stProfileId;
    TextView tvNoDuties;
    RecyclerAdapter mAdapter;
    RecyclerView rView;
    View rootView;
    ImageButton ibRefresh;
    boolean bookingStatus=false;
    DBAdapter dbAdapter;
    SharedPreferences pref;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    SharedPreferences.Editor editor;
    SwitchCompat switchCompat;
    String status;
    GPSTracker gps;
    Handler h1;
    Runnable r1;
    double current_lat,current_long;
    final static int REQUEST_LOCATION = 199;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity());
        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        REST_CLIENT= RestClient.get();
        gps=new GPSTracker(getActivity());

        stProfileId=pref.getString("profileId","");

        //System.out.println("profileId in Showduty"+stProfileId);

        if(Build.VERSION.SDK_INT<23)
        {
            //System.out.println("Sdk_int is"+Build.VERSION.SDK_INT);
            //System.out.println("the enetred values is "+entered);
            sendLocationCoordinates();

        }
        else
        {
            if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                sendLocationCoordinates();
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





    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_show_duties, container, false);

        tvNoDuties=(TextView)rootView.findViewById(R.id.fsd_tv_no_duties);
        rView=(RecyclerView)rootView.findViewById(R.id.fsd_rv_list);
        ibRefresh=(ImageButton)rootView.findViewById(R.id.fsd_ib_refresh);
        switchCompat=(SwitchCompat)rootView.findViewById(R.id.switchButton);

        dutyData=new ArrayList<DutyData>();

        user = session.getUserDetails();
        stName = user.get(SessionManager.KEY_NAME);
        stPwd = user.get(SessionManager.KEY_PWD);

        status=pref.getString("status","1");

        if(status.equals("1"))
        {
            switchCompat.setChecked(true);
            switchCompat.setText("Online");
            switchCompat.setBackgroundResource(R.drawable.rect_online);

        }
        else {
            switchCompat.setChecked(false);
            switchCompat.setText("Offline");
            switchCompat.setBackgroundResource(R.drawable.rect_offline);

        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String status=pref.getString("status","1");

                if(status.equals("1")) {

                    goingOffline();
                }
                else {

                    goingOnline();
                }
            }
        });



        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getAllRideDetails();
            }
        });

        getAllRideDetails();

        return rootView;
    }


    public void getAllRideDetails()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        Call<List<HomePojo>> call = REST_CLIENT.getInfo(stName,stPwd);
        call.enqueue(new Callback<List<HomePojo>>() {
            @Override
            public void onResponse(Call<List<HomePojo>> call, Response<List<HomePojo>> response) {

                List<HomePojo> data;
                HomePojo content;

                if(response.isSuccessful())
                {
                    ibRefresh.setVisibility(View.GONE);
                    data=response.body();

                    for (int i = 0; i < data.size(); i++) {

                        content = data.get(i);

                        if(dbAdapter.isDSNOpresent(content.getDslipid()))
                        {
                            bookingStatus=true;
                        }

                        dutyData.add(new DutyData(content.getDslipid(),content.getUddsno(),content.getBookingtype(),content.getStartdate(),content.getStarttime(),
                                content.getBookedvehicleid(),content.getBookedvehicletype(),content.getTraveltype(),
                                content.getPointpointid(),content.getPickuplocation(),content.getSlabname(),content.getGuestname(),content.getGuestmobile(),content.getDriverid(),content.getAcceptancestatus(),content.getStatus(),bookingStatus,
                                content.getGaragestatus(),content.getLatitude(),content.getLongitude()));

                        bookingStatus=false;
                    }
                }
                else
                {
                    progressDialog.dismiss();
                }

                //dutyData.add(new DutyData("1","1","a","a",Float.parseFloat("1"),"a","a","a","a","a","a","a","a","a","a"));
                //dutyData.add(new DutyData("2","2","a","a",Float.parseFloat("1"),"a","a","a","a","a","a","a","a","a","a"));

                if(dutyData.size()!=0) {

                    progressDialog.dismiss();

                    mAdapter = new RecyclerAdapter(getActivity(), dutyData);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rView.setLayoutManager(mLayoutManager);
                    rView.setItemAnimator(new DefaultItemAnimator());
                    rView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else
                {
                    ibRefresh.setVisibility(View.GONE);
                    tvNoDuties.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<HomePojo>> call, Throwable t) {

                ibRefresh.setVisibility(View.VISIBLE);

                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Please check Internet connection!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goingOffline()
    {
        JsonObject v=new JsonObject();
        v.addProperty("profileid",user.get(SessionManager.KEY_ID));
        v.addProperty("AppStatus","0");
        Call<Pojo> call=REST_CLIENT.changeStatus(v);

        call.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                if(response.isSuccessful())
                {
                    switchCompat.setChecked(false);
                    switchCompat.setText("Offline");
                    switchCompat.setBackgroundResource(R.drawable.rect_offline);
                    editor.putString("status","0");
                    editor.commit();
                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

                Toast.makeText(getActivity(),"Connection Issue!\nPlease try again.",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void goingOnline()
    {
        JsonObject v=new JsonObject();
        v.addProperty("profileid",user.get(SessionManager.KEY_ID));
        v.addProperty("AppStatus","1");
        Call<Pojo> call=REST_CLIENT.changeStatus(v);

        call.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                if(response.isSuccessful())
                {
                    switchCompat.setChecked(true);
                    switchCompat.setText("Online");
                    switchCompat.setBackgroundResource(R.drawable.rect_online);
                    editor.putString("status","1");
                    editor.commit();
                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

                Toast.makeText(getActivity(),"Connection Issue!\nPlease try again.",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        gps.stopUsingGPS();

        if(h1!=null) {
            h1.removeCallbacks(r1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                gps=new GPSTracker(getActivity());

                sendLocationCoordinates();

            } else {
                Toast.makeText(getActivity(), "Location Permission is required for app to run!", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void sendLocationCoordinates()
    {
        h1 = new Handler();
        r1 = new Runnable() {
            @Override
            public void run() {

                h1.postDelayed(this, 300000);

                current_lat=gps.getLatitude();
                current_long=gps.getLongitude();

                //System.out.println("Coordinates( "+current_lat+":::"+current_long+" )");
                //System.out.println("profileId in Showduty"+pref.getString("profileId", ""));


                if(current_lat!=0.0&&current_long!=0.0) {

                    JsonObject v = new JsonObject();
                    v.addProperty("profileid", pref.getString("profileId", ""));
                    v.addProperty("longitude", current_long);
                    v.addProperty("latittude", current_lat);

                    Call<Pojo> call = REST_CLIENT.updateCoordinates(v);
                    call.enqueue(new Callback<Pojo>() {
                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                            if (response.isSuccessful()) {
                                //Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            Toast.makeText(getActivity(), "Please check Internet connection!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }


            }
        };

        h1.post(r1);

    }
}
