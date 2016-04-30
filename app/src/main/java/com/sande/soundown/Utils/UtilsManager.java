package com.sande.soundown.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

/**
 * Created by Sandeep on 27-Apr-16.
 */
public class UtilsManager{

    public static boolean isLoggedIn(Context context){
        SharedPreferences mShared=context.getSharedPreferences(ProjectConstants.SHARED_PRES,Context.MODE_PRIVATE);
        return mShared.getBoolean(ProjectConstants.IS_LOGGED_IN,false);
    }

    public static void setIsLoggedIn(Context context,boolean isli){
        SharedPreferences.Editor mShareEdit=context.getSharedPreferences(ProjectConstants.SHARED_PRES,Context.MODE_PRIVATE).edit();
        mShareEdit.putBoolean(ProjectConstants.IS_LOGGED_IN,isli);
        mShareEdit.apply();
    }

    public static String getAccessToken(Context context){
        SharedPreferences mShared=context.getSharedPreferences(ProjectConstants.SHARED_PRES,Context.MODE_PRIVATE);
        return mShared.getString(ProjectConstants.ACCESS_TOKEN,"");
    }

    public static void setAccessToken(Context context,String accesstoken){
        SharedPreferences.Editor mShareEdit=context.getSharedPreferences(ProjectConstants.SHARED_PRES,Context.MODE_PRIVATE).edit();
        mShareEdit.putString(ProjectConstants.ACCESS_TOKEN,accesstoken);
        mShareEdit.apply();
    }

    public static long getUserID(Context context){
        SharedPreferences mShared=context.getSharedPreferences(ProjectConstants.SHARED_PRES,Context.MODE_PRIVATE);
        return mShared.getLong(ProjectConstants.USER_ID,-1);
    }

    public static void setUserID(Context context,long userID){
        SharedPreferences.Editor mShareEdit=context.getSharedPreferences(ProjectConstants.SHARED_PRES,Context.MODE_PRIVATE).edit();
        mShareEdit.putLong(ProjectConstants.USER_ID,userID);
        mShareEdit.apply();
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getSongStorageDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC)+"/Soundown");
        if (!file.mkdirs()) {
            // TODO: 18-Apr-16 make me own folder
        }
        return file;
    }

    public static File getSongFile(String fileName){
        fileName=fileName.replace("\\W+","");
        return new File(getSongStorageDir(),fileName+".mp3");
    }

    public static String getSongStorDir(String filename){
        return "Soundown/"+filename+".mp3";
    }

    public static boolean doesSongExist(String title){
        title=title.replaceAll("\\W+", "");
        File mSongfile=new File(getSongStorageDir(),title+".mp3");
        if(mSongfile.exists()){
            return true;
        }else{
            return false;
        }
    }
}
