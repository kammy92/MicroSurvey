package com.resultier.crux.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class UserDetailsPref {
    public static String USER_ID = "user_id";
    private static UserDetailsPref userDetailsPref;
    private String USER_DETAILS = "USER_DETAILS";
    
    public static UserDetailsPref getInstance () {
        if (userDetailsPref == null)
            userDetailsPref = new UserDetailsPref ();
        return userDetailsPref;
    }
    
    private SharedPreferences getPref (Context context) {
        return context.getSharedPreferences (USER_DETAILS, Context.MODE_PRIVATE);
    }
    
    public String getStringPref (Context context, String key) {
        return getPref (context).getString (key, "");
    }
    
    public int getIntPref (Context context, String key) {
        return getPref (context).getInt (key, 0);
    }
    
    public boolean getBooleanPref (Context context, String key) {
        return getPref (context).getBoolean (key, false);
    }
    
    public void putBooleanPref (Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putBoolean (key, value);
        editor.apply ();
    }
    
    public void putStringPref (Context context, String key, String value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putString (key, value);
        editor.apply ();
    }
    
    public void putIntPref (Context context, String key, int value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putInt (key, value);
        editor.apply ();
    }
    
    public void clearPrefs (Context context) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.clear ().apply ();
    }
}