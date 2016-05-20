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
