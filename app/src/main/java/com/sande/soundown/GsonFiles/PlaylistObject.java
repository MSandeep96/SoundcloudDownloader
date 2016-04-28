package com.sande.soundown.GsonFiles;

/**
 * Created by Sandeep on 29-Apr-16.
 */
public class PlaylistObject {
    String uri;
    String title;
    int track_count;
    User user;

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public int getTrack_count() {
        return track_count;
    }

    public User getUser() {
        return user;
    }
}
