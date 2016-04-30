package com.sande.soundown.Interfaces;

import com.sande.soundown.GsonFiles.TrackObject;

/**
 * Created by Sandeep on 30-Apr-16.
 */
public interface CallBackMain {
    public void setViewPager(int item);
    public void enqueueDownload(TrackObject song);
}
