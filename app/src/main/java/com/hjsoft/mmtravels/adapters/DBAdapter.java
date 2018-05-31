package com.hjsoft.mmtravels.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.hjsoft.mmtravels.DatabaseHandler;
import com.hjsoft.mmtravels.model.DutyUpdates;
import com.hjsoft.mmtravels.model.JourneyDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hjsoft on 27/2/17.
 */

public class DBAdapter {

    static final String DATABASE_NAME = "user.db";
    static final int DATABASE_VERSION = 6;
    public static final int NAME_COLUMN = 1;

    public static final String TABLE_LATLNG = "create table if not exists "+"LATLNG_DETAILS"+
            "( " +"DSNO  text,LATITUDE  double,LONGITUDE double,PLACE text,CUM_DISTANCE integer,TIME_UPDATED text); ";

    public static final String TABLE_JOURNEY_DATA = "create table if not exists "+"JOURNEY_DETAILS"+
            "( " +"DSNO  text,PICKUP_LAT text,PICKUP_LNG text,DROP_LAT text,DROP_LNG text,WAYPOINTS text,DRIVER_ID text,STARTDATE text,ALL_JDETAILS text,J_DETAILS text,SJ_DETAILS text,CJ_DETAILS text,TOT_HRS text,IDLE_TIME text,PAUSE text,GUEST_NAME text,GUEST_MOBILE text,STARTING_TIME text,ENDING_TIME text); ";

    public static final String TABLE_CREATE_DUTY_UPDATES= "create table if not exists "+"DUTY_UPDATES"+
            "( " +"DSNO  text,DRIVER_ID text,RDATE text,RTIME text,TRAVEL_TYPE text,TOT_KMS integer,STARTING_TIME text,STOPPING_TIME text,GUEST_NAME text,GUEST_MOBILE text,LATITUDE double,LONGITUDE double,IDLE_TIME integer,IDLE_TIME_DIFF text); ";

    public static final String TABLE_CREATE_STATUS="create table if not exists "+"STATUS"+
            "( " +"DSNO text,STATUS text); ";

    public static final String TABLE_STORE_TIMES="create table if not exists "+"TIMES"+
            "( " +"DESC text,TIME text,DSNO text); ";

    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DatabaseHandler dbHelper;

    public  DBAdapter(Context _context)
    {
        context = _context;
        dbHelper = new DatabaseHandler(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public  DBAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    public long insertEntry(String dsno,double latitude,double longitude,String place,long cum_distance,String time_updated)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("DSNO",dsno);
        newValues.put("LATITUDE",latitude);
        newValues.put("LONGITUDE",longitude);
        newValues.put("PLACE",place);
        newValues.put("CUM_DISTANCE",cum_distance);
        newValues.put("TIME_UPDATED",time_updated);
        // Insert the row into your table
        long value=db.insert("LATLNG_DETAILS", null, newValues);
        //  close();
        return  value;
    }

    public String getTodaysValues()
    {
        String st="";
        db=dbHelper.getReadableDatabase();
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //System.out.println("Todays date is "+today);
        String sql="SELECT * FROM LATLNG_DETAILS "+"WHERE strftime('%Y%m%d',TIME_UPDATED)= '"+today+"' ";
        Cursor cursor=db.rawQuery(sql,null);
        JSONObject jobj ;
        JSONArray arr = new JSONArray();

        while(cursor.moveToNext()) {
            try {
                jobj = new JSONObject();
                jobj.put("Dsno",cursor.getString(0));
                jobj.put("Latitude", cursor.getString(1));
                jobj.put("Longitude", cursor.getString(2));
                jobj.put("Place",cursor.getString(3));
                jobj.put("Cumulative_Distance (meters)",cursor.getString(4));
                jobj.put("Time_Updated", cursor.getString(5));
                //System.out.println("lat "+cursor.getString(0)+" long "+cursor.getString(1)+" place "+cursor.getString(2)+" distance "+cursor.getString(3)+" time"+cursor.getString(4));
                arr.put(jobj);
            }
            catch (JSONException e){e.printStackTrace();}
        }
        try{
            jobj = new JSONObject();
            jobj.put("data", arr);
            st=jobj.toString();
        }catch(JSONException e){e.printStackTrace();}

        return st;
    }

    public String getIntervalData(String time1,String time2,String dsno) {
        String st = "";
        db = dbHelper.getReadableDatabase();

        String sql="SELECT * FROM LATLNG_DETAILS "+"WHERE TIME_UPDATED>= '"+time1+"' "+"AND TIME_UPDATED<= '"+time2+"' "+"AND DSNO= '"+dsno+"' ";
        Cursor cursor=db.rawQuery(sql,null);
        JSONObject jobj ;
        JSONArray arr = new JSONArray();

        String data="";
        while (cursor.moveToNext())
        {
            if(cursor.isFirst())
            {
                data=data+cursor.getString(1)+","+cursor.getString(2);
            }
            else {
                data=data+"*"+cursor.getString(1)+","+cursor.getString(2);
            }
        }

        while(cursor.moveToNext()) {
            try {
                jobj = new JSONObject();
                jobj.put("Dsno",cursor.getString(0));
                jobj.put("Latitude", cursor.getString(1));
                jobj.put("Longitude", cursor.getString(2));
                jobj.put("Place",cursor.getString(3));
                jobj.put("Cumulative_Distance (meters)",cursor.getString(4));
                jobj.put("Time_Updated", cursor.getString(5));
                //System.out.println("lat "+cursor.getString(0)+" long "+cursor.getString(1)+" place "+cursor.getString(2)+" distance "+cursor.getString(3)+" time"+cursor.getString(4));
                arr.put(jobj);
            }
            catch (JSONException e){e.printStackTrace();}
        }
        try{
            jobj = new JSONObject();
            jobj.put("data", arr);
            st=jobj.toString();
        }catch(JSONException e){e.printStackTrace();}

        return data;
    }

    public void deleteAll(String dsno)
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from LATLNG_DETAILS WHERE DSNO ="+" '"+dsno+"' ");
        // System.out.println("delete from LATLNG_DETAILS WHERE DSNO ="+" '"+dsno+"' ");
        //System.out.println("deletion done.....latlng_data");
    }

