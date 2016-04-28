package com.sande.soundown.GsonFiles;

/**
 * Created by Sandeep on 28-Apr-16.
 */
public class LikesObject {
    long id;
    boolean streamable;
    String title;
    String artwork_url;
    int duration;
    User user;


    public int getDuration() {
        return duration;
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
