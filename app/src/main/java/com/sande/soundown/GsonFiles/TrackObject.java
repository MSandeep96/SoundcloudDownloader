package com.sande.soundown.GsonFiles;

/**
 * Created by Sandeep on 28-Apr-16.
 */
public class TrackObject {
    long id;
    boolean streamable;
    String title;
    String artwork_url;
    int duration;
    String stream_url;
    User user;


    public int getDuration() {
        return duration;
    }

    public String getStream_url() {
        return stream_url;
    }

    public long getId() {
        return id;
    }

    public boolean isStreamable() {
        return streamable;
    }

    public String getTitle() {
        return title;
    }

    public String getArtwork_url() {
        return artwork_url;
    }

    public User getUser() {
        return user;
    }
}
