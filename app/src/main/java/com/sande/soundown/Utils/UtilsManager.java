package com.sande.soundown.Utils;

import android.net.Uri;
import android.os.Environment;

import com.sande.soundown.GsonFiles.TrackObject;

import java.io.File;

/**
 * Created by Sandeep on 27-Apr-16.
 */
public class UtilsManager{

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getRoot(){
        return new File(Environment.getExternalStorageDirectory()+"/SoundDown");
    }

    public static File getContainingFolder(String artist){
        File mFolder=new File(getRoot(),artist);
        try {
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
        }catch (Exception e){
            //do something?
        }
        return mFolder;
    }

    public static File getDestinationFile(TrackObject song){
        return new File(getContainingFolder(song.getUser().getUsername()),song.getTitle()+".mp3");
    }

    public static Uri getDestinationUri(TrackObject song) {
        File mFile=new File(getContainingFolder(song.getUser().getUsername()),song.getTitle()+".mp3");
        return Uri.fromFile(mFile);
    }

    public static boolean doesSongExist(TrackObject song){
        File mFile=new File(getContainingFolder(song.getUser().getUsername()),song.getTitle()+".mp3");
        return mFile.exists();
    }
}