    public long insertDutyEntry(String dsNo,String pickupLat,String pickupLng,String dropLat,String dropLng,String driverId,String startDate,String waypoints,String alljDetails,String jDetails,String sjDetails,String cjDetails,String totHrs,String pause,String idleTime,String guestName,String guestMobile,String startingTime,String endingTime)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("DSNO",dsNo);
        newValues.put("PICKUP_LAT",pickupLat);
        newValues.put("PICKUP_LNG",pickupLng);
        newValues.put("DROP_LAT",dropLat);
        newValues.put("DROP_LNG",dropLng);
        newValues.put("DRIVER_ID",driverId);
        newValues.put("STARTDATE",startDate);
        newValues.put("WAYPOINTS",waypoints);
        newValues.put("ALL_JDETAILS",alljDetails);
        newValues.put("J_DETAILS",jDetails);
        newValues.put("SJ_DETAILS",sjDetails);
        newValues.put("CJ_DETAILS",cjDetails);
        newValues.put("TOT_HRS",totHrs);
        newValues.put("IDLE_TIME",idleTime);
        newValues.put("PAUSE",pause);
        newValues.put("GUEST_NAME",guestName);
        newValues.put("GUEST_MOBILE",guestMobile);
        newValues.put("STARTING_TIME",startingTime);
        newValues.put("ENDING_TIME",endingTime);
        // Insert the row into your table
        long value=db.insert("JOURNEY_DETAILS", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
        //  close();
        //            "( " +"LOGNO  text,DRIVER_ID text,JDATE text,TOT_KMS text,J_DETAILS text,SJ_DETAILS text,CJ_DETAILS text,TOT_HRS text,SIGNATURE text); ";

        return  value;
    }

