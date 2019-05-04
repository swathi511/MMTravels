package com.hjsoft.mmtravels.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.activity.HomeActivity;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.model.Distance;
import com.hjsoft.mmtravels.model.DistancePojo;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.JourneyDetails;
import com.hjsoft.mmtravels.model.Leg;
import com.hjsoft.mmtravels.model.Route;
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
 * Created by hjsoft on 24/5/17.
 */
public class OfflineFragment extends Fragment {

    View v;
    TextView tvDsno,tvStart,tvEnd,tvKms,tvMovTime,tvIdleTime;
    Button btSendData;
    DBAdapter dbAdapter;
    ArrayList<JourneyDetails> jDataList;
    JourneyDetails jData;
    String pickupLat,pickupLong,dropLat,dropLong,stWaypoints;
    API REST_CLIENT;
    float distance=0,finalDistance;
    String signature;
    ImageView ivImg1, ivImg2, ivImg3, ivImg4;
    Button btUpload, btRetry, btSendImages, btHome;
    int count = 1;
    public static ArrayList<String> filePaths = new ArrayList<String>();
    ArrayList<DutyData> myList;
    DutyData data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_offline_data, container,false);
        tvDsno=(TextView) v.findViewById(R.id.fod_tv_dsno);
        tvStart=(TextView)v.findViewById(R.id.fod_tv_tstart);
        tvEnd=(TextView)v.findViewById(R.id.fod_tv_tend);
        tvKms=(TextView)v.findViewById(R.id.fod_tv_kms);
        tvMovTime=(TextView)v.findViewById(R.id.fod_tv_movetime);
        tvIdleTime=(TextView)v.findViewById(R.id.fod_tv_idletime);
        btSendData=(Button)v.findViewById(R.id.fod_bt_sendData);
        ivImg1=(ImageView)v.findViewById(R.id.fod_iv1);
        ivImg2=(ImageView)v.findViewById(R.id.fod_iv2);
        ivImg3=(ImageView)v.findViewById(R.id.fod_iv3);
        ivImg4=(ImageView)v.findViewById(R.id.fod_iv4);
        btUpload=(Button)v.findViewById(R.id.fod_bt_upload);
        btRetry=(Button)v.findViewById(R.id.fod_bt_retry);
        btSendImages=(Button)v.findViewById(R.id.fod_bt_send);
        btHome=(Button)v.findViewById(R.id.fod_bt_home);

        REST_CLIENT= RestClient.get();

        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        Bundle d=getActivity().getIntent().getExtras();
        final int position = d.getInt("position");

        myList = (ArrayList<DutyData>) getActivity().getIntent().getSerializableExtra("list");
        data=myList.get(position);

        jDataList=dbAdapter.getAllDutyValues(data.getDslipid());
        jData=jDataList.get(0);

        tvDsno.setText(data.getUddsno());
        tvStart.setText(jData.getStartingTime());
        tvEnd.setText(jData.getEndingTime());

        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date1 = (Date) format.parse(jData.getIdleTime());
            Date date2 = (Date) format.parse(jData.getTotHrs());
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
            tvMovTime.setText(mov_time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tvIdleTime.setText(jData.getIdleTime());

        pickupLat=jData.getPickupLat();
        pickupLong=jData.getPickupLng();
        dropLat=jData.getDropLat();
        dropLong=jData.getDropLng();
        stWaypoints=jData.getWaypoints();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + pickupLat + "," + pickupLong + "&destination=" + dropLat + "," + dropLong + "&waypoints=" + stWaypoints + "&key=AIzaSyA5f0AhduhPgOWHWC9tGgzE_teAC_it5d4";

        // System.out.println(urlString);

        Call<DistancePojo> call = REST_CLIENT.getDistanceDetails(urlString);
        call.enqueue(new Callback<DistancePojo>() {
            @Override
            public void onResponse(Call<DistancePojo> call, Response<DistancePojo> response) {

                DistancePojo data;
                Route rData;
                Leg lData;

                if (response.isSuccessful()) {
                    progressDialog.dismiss();
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

                                // System.out.println("dist and value is... " + d.getValue() + ":::" + distance);
                            }

                        }

                        distance = distance / 1000;
                        finalDistance = distance;
                        tvKms.setText(String.valueOf(finalDistance));

                        //sendDataToServer();

                        /////////////////////


                        ///////////////////
                    } else {

                        //  Toast.makeText(MapsActivity.this,data.getStatus(),Toast.LENGTH_LONG).show();
                    }
                } else {

                    progressDialog.dismiss();
                    // System.out.println(response.message() + "::" + response.code() + "::" + response.isSuccessful());
                }

            }

            @Override
            public void onFailure(Call<DistancePojo> call, Throwable t) {

                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Connectivity Error..Please try again!",Toast.LENGTH_LONG).show();

            }
        });

        btSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendDataToServer();
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

        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });



        return  v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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


            Call<List<String>> upload=REST_CLIENT.sendImages(jData.getDsno(),jData.getDriverId(),jData.getStartDate(),finalRequestBody);
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

    public void sendDataToServer()
    {
        String val=String.valueOf(finalDistance);
        signature=dbAdapter.getTime(jData.getDsno(),"sign");

        final JsonObject v=new JsonObject();
        v.addProperty("dslipid",jData.getDsno());
        v.addProperty("driverid",jData.getDriverId());
        v.addProperty("startdate",jData.getStartDate());
        v.addProperty("totkms",val);
        v.addProperty("jdetails",jData.getjDetails());
        v.addProperty("sjdetails",jData.getsJdetails());
        v.addProperty("cjdetails",jData.getcJdetails());
        v.addProperty("tothrs",jData.getTotHrs());
        v.addProperty("signature",signature);
        v.addProperty("idletime",jData.getPause());
        v.addProperty("status","Y");

        Call<UpdatePojo> call=REST_CLIENT.sendJourneyDetails(v);
        call.enqueue(new Callback<UpdatePojo>() {
            @Override
            public void onResponse(Call<UpdatePojo> call, Response<UpdatePojo> response) {

                UpdatePojo status;

                if(response.isSuccessful())
                {
                    status=response.body();
                    btSendData.setVisibility(View.GONE);

                    if(status.getMessage().equals("updated"))
                    {
                        Toast.makeText(getActivity(),"Successfully Sent Data to Server",Toast.LENGTH_LONG).show();

                        dbAdapter.deleteAll(jData.getDsno());
                        dbAdapter.deleteDutyUpdates(jData.getDsno());
                        dbAdapter.deleteTimeForDsno(jData.getDsno());
                        dbAdapter.deleteStatusForDsno(jData.getDsno());
                        dbAdapter.deleteAllDutyValues(jData.getDsno());

                        btUpload.setVisibility(View.VISIBLE);
                        btHome.setVisibility(View.VISIBLE);

                        //String isThere=dbAdapter.getTime(stDsno,"sign");
                        /// System.out.println("((((((((((((((**%%%%***((((((((((((((((("+isThere);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Error Sending Data to Server"+String.valueOf(response),Toast.LENGTH_LONG).show();
                        //dbAdapter.insertDutyEntry(data.getDslipid(),data.getDriverid(),data.getStartdate(),
                        // val,stAfterPickUp,stBeforePickUp,stAfterDrop,finalTimeTravelled,signature,pause,data.getGuestname(),data.getGuestmobile());
                    }
                }

                else {

                    // System.out.println(response.errorBody()+":"+response.message()+":"+response.code());
                }


            }

            @Override
            public void onFailure(Call<UpdatePojo> call, Throwable t) {

                Toast.makeText(getActivity(),"No Network Connection! Please Retry!",Toast.LENGTH_LONG).show();
                btSendData.setVisibility(View.VISIBLE);


            }
        });
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
}
