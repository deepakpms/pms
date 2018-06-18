package com.cvvid.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by webandmobile on 10/01/2018.
 */

public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "CvvidPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String USER_ID = "user_id";

    // Email address (make variable public to access from outside)
    public static final String USER_TYPE = "type";

    // Email address (make variable public to access from outside)
    public static final String USER_NAME = "username";

    // Email address (make variable public to access from outside)
    public static final String PROFILE_ID = "profile_id";

    // Email address (make variable public to access from outside)
    public static final String EMPLOYER_ID = "employer_id";

    // Email address (make variable public to access from outside)
    public static final String INSTITUTION_ID = "institute_id";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String user_id, String type, String username, String profile_id,String employer_id, String institute_id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing user_id in pref
        editor.putString(USER_ID, user_id);

        // Storing type in pref
        editor.putString(USER_TYPE, type);

        // Storing type in pref
        editor.putString(USER_NAME, username);

        // Storing type in pref
        editor.putString(PROFILE_ID, profile_id);

        // Storing type in pref
        editor.putString(EMPLOYER_ID, employer_id);

        // Storing type in pref
        editor.putString(INSTITUTION_ID, institute_id);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Signin.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(USER_ID, pref.getString(USER_ID, null));

        // user email id
        user.put(USER_TYPE, pref.getString(USER_TYPE, null));

        // user email id
        user.put(USER_NAME, pref.getString(USER_NAME, null));

        // user email id
        user.put(PROFILE_ID, pref.getString(PROFILE_ID, null));

        // user email id
        user.put(EMPLOYER_ID, pref.getString(EMPLOYER_ID, null));

        // user email id
        user.put(INSTITUTION_ID, pref.getString(INSTITUTION_ID, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Signin.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
