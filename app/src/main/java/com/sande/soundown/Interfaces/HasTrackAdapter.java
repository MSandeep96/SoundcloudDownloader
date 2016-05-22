package com.sande.soundown.Interfaces;

import com.sande.soundown.GsonFiles.TrackObject;

/**
 * Created by Sandeep on 22-05-2016.
 */


//Must be implemented by any activity that has a track
public interface HasTrackAdapter {
    void launchDialog(TrackObject song);
    void startDownload(TrackObject song);
}
