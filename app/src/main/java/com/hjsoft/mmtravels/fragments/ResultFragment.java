package com.hjsoft.mmtravels.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.GPSTracker;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.SessionManager;
import com.hjsoft.mmtravels.activity.HomeActivity;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.Pojo;
import com.hjsoft.mmtravels.model.UpdatePojo;
import com.hjsoft.mmtravels.webservices.API;
import com.hjsoft.mmtravels.webservices.RestClient;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 28/2/17.
 */
public class ResultFragment extends Fragment {


    View rootView;
    TextView tvDsno, tvBtype, tvRdate, tvRtime, tvPickup, tvTravel, tvGname, tvGmobile, tvDist, tvTstart, tvTend, tvTmove, tvTidle;
    Button btUpload, btRetry, btSendImages, btHome;
    ImageView ivImg1, ivImg2, ivImg3, ivImg4;
    int count = 1;
    public static ArrayList<String> filePaths = new ArrayList<String>();
    Toolbar tbTitle;
    ArrayList<DutyData> myList;
    DutyData data;
    String date;
    String finalTimeTravelled;
    API REST_CLIENT;
    DBAdapter dbAdapter;
    boolean dataSent=false;
    String signature;
    long diff;
    String tot_time,totHrs="00.00";
    SessionManager session;
    String stDsno;
    int position;
    float startToReportKms,finishToGarageKms,startToReportHrs,finishToGarageHrs;
    GPSTracker gps;
    Handler h1;
    Runnable r1;
    double current_lat,current_long;
    SharedPreferences pref;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        REST_CLIENT= RestClient.get();
        gps=new GPSTracker(getActivity());

