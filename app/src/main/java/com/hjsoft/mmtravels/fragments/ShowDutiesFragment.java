package com.hjsoft.mmtravels.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.SessionManager;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.adapters.RecyclerAdapter;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.HomePojo;
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
    String stName,stPwd,stCode;
    TextView tvNoDuties;
    RecyclerAdapter mAdapter;
    RecyclerView rView;
    View rootView;
    ImageButton ibRefresh;
    boolean bookingStatus=false;
    DBAdapter dbAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        session = new SessionManager(getActivity());
        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_show_duties, container, false);

        tvNoDuties=(TextView)rootView.findViewById(R.id.fsd_tv_no_duties);
        rView=(RecyclerView)rootView.findViewById(R.id.fsd_rv_list);
        ibRefresh=(ImageButton)rootView.findViewById(R.id.fsd_ib_refresh);

        REST_CLIENT= RestClient.get();
        dutyData=new ArrayList<DutyData>();

        user = session.getUserDetails();
        stName = user.get(SessionManager.KEY_NAME);
        stPwd = user.get(SessionManager.KEY_PWD);

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
}
