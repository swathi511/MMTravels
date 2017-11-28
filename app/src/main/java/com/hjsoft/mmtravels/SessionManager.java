package com.hjsoft.mmtravels;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by hjsoft on 28/2/17.
 */
public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "sp";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_PWD = "pwd";
    public static final String KEY_CODE = "code";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String pwd){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PWD, pwd);
      //  editor.putString(KEY_CODE, code);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        System.out.println("name and pwd is ......."+pref.getString(KEY_NAME,null));
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PWD, pref.getString(KEY_PWD, null));
        //user.put(KEY_CODE, pref.getString(KEY_CODE, null));
        return user;
    }
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

        //  Intent i = new Intent(_context, MainActivity.class);
        // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        //  _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){

        return pref.getBoolean(IS_LOGIN, false);
    }

}