    public ArrayList<JourneyDetails> getAllDutyValues(String dsno){

        db=dbHelper.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * FROM JOURNEY_DETAILS WHERE DSNO="+" '"+dsno+"' ",null);
        ArrayList<JourneyDetails> dataList=new ArrayList<JourneyDetails>();
        JourneyDetails data;

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();
                data=new JourneyDetails(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getString(8),c.getString(9),c.getString(10),c.getString(11),c.getString(12),c.getString(13),c.getString(14),c.getString(15),c.getString(16),c.getString(17),c.getString(18));
                dataList.add(data);
            }
        }
        c.close();
        // close();
        return dataList;

    }

    public boolean isDSNOpresent(String dsno)
    {
        boolean there=false;
        db=dbHelper.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * FROM JOURNEY_DETAILS WHERE DSNO="+" '"+dsno+"' ",null);
        ArrayList<JourneyDetails> dataList=new ArrayList<JourneyDetails>();
        JourneyDetails data;

        if(c.getCount()>0)
        {
           there=true;
        }
        c.close();
        // close();
        return there;
    }

    public void deleteAllDutyValues(String dsno)
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from JOURNEY_DETAILS WHERE DSNO ="+" '"+dsno+"' ");
        //System.out.println("deletion done.....duty_details");
    }


    public long insertDutyUpdates(String dsNo,String travelType,String driverId,String rDate,String rTime,long totKms,String startingTime,String stoppingTime,String guestName,String guestMobile,double latitude,double longitude,long idleTime,String idleTimeDiff)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.

        newValues.put("DSNO",dsNo);
        newValues.put("DRIVER_ID",driverId);
        newValues.put("RDATE",rDate);
        newValues.put("RTIME",rTime);
        newValues.put("TRAVEL_TYPE",travelType);
        newValues.put("TOT_KMS",totKms);
        newValues.put("STARTING_TIME",startingTime);
        newValues.put("STOPPING_TIME",stoppingTime);
        newValues.put("GUEST_NAME",guestName);
        newValues.put("GUEST_MOBILE",guestMobile);
        newValues.put("LATITUDE",latitude);
        newValues.put("LONGITUDE",longitude);
        newValues.put("IDLE_TIME",idleTime);
        newValues.put("IDLE_TIME_DIFF",idleTimeDiff);
        // Insert the row into your table
        long value=db.insert("DUTY_UPDATES", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
        //  close();
        //            "( " +"LOGNO  text,DRIVER_ID text,JDATE text,TOT_KMS text,J_DETAILS text,SJ_DETAILS text,CJ_DETAILS text,TOT_HRS text,SIGNATURE text); ";
        return  value;
    }

    public ArrayList<DutyUpdates> getAllDutyUpdates(){

        db=dbHelper.getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * FROM DUTY_UPDATES",null);
        ArrayList<DutyUpdates> dataList=new ArrayList<DutyUpdates>();
        DutyUpdates data;

        if(c.getCount()>0)
        {
            for(int i=0;i<c.getCount();i++)
            {
                c.moveToNext();
                data=new DutyUpdates(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getInt(5),c.getString(6),
                        c.getString(7),c.getString(8),c.getString(9),c.getDouble(10),c.getDouble(11),c.getInt(12),c.getString(13));
                dataList.add(data);
            }
        }
        c.close();
        // close();
        return dataList;

    }

    public void deleteDutyUpdates(String dsno)
    {
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from DUTY_UPDATES WHERE DSNO ="+" '"+dsno+"' ");
        //System.out.println("deletion done.....duty_details");

    }

    public boolean findDSNo(String dSNo)
    {
        boolean flag=false;

        db=dbHelper.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM DUTY_UPDATES WHERE DSNO ="+" '"+dSNo+"' ",null);
        String status="";

        if(c.getCount()>0) {

            flag=true;
        }
        c.close();
        // close();
        return flag;

    }

    public void updateDutyUpdates(String dSNo,String stoppingTime,long distance,double latitude,double longitude)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("STOPPING_TIME",stoppingTime);
        newValues.put("TOT_KMS",distance);
        newValues.put("LATITUDE",latitude);
        newValues.put("LONGITUDE",longitude);
        // Assign values for each row.

        // Insert the row into your table
        db.update("DUTY_UPDATES",newValues,"DSNO="+" '"+dSNo+"' ", null);
    }

    public void updateDutyIdleTime(String dSNo,long idle_time,String idle_time_diff)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("IDLE_TIME",idle_time);
        newValues.put("IDLE_TIME_DIFF",idle_time_diff);
        // Assign values for each row.

        // Insert the row into your table
        db.update("DUTY_UPDATES",newValues,"DSNO="+" '"+dSNo+"' ", null);
    }

    public int getDutyIdleTime(String dsNo)
    {
        int i=0;

        Cursor c=db.rawQuery("SELECT IDLE_TIME FROM DUTY_UPDATES WHERE DSNO ="+" '"+dsNo+"' ",null);

        c.moveToNext();

        i=c.getInt(0);

        return i;
    }

    public String getDutyIdleTimeDiff(String dsNo)
    {
        String s="";

        Cursor c=db.rawQuery("SELECT IDLE_TIME_DIFF FROM DUTY_UPDATES WHERE DSNO ="+" '"+dsNo+"' ",null);

        c.moveToNext();

        s=c.getString(0);

        return s;
    }

    public void insertStatus(String dSNo,String status)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("DSNO",dSNo);
        newValues.put("STATUS",status);

        // Insert the row into your table
        long value=db.insert("STATUS", null, newValues);
    }

    public void updateStatus(String dSNo,String status)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("STATUS",status);

        // Assign values for each row.

        // Insert the row into your table
        db.update("STATUS",newValues,"DSNO="+" '"+dSNo+"' ", null);
    }

    public String getStatus(String dSNo)
    {
        String status="";

        Cursor c=db.rawQuery("SELECT * FROM STATUS WHERE DSNO ="+" '"+dSNo+"' ",null);

        c.moveToNext();

        status=c.getString(1);

        return status;
    }

    public void deleteStatusForDsno(String dsno)
    {
        // System.out.println("dsno is "+dsno);
        db=dbHelper.getReadableDatabase();
        db.execSQL("delete from STATUS WHERE DSNO ="+" '"+dsno+"'" );
        //System.out.println("deletion done.....duty_details");

    }

    public void insertTimes(String desc,String time,String dsno)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("DESC",desc);
        newValues.put("TIME",time);
        newValues.put("DSNO",dsno);
        // Insert the row into your table
        long value=db.insert("TIMES", null, newValues);

        // System.out.println("insertion result is "+value);
    }

    public void updateTimes(String desc,String time,String dsno)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("TIME",time);

        // Assign values for each row.
        // Insert the row into your table

        db.update("TIMES",newValues,"DESC ="+" '"+desc+"' "+" AND DSNO ="+" '"+dsno+"'", null);
    }

    public void deleteTimeForDsno(String dsno)
    {
        db=dbHelper.getReadableDatabase();
        // System.out.println("delete from TIMES WHERE DSNO ="+" '"+dsno+"'" );
        db.execSQL("delete from TIMES WHERE DSNO ="+" '"+dsno+"'" );
        //System.out.println("deletion done.....duty_details");

    }

    public String getTime(String dsno,String desc)
    {
        String time="";

        Cursor c=db.rawQuery("SELECT * FROM TIMES WHERE DESC ="+" '"+desc+"' "+" AND DSNO ="+" '"+dsno+"'  ",null);

        // System.out.println("SELECT * FROM TIMES WHERE DESC ="+" '"+desc+"' "+" AND DSNO ="+" '"+dsno+"'  ");

        c.moveToNext();

        if(c.getCount()>0) {

            time = c.getString(1);
        }

        return time;
    }

    public String getWaypoints(String dsno)
    {
        String waypoints="";

        String st = "";
        db = dbHelper.getReadableDatabase();
        float[] dist=new float[3];
        long distance=0;

        String sql="SELECT * FROM LATLNG_DETAILS WHERE DSNO ="+" '"+dsno+"' ";

        Cursor c=db.rawQuery(sql,null);

        // System.out.println("c count before "+c.getCount());

        int n;

//        if(c.getCount()<5)
//        {
//            n=1;
//        }
//        else {
//            n=c.getCount()/5;
//
//            // System.out.println("n is "+n);
//        }


        if(c.getCount()<10)
        {
            n=1;
        }
        else if(c.getCount()<10&&c.getCount()>20)
        {
            n=2;
        }
        else {
            n=c.getCount()/10;

            // System.out.println("n is "+n);
        }


        for(int i=0;(n*i)<c.getCount();i++)
        {
            c.moveToPosition(n*i);

            if(i==0)
            {
                waypoints=c.getString(1)+","+c.getString(2);
            }
            else {
                waypoints=waypoints+"|"+c.getString(1)+","+c.getString(2);
            }
        }

        String sql1="SELECT * FROM LATLNG_DETAILS";

        Cursor c1=db.rawQuery(sql1,null);

        // System.out.println("c count after is "+c1.getCount());

        for(int j=0;j<c1.getCount();j++)
        {
            c1.moveToNext();
            // System.out.println(c1.getString(0)+":"+c1.getString(1)+":"+c1.getString(2));

        }

        return waypoints;
    }



}


