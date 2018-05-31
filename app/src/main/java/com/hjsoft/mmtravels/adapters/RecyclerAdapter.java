package com.hjsoft.mmtravels.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.UpdatePojo;
import com.hjsoft.mmtravels.webservices.API;
import com.hjsoft.mmtravels.webservices.RestClient;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 27/2/17.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    ArrayList<DutyData> customArrayList;
    Context context;
    DutyData data;
    LayoutInflater inflater;
    boolean accept=false;
    API REST_CLIENT;
    Dialog dialog;
    private AdapterCallback mAdapterCallback;
    int pos;
    ArrayList<DutyData> mResultList;
    boolean status=false;

    public RecyclerAdapter(Context context, ArrayList<DutyData> customArrayList)
    {
        this.context=context;
        this.customArrayList=customArrayList;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialog=new Dialog(context);
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        data=customArrayList.get(position);

        holder.tvStatus.setText("New");
        holder.tvDsno.setText("# "+data.getUddsno());
        holder.tvStart.setVisibility(View.GONE);
        holder.tvGname.setText(data.getGuestname());
        holder.tvAccept.setVisibility(View.VISIBLE);

        //holder.tvGmobile.setText("91xxxxxxxx");
        if(data.getGuestmobile().equals(""))
        {
            holder.tvGmobile.setText("-");
        }
        else {
            holder.tvGmobile.setText(data.getGuestmobile());
            holder.tvGmobile.setText("9198xxxxxxxx");
        }


        if(data.isOfflineBookingStatus())
        {
            holder.tvAccept.setVisibility(View.GONE);
            holder.tvOfflineData.setVisibility(View.VISIBLE);
            //holder.tvMore.setVisibility(View.GONE);
            status=true;
        }

        try {
            SimpleDateFormat newformat = new SimpleDateFormat("dd-MM-yyyy");
            String datestring = data.getStartdate().split("T")[0];
            SimpleDateFormat oldformat = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = newformat.format(oldformat.parse(datestring));

            DecimalFormat formatter=new DecimalFormat("00.00");
            String aFormatter=formatter.format(data.getStarttime());

            holder.tvRepDate.setText(reformattedStr+" "+aFormatter);

        }catch(ParseException e){e.printStackTrace();}

        //holder.tvRepDate.setText(data.getStartdate());
        if(data.getPickuplocation().equals(""))
        {
            holder.tvRepPlace.setText("-");
        }
        else {

            holder.tvRepPlace.setText(data.getPickuplocation());
        }


        holder.lLayout.setTag(position);

        if(data.getAcceptancestatus()!=null) {

            if (data.getAcceptancestatus().equals("Y")) {
                if (data.getStatus().equals("P")) {
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    holder.tvStatus.setText("Ongoing");
                    holder.tvAccept.setVisibility(View.GONE);
                    holder.tvStart.setVisibility(View.VISIBLE);
                    holder.tvStart.setText("Continue Duty");
                }
            }
        }

        holder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int p=(int)holder.lLayout.getTag();
                DutyData d;
                d=customArrayList.get(p);

                Log.i("data",d.getDslipid()+":"+d.getDriverid()+":"+d.getStartdate());

                JsonObject v=new JsonObject();
                v.addProperty("dslipid",d.getDslipid());
                v.addProperty("driverid",d.getDriverid());
                v.addProperty("startdate",d.getStartdate());

                REST_CLIENT= RestClient.get();
                Call<UpdatePojo> call=REST_CLIENT.sendAcceptStatus(v);
                call.enqueue(new Callback<UpdatePojo>() {
                    @Override
                    public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {

                        UpdatePojo msg;

                        if(response.isSuccessful())
                        {
                            msg=response.body();
                            if(msg.getMessage().equals("updated"))
                            {
                                holder.tvAccept.setVisibility(View.GONE);
                                holder.tvStart.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdatePojo> call, Throwable t) {

                        Toast.makeText(context,"Connectivity Error",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        holder.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    pos= (int) holder.lLayout.getTag();
                    //Toast.makeText(context,pos+":::",Toast.LENGTH_SHORT).show();
                    mAdapterCallback.onMethodCallback(pos,customArrayList,status);
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                }

            }
        });

        /*

        holder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                final View dialogView = inflater.inflate(R.layout.alert_more_details, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                int p=(int)holder.lLayout.getTag();
                final DutyData ddata;
                ddata=customArrayList.get(p);

                TextView tvDsno=(TextView)dialogView.findViewById(R.id.amd_tv_dsno);
                //TextView tvBType=(TextView)dialogView.findViewById(R.id.amd_tv_btype);
                TextView tvRDate=(TextView)dialogView.findViewById(R.id.amd_tv_rdate);
                TextView tvRTime=(TextView)dialogView.findViewById(R.id.amd_tv_rtime);
                TextView tvRPlace=(TextView)dialogView.findViewById(R.id.amd_tv_pickup);
                // TextView tvTType=(TextView)dialogView.findViewById(R.id.amd_tv_travel);
                TextView tvGName=(TextView)dialogView.findViewById(R.id.amd_tv_gname);
                TextView tvGMobile=(TextView)dialogView.findViewById(R.id.amd_tv_gmobile);
                final Button btAccept=(Button)dialogView.findViewById(R.id.amd_bt_accept);
                final Button btStart=(Button)dialogView.findViewById(R.id.amd_bt_start);

                btStart.setVisibility(View.GONE);

                if(ddata.getAcceptancestatus().equals("Y"))
                {
                    if(ddata.getStatus().equals("P"))
                    {
                        btStart.setText("Continue Duty");
                    }
                    btAccept.setVisibility(View.GONE);
                    btStart.setVisibility(View.VISIBLE);
                }

                tvDsno.setText(ddata.getUddsno());
                //tvBType.setText(data.getBookingtype());
                //tvRDate.setText(data.getStartdate());

                try {
                    SimpleDateFormat newformat = new SimpleDateFormat("dd-MM-yyyy");
                    String datestring =ddata.getStartdate().split("T")[0];
                    SimpleDateFormat oldformat = new SimpleDateFormat("yyyy-MM-dd");
                    String reformattedStr = newformat.format(oldformat.parse(datestring));
                    tvRDate.setText(reformattedStr);

                }catch(ParseException e){e.printStackTrace();}

                tvRTime.setText(String.valueOf(ddata.getStarttime()));
                tvRPlace.setText(ddata.getPickuplocation());
                // tvTType.setText(data.getTraveltype());
                tvGName.setText(ddata.getGuestname());
                tvGMobile.setText(ddata.getGuestmobile());


                btAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        JsonObject v=new JsonObject();
                        v.addProperty("dslipid",ddata.getDslipid());
                        v.addProperty("driverid",ddata.getDriverid());
                        v.addProperty("startdate",ddata.getStartdate());

                        REST_CLIENT= RestClient.get();
                        Call<UpdatePojo> call=REST_CLIENT.sendAcceptStatus(v);
                        call.enqueue(new Callback<UpdatePojo>() {
                            @Override
                            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {

                                UpdatePojo msg;

                                if(response.isSuccessful())
                                {
                                    msg=response.body();
                                    if(msg.getMessage().equals("updated"))
                                    {
                                        btAccept.setVisibility(View.GONE);
                                        btStart.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                                Toast.makeText(context,"Connectivity Error",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

                btStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                        try {
                            pos= (int) holder.lLayout.getTag();
                            Toast.makeText(context,pos+":::",Toast.LENGTH_SHORT).show();
                            mAdapterCallback.onMethodCallback(pos,customArrayList,status);
                        }
                        catch (ClassCastException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        */

        holder.tvOfflineData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    pos= (int) holder.lLayout.getTag();
                    Toast.makeText(context,pos+":::",Toast.LENGTH_SHORT).show();
                    mAdapterCallback.onMethodCallback(pos,customArrayList,status);
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvStatus,tvDsno,tvRepDate,tvRepTime,tvRepPlace,tvMore,tvOfflineData,tvGname,tvGmobile,tvAccept,tvStart;
        LinearLayout lLayout;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tvStatus=(TextView)itemView.findViewById(R.id.rw_tv_duty_status);
            tvDsno=(TextView)itemView.findViewById(R.id.rw_tv_dsno);
            tvRepDate=(TextView)itemView.findViewById(R.id.rw_tv_rep_date);
            tvRepTime=(TextView)itemView.findViewById(R.id.rw_tv_rep_time);
            tvRepPlace=(TextView)itemView.findViewById(R.id.rw_tv_rep_place);
            tvMore=(TextView)itemView.findViewById(R.id.rw_tv_more);
            lLayout=(LinearLayout)itemView.findViewById(R.id.rw_ll_layout);
            tvOfflineData=(TextView)itemView.findViewById(R.id.rw_offline_data);
            tvGname=(TextView)itemView.findViewById(R.id.rw_tv_gname);
            tvGmobile=(TextView)itemView.findViewById(R.id.rw_tv_gmobile);
            tvAccept=(TextView)itemView.findViewById(R.id.rw_tv_accept);
            tvStart=(TextView)itemView.findViewById(R.id.rw_tv_start);

          /*  lLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        pos= (int) view.getTag();
                        mAdapterCallback.onMethodCallback(pos,mResultList);

                    }
                    catch (ClassCastException e)
                    {
                        e.printStackTrace();
                    }
                }
            });*/
        }
    }

    public static interface AdapterCallback {
        void onMethodCallback(int position,ArrayList<DutyData> data,boolean status);
    }

}