        sendLocationUpdates();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.fragment_result, container,false);

        myList = (ArrayList<DutyData>) getActivity().getIntent().getSerializableExtra("mylist");


        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        session=new SessionManager(getContext());

        tvDsno = (TextView) rootView.findViewById(R.id.fr_tv_dsno);
        tvBtype = (TextView) rootView.findViewById(R.id.fr_tv_btype);
        tvRdate = (TextView) rootView.findViewById(R.id.fr_tv_rdate);
        tvRtime = (TextView) rootView.findViewById(R.id.fr_tv_rtime);
        tvGname = (TextView) rootView.findViewById(R.id.fr_tv_gname);
        tvGmobile = (TextView) rootView.findViewById(R.id.fr_tv_gmobile);
        tvDist = (TextView) rootView.findViewById(R.id.fr_tv_dist);
        tvTstart = (TextView) rootView.findViewById(R.id.fr_tv_tstart);
        tvTend = (TextView) rootView.findViewById(R.id.fr_tv_tend);
        tvTmove = (TextView) rootView.findViewById(R.id.fr_tv_tmove);
        tvTidle = (TextView) rootView.findViewById(R.id.fr_tv_tidle);

        btUpload = (Button) rootView.findViewById(R.id.fr_bt_upload);
        btRetry = (Button) rootView.findViewById(R.id.fr_bt_retry);
        btSendImages = (Button) rootView.findViewById(R.id.fr_bt_send);
        btHome = (Button) rootView.findViewById(R.id.fr_bt_home);

        ivImg1 = (ImageView) rootView.findViewById(R.id.fr_iv1);
        ivImg2 = (ImageView) rootView.findViewById(R.id.fr_iv2);
        ivImg3 = (ImageView) rootView.findViewById(R.id.fr_iv3);
        ivImg4 = (ImageView) rootView.findViewById(R.id.fr_iv4);

        btRetry.setVisibility(View.GONE);
        btSendImages.setVisibility(View.INVISIBLE);
        btUpload.setVisibility(View.GONE);
        btHome.setVisibility(View.GONE);

        Bundle b=getActivity().getIntent().getExtras();
        float res=b.getFloat("Result");
        String starting_time=b.getString("starting_time");
        String ending_time=b.getString("ending_time");
        String idle_time=b.getString("idle_time");
        String jDetails=b.getString("jDetails");
        final String stBeforePickUp=b.getString("stBeforePickUp");
        final String stAfterPickUp=b.getString("stAfterPickUp");
        final String stAfterDrop=b.getString("stAfterDrop");
        signature=b.getString("sign");
        final String pause=b.getString("pause");
        position=b.getInt("position");
        startToReportKms=b.getFloat("startToReportKms");
        startToReportHrs=b.getFloat("startToReportHrs");
        finishToGarageKms=b.getFloat("finishToGarageKms");
        finishToGarageHrs=b.getFloat("finishToGarageHrs");

       //__
        // Toast.makeText(getActivity(),position,Toast.LENGTH_SHORT).show();
        // System.out.println("Position is "+position);

        System.out.println("Result isss "+res);

        data=myList.get(position);

        stDsno=data.getDslipid();
        tvDsno.setText(data.getUddsno());
        tvBtype.setText(data.getBookingtype());
        //tvRdate.setText(data.getStartdate());
        tvRtime.setText(String.valueOf(data.getStarttime()));
        tvGname.setText(data.getGuestname());
        tvGmobile.setText(data.getGuestmobile());
        //tvGmobile.setText("9198xxxxxxxx");

        String s=data.getStartdate();
        try {
            SimpleDateFormat newformat = new SimpleDateFormat("dd-MM-yyyy");
            String datestring = s.split("T")[0];
            SimpleDateFormat oldformat = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = newformat.format(oldformat.parse(datestring));
            tvRdate.setText(reformattedStr);

        }catch(ParseException e){e.printStackTrace();}



        if(signature==null)
        {
            signature=dbAdapter.getTime(stDsno,"sign");
            // System.out.println("signature is "+signature);
        }

         /*System.out.println("Res"+res);
         System.out.println("stime & endtime"+starting_time+" "+ending_time);
         System.out.println("idle_time"+idle_time);
         System.out.println("jdet"+jDetails);
         System.out.println("pause"+pause);
         System.out.println("stBef"+stBeforePickUp);
         System.out.println("stAft"+stAfterPickUp);
         System.out.println("stAfterDrop"+stAfterDrop);
         System.out.println("sign"+signature);*/


        //SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date1 = timeFormat.parse(starting_time);
            Date date2 = timeFormat.parse(ending_time);
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
            totHrs=hFormatted+"."+mFormatted;

            // System.out.println("Total time travelled is "+finalTimeTravelled);

        }catch (ParseException e) {e.printStackTrace();}

        System.out.println("idle time is "+idle_time);
        System.out.println("total time is "+tot_time);

        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date1 = (Date) format.parse(idle_time);
            Date date2 = (Date) format.parse(tot_time);
            //time difference in milliseconds
            long timeDiff = date2.getTime() - date1.getTime();
            //new date object with time difference
            int Hours = (int) (timeDiff/(1000 * 60 * 60));
            int Mins = (int) (timeDiff/(1000*60)) % 60;
            long Secs = (int) (timeDiff/ 1000) % 60;

            DecimalFormat formatter = new DecimalFormat("00");
            String hFormatted = formatter.format(Hours);
            String mFormatted = formatter.format(Mins);
            String sFormatted = formatter.format(Secs);
            String mov_time=hFormatted+":"+mFormatted+":"+sFormatted;
            tvTmove.setText(mov_time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tvDist.setText(String.valueOf(res)+" km");
        tvTstart.setText(starting_time);
        tvTend.setText(ending_time);
        // tvTmove.setText(date);
        tvTidle.setText(idle_time);

        data=myList.get(position);

        final String val=String.valueOf(res);

        //System.out.println("signature is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        //System.out.println(signature);

        editor.putFloat("distance",0);
        editor.commit();


        final JsonObject v=new JsonObject();
        v.addProperty("dslipid",data.getDslipid());
        v.addProperty("driverid",data.getDriverid());
        v.addProperty("startdate",data.getStartdate());
        v.addProperty("totkms",val);
        v.addProperty("jdetails",stAfterPickUp);
        v.addProperty("sjdetails",stBeforePickUp);
        v.addProperty("cjdetails",stAfterDrop+"URL");
        v.addProperty("tothrs",totHrs);
        v.addProperty("signature",signature);
        v.addProperty("idletime",pause+"$"+jDetails+"$");
        v.addProperty("status","Y");
        v.addProperty("startToReportKms",startToReportKms);
        v.addProperty("startToReportHrs",startToReportHrs);
        v.addProperty("finishToGarageKms",finishToGarageKms);
        v.addProperty("finishToGarageHrs",finishToGarageHrs);

        /*System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(data.getDslipid());
        System.out.println(data.getDriverid());
        System.out.println(data.getStartdate());
        System.out.println(val);
        System.out.println("stAFterpickup"+stAfterPickUp);
        System.out.println("stBefPickup"+stBeforePickUp);
        System.out.println("stAFterdrop"+stAfterDrop);
        System.out.println(tot_time);
        System.out.println(pause+"$"+jDetails+"$");
        System.out.println("ALL THE URLS..");
        //System.out.println(dbAdapter.getLocUrl(stDsno));
        //System.out.println(signature);

        System.out.println(startToReportKms+":"+startToReportHrs+":"+finishToGarageKms+":"+finishToGarageHrs);

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");*/

        //copy this line inside API success

        Call<UpdatePojo> call=REST_CLIENT.sendJourneyDetails(v);
        call.enqueue(new Callback<UpdatePojo>() {
            @Override
            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {

                UpdatePojo status;

                if(response.isSuccessful())
                {
                    status=response.body();

                    if(status.getMessage().equals("updated"))
                    {
                        Toast.makeText(getActivity(),"Successfully Sent Data to Server",Toast.LENGTH_LONG).show();
                        btUpload.setVisibility(View.VISIBLE);
                        btHome.setVisibility(View.VISIBLE);
                        dbAdapter.deleteAll(data.getDslipid());
                        dbAdapter.deleteDutyUpdates(data.getDslipid());
                        dbAdapter.deleteTimeForDsno(data.getDslipid());
                        dbAdapter.deleteStatusForDsno(data.getDslipid());
                        //dbAdapter.deleteLocUrl(stDsno);

                        //String isThere=dbAdapter.getTime(stDsno,"sign");
                       /// System.out.println("((((((((((((((**%%%%***((((((((((((((((("+isThere);

                        dataSent=true;
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Error Sending Data to Server"+String.valueOf(response),Toast.LENGTH_LONG).show();
                        btRetry.setVisibility(View.VISIBLE);
                        //dbAdapter.insertDutyEntry(data.getDslipid(),data.getDriverid(),data.getStartdate(),
                        // val,stAfterPickUp,stBeforePickUp,stAfterDrop,finalTimeTravelled,signature,pause,data.getGuestname(),data.getGuestmobile());
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                Toast.makeText(getActivity(),"No Network Connection! Please Retry!",Toast.LENGTH_LONG).show();

                btRetry.setVisibility(View.VISIBLE);
               // dbAdapter.insertDutyEntry(data.getDslipid(),data.getDriverid(),data.getStartdate(),
                       // val,stAfterPickUp,stBeforePickUp,stAfterDrop,finalTimeTravelled,signature,pause,data.getGuestname(),data.getGuestmobile(),String.valueOf(data.getStarttime()),data.getBookingtype());
            }
        });



        btRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<UpdatePojo> call=REST_CLIENT.sendJourneyDetails(v);
                call.enqueue(new Callback<UpdatePojo>() {
                    @Override
                    public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {
                        UpdatePojo status;

                        if(response.isSuccessful())
                        {
                            status=response.body();

                            if(status.getMessage().equals("updated"))
                            {
                                Toast.makeText(getActivity(),"Successfully Sent Data to Server",Toast.LENGTH_LONG).show();
                                btUpload.setVisibility(View.VISIBLE);
                                btHome.setVisibility(View.VISIBLE);
                                btRetry.setVisibility(View.GONE);
                                dbAdapter.deleteAll(data.getDslipid());
                                dbAdapter.deleteDutyUpdates(data.getDslipid());
                                dbAdapter.deleteTimeForDsno(data.getDslipid());
                                dbAdapter.deleteStatusForDsno(data.getDslipid());
                                //dbAdapter.deleteAllDutyValues(data.getDslipid());
                                dataSent=true;
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Error Sending Data to Server"+String.valueOf(response),Toast.LENGTH_LONG).show();
                                // btRetry.setVisibility(View.VISIBLE);
                                //dbAdapter.insertDutyEntry(data.getDslipid(),data.getDriverid(),data.getStartdate(),
                                // val,stAfterPickUp,stBeforePickUp,stAfterDrop,finalTimeTravelled,signature,pause,data.getGuestname(),data.getGuestmobile());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdatePojo> call, Throwable t) {

                        Toast.makeText(getActivity(),"No Network Connection! Please Retry!",Toast.LENGTH_LONG).show();
                        //btRetry.setVisibility(View.VISIBLE);
                        //dbAdapter.insertDutyEntry(data.getDslipid(),data.getDriverid(),data.getStartdate(),
                        //val,stAfterPickUp,stBeforePickUp,stAfterDrop,finalTimeTravelled,signature,pause,data.getGuestname(),data.getGuestmobile());
                    }
                });
            }
        });


        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < 23) {
                    //  System.out.println("Sdk_int is"+Build.VERSION.SDK_INT);
                    getData();
                } else {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        getData();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(getActivity(), "This permission is required for the images to be uploaded!", Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                    }
                }
            }
        });

        btSendImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendtoServer();
            }
        });

        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
               getActivity().finish();
            }
        });

        ivImg1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setMessage("Delete image?");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ivImg1.setImageDrawable(null);

                        int k=(Integer) ivImg1.getTag();
                        for(int l=0; l<filePaths.size(); l++)
                        {
                            if(l==k)
                            {
                                filePaths.remove(l);
                            }
                        }
                        count--;

                        if(filePaths.size()<5)
                        {
                            btUpload.setVisibility(View.VISIBLE);
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;

            }
        });

        ivImg2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setMessage("Delete image?");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ivImg2.setImageDrawable(null);

                        int k=(Integer) ivImg2.getTag();
                        for(int l=0; l<filePaths.size(); l++)
                        {
                            if(l==k)
                            {
                                filePaths.remove(l);
                            }
                        }
                        count--;

                        if(filePaths.size()<5)
                        {
                            btUpload.setVisibility(View.VISIBLE);
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;
            }
        });

        ivImg3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setMessage("Delete image?");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ivImg3.setImageDrawable(null);

                        int k=(Integer) ivImg3.getTag();
                        for(int l=0; l<filePaths.size(); l++)
                        {
                            if(l==k)
                            {
                                filePaths.remove(l);
                            }
                        }
                        count--;

                        if(filePaths.size()<5)
                        {
                            btUpload.setVisibility(View.VISIBLE);
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;
            }
        });

        ivImg4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setMessage("Delete image?");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ivImg4.setImageDrawable(null);

                        int k=(Integer) ivImg4.getTag();
                        for(int l=0; l<filePaths.size(); l++)
                        {
                            if(l==k)
                            {
                                filePaths.remove(l);
                            }
                        }
                        count--;

                        if(filePaths.size()<5)
                        {
                            btUpload.setVisibility(View.VISIBLE);
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;
            }
        });

        return rootView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==9) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getData();
            } else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getData() {
        if (count < 5) {

            if (btSendImages.isShown()) {
            } else {
                btSendImages.setVisibility(View.VISIBLE);
            }

            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            startActivityForResult(galleryIntent, 0);
            count++;
        } else {
            Toast.makeText(getActivity(), "Cannot Upload further..!", Toast.LENGTH_LONG).show();
            btUpload.setVisibility(View.GONE);
            //ivPaperClip.setEnabled(false);
        }
    }

    public void sendtoServer() {
        Toast.makeText(getActivity(), "Uploading images!!!", Toast.LENGTH_LONG).show();
        MediaType MEDIA_TYPE_IMG = MediaType.parse("image/jpeg");
        MultipartBody.Builder builder=new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        RequestBody requestBody;
        try {

            for (int i = 0; i < filePaths.size(); i++) {

                File file = new File(filePaths.get(i));
                File compressedImageFile= Compressor.getDefault(getActivity()).compressToFile(file);
                //requestBody=RequestBody.create(MEDIA_TYPE_IMG,file);
                requestBody=RequestBody.create(MEDIA_TYPE_IMG,compressedImageFile);
                // builder.addFormDataPart("photo"+i,file.getName(),requestBody);
                builder.addFormDataPart("photo"+i,compressedImageFile.getName(),requestBody);
            }
            RequestBody finalRequestBody=builder.build();
            REST_CLIENT= RestClient.get();

            // System.out.println(data.getDslipid()+"*"+data.getDriverid()+"*"+data.getStartdate());

            Call<List<String>> upload=REST_CLIENT.sendImages(data.getDslipid(),data.getDriverid(),data.getStartdate(),finalRequestBody);
            upload.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                    //System.out.println("response is "+response.isSuccessful()+response.message()+response.code());

                    if(response.isSuccessful())
                    {
                        btSendImages.setVisibility(View.GONE);
                        btUpload.setVisibility(View.GONE);

                        ivImg1.setEnabled(false);
                        ivImg1.setLongClickable(false);
                        ivImg2.setEnabled(false);
                        ivImg2.setLongClickable(false);
                        ivImg3.setEnabled(false);
                        ivImg3.setLongClickable(false);
                        ivImg4.setEnabled(false);
                        ivImg4.setLongClickable(false);

                        Toast.makeText(getActivity(),"File(s) Uploaded !",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        //System.out.println("error sending....");
                        // System.out.println(response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {

                    t.printStackTrace();
                    t.getCause();
                    //btOk.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(),"No Network Connection! Please Retry!",Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == getActivity().RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String mediaPath = cursor.getString(columnIndex);

            //System.out.println("mediapath is"+mediaPath);
            // Set the Image in ImageView for Previewing the Media

            if (ivImg1.getDrawable() == null) {

                String extension = mediaPath.substring(mediaPath.lastIndexOf("."));

                if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")) {
                    filePaths.add(mediaPath);
                    ivImg1.setImageBitmap(decodeSampledBitmapFromResource(mediaPath, 100, 100));
                    ivImg1.setTag(0);
                } else {
                    Toast.makeText(getActivity(), "Cannot Upload file other than .png or .jpg", Toast.LENGTH_LONG).show();
                }
            } else if (ivImg2.getDrawable() == null) {
                //  ivImage2.setImageBitmap(BitmapFactory.decodeFile(mediaPath));

                String extension = mediaPath.substring(mediaPath.lastIndexOf("."));

                if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")) {
                    //add fil to ArayList
                    filePaths.add(mediaPath);
                    ivImg2.setImageBitmap(decodeSampledBitmapFromResource(mediaPath, 100, 100));
                    ivImg2.setTag(1);
                } else {
                    Toast.makeText(getActivity(), "Cannot Upload file other than .png or .jpg", Toast.LENGTH_LONG).show();
                }
            } else if (ivImg3.getDrawable() == null) {
                //ivImage3.setImageBitmap(BitmapFactory.decodeFile(mediaPath));

                String extension = mediaPath.substring(mediaPath.lastIndexOf("."));

                if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")) {
                    //add fil to ArayList
                    filePaths.add(mediaPath);
                    ivImg3.setImageBitmap(decodeSampledBitmapFromResource(mediaPath, 100, 100));
                    ivImg3.setTag(2);
                } else {
                    Toast.makeText(getActivity(), "Cannot Upload file other than .png or .jpg", Toast.LENGTH_LONG).show();
                }
            } else if (ivImg4.getDrawable() == null) {
                // ivImage4.setImageBitmap(BitmapFactory.decodeFile(mediaPath));

                String extension = mediaPath.substring(mediaPath.lastIndexOf("."));

                if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")) {
                    filePaths.add(mediaPath);
                    ivImg4.setImageBitmap(decodeSampledBitmapFromResource(mediaPath, 100, 100));
                    ivImg4.setTag(3);
                } else {
                    Toast.makeText(getActivity(), "Cannot Upload file other than .png or .jpg", Toast.LENGTH_LONG).show();
                }
            }
            cursor.close();
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(String file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap b1 = BitmapFactory.decodeFile(file, options);
        // System.out.println("size of bitmap b1 is " + b1.getByteCount());
        Bitmap b2 = Bitmap.createScaledBitmap(b1, 100, 100, true);

        return b1;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public void sendLocationUpdates()
    {
        h1 = new Handler();
        r1 = new Runnable() {
            @Override
            public void run() {

                h1.postDelayed(this, 300000);//5 min

                current_lat=gps.getLatitude();
                current_long=gps.getLongitude();

                System.out.println("Coordinates( "+current_lat+":::"+current_long+" )");
                System.out.println("profileId in Showduty"+pref.getString("profileId", ""));

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        gps.stopUsingGPS();
        if(h1!=null) {
            h1.removeCallbacks(r1);
        }
    }
}
