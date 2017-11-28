package com.hjsoft.mmtravels;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hjsoft.mmtravels.adapters.DBAdapter;

/**
 * Created by hjsoft on 28/2/17.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    Context context;

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DBAdapter.TABLE_LATLNG);
        sqLiteDatabase.execSQL(DBAdapter.TABLE_JOURNEY_DATA);
        sqLiteDatabase.execSQL(DBAdapter.TABLE_CREATE_DUTY_UPDATES);
        sqLiteDatabase.execSQL(DBAdapter.TABLE_CREATE_STATUS);
        sqLiteDatabase.execSQL(DBAdapter.TABLE_STORE_TIMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "TEMPLATE");
        //System.out.println("onUpgrade oldversion newversion"+oldVersion+" "+newVersion);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "LATLNG_DETAILS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "JOURNEY_DETAILS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "DUTY_UPDATES");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "STATUS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "TIMES");
        // Create a new one.
        onCreate(sqLiteDatabase);
    }
}
