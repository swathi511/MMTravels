package com.hjsoft.mmtravels;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * Created by hjsoft on 8/11/17.
 */
public class GPSTracker extends Service implements LocationListener {


    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude,lastKnownLat; // latitude
    double longitude,lastKnownLong; // longitude
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";

    Handler h1;
    Runnable r1;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    //private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters


    // The minimum time between updates in milliseconds
    //private static final long MIN_TIME_BW_UPDATES = 1000 * 10 ; // 10 sec
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 ; // 1 sec
    // 1000 * 2

    static Double lat1 = null;
    static Double lon1 = null;
    static Double lat2 = null;
    static Double lon2 = null;
    static Double distance = 0.0;
    static int status = 0;

    boolean first=true;
    boolean flag;

    // Declaring a Location Manager
    protected LocationManager locationManager;
    float accuracy=10;
    long getTime;




    public GPSTracker(Context context) {
        this.mContext = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        flag=pref.getBoolean("flag",true);
        getLocation();
        //System.out.println("Constructor is called");
    }

    public Location getLocation() {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null ;
        }

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                /*if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("Network", "Network"+location.getLatitude()+":"+location.getLongitude());
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }*/
                // if GPS Enabled get lat/long using GPS Services

                System.out.println("Checking if GPS is enabled!"+isGPSEnabled);
                if (isGPSEnabled) {
                    //if (location == null) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.d("GPS Enabled", "GPS Enabled"+location.getLatitude()+":"+location.getLongitude());
//                            lastKnownLat = location.getLatitude();
//                            lastKnownLong = location.getLongitude();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if(location.hasAccuracy()) {
                                accuracy = location.getAccuracy();
                            }
                            else {
                                accuracy=10;
                            }
                            getTime=location.getTime();

                            System.out.println("Details in GPSTracker class for GPS Provider");
                            System.out.println(latitude+"::"+longitude+"::"+accuracy+"::"+getTime);
                        }
                    }
                    // }
                }


                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("Network", "Network"+location.getLatitude()+":"+location.getLongitude());
                            /*lastKnownLat = location.getLatitude();
                            lastKnownLong = location.getLongitude()*/;
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if(location.hasAccuracy()) {
                                accuracy = location.getAccuracy();
                            }
                            else {
                                accuracy=10;
                            }
                            getTime=location.getTime();

                            System.out.println("Details in GPSTracker class for Network Provider");
                            System.out.println(latitude+"::"+longitude+"::"+accuracy+"::"+getTime);

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        //System.out.println("GPS COORDINATES STOPPED");
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(GPSTracker.this);
            }
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }

        //h1.removeCallbacks(r1);
    }

    public double getLastKnownLatitude(){
      /*  if(location != null){
            latitude = location.getLatitude();
        }*/
        //System.out.println("Knownlottitudeis"+lastKnownLat);
        // return latitude;

        return lastKnownLat;
    }

    /**
     * Function to get longitude
     * */
    public double getLastKnownLongitude(){
       /* if(location != null){
            longitude = location.getLongitude();
        }*/
        //  System.out.println("Knownlongitutudeis"+lastKnownLong);
        // return longitude;
        return lastKnownLong;
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude(){
//        if(location != null){
//            latitude = location.getLatitude();
//        }
        //System.out.println("GPSSSSSSSS Knownlottitude is"+latitude);
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
//        if(location != null){
//            longitude = location.getLongitude();
//        }
        //System.out.println("GPSSSSSSSSKnownlongitudetude is"+longitude);
        // return longitude
        return longitude;
    }

    public float getAccuracy(){
//        if(location != null){
//            longitude = location.getLongitude();
//        }
        //System.out.println("GPSSSSSSSSKnownlongitudetude is"+longitude);
        // return longitude
        return accuracy;
    }

    public long getTime(){
//        if(location != null){
//            longitude = location.getLongitude();
//        }
        //System.out.println("GPSSSSSSSSKnownlongitudetude is"+longitude);
        // return longitude
        return getTime;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //System.out.println("location changed ...."+location.getLatitude()+":"+location.getLongitude()+"****"+location.getProvider());
        //System.out.println("accuracy issssss "+location.getAccuracy());


        if(location!=null&&location.hasAccuracy()) {

            if(first) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                accuracy=location.getAccuracy();
                getTime=location.getTime();
                first=false;

                //System.out.println("first "+location.getAccuracy()+":"+"lt&lng"+latitude+":"+longitude);
            }
            else {

                //if (location.getAccuracy() <= 10) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                accuracy=location.getAccuracy();
                getTime=location.getTime();
                // }
            }
        }

        /*if (status == 0) {
            lat1 = location.getLatitude();
            lon1 = location.getLongitude();
        } else if ((status % 2) != 0) {
            lat2 = location.getLatitude();
            lon2 = location.getLongitude();
            distance += distanceBetweenTwoPoint(lat1, lon1, lat2, lon2);
        } else if ((status % 2) == 0) {
            lat1 = location.getLatitude();
            lon1 = location.getLongitude();
            distance += distanceBetweenTwoPoint(lat2, lon2, lat1, lon1);
        }
        status++;
*/
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    double distanceBetweenTwoPoint(double srcLat, double srcLng, double desLat, double desLng) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(desLat - srcLat);
        double dLng = Math.toRadians(desLng - srcLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(srcLat))
                * Math.cos(Math.toRadians(desLat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double meterConversion = 1609;

        return (int) (dist * meterConversion);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("Service OnCreate is called");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("Service OnDestory is called");


        //h1.removeCallbacks(r1);
    }
}
