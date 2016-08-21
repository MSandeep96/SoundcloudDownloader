package com.sande.soundown.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sandeep on 20-05-2016.
 */
public class PrefsWrapper {
    public final String SHARED_PRES="SNDWN";
    SharedPreferences mSharedPrefs;
    PrefsWrapper prefsWrapper;


    final String IS_LOGGED_IN = "IS_LOGGED_IN";
    final String ACCESS_TOKEN="ACCESS_TOKEN";
    final String USER_ID="USER_ID";


    public static PrefsWrapper with(Context context){
        return new PrefsWrapper(context);
    }


    public PrefsWrapper(Context context){
        mSharedPrefs=context.getSharedPreferences(SHARED_PRES,Context.MODE_PRIVATE);
    }



    //getters

    public boolean isLoggedIn(){
        return mSharedPrefs.getBoolean(IS_LOGGED_IN,false);
    }

    public String getAccessToken(){
        return mSharedPrefs.getString(ACCESS_TOKEN,"");
    }


    public long getUserID(){
        return mSharedPrefs.getLong(USER_ID,-1);
    }




    //setters

    public PrefsWrapper setUserID(long userID){
        SharedPreferences.Editor mShareEdit=mSharedPrefs.edit();
        mShareEdit.putLong(USER_ID,userID);
        mShareEdit.apply();
        return this;
    }

    public PrefsWrapper setAccessToken(String accesstoken){
        SharedPreferences.Editor mShareEdit=mSharedPrefs.edit();
        mShareEdit.putString(ACCESS_TOKEN,accesstoken);
        mShareEdit.apply();
        return this;
    }

    public PrefsWrapper setIsLoggedIn(boolean isli){
        SharedPreferences.Editor mShareEdit=mSharedPrefs.edit();
        mShareEdit.putBoolean(IS_LOGGED_IN,isli);
        mShareEdit.apply();
        return this;
    }


}
