package com.sande.soundown.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

/**
 * Created by Sandeep on 27-Apr-16.
 */
public class UtilsManager{

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getSongStorageDir() {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC)+"/Soundown");
    }

    public static File getSongFile(String fileName){
        fileName=fileName.replaceAll("\\W+","");
        return new File(getSongStorageDir(),fileName+".mp3");
    }

    public static String getSongStorDir(String filename){
        return "Soundown/"+filename+".mp3";
    }

    public static boolean doesSongExist(String title){
        title=title.replaceAll("\\W+", "");
        File mSongfile=new File(getSongStorageDir(),title+".mp3");
        return mSongfile.exists();
    }
}
