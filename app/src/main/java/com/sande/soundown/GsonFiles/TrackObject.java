package com.sande.soundown.GsonFiles;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sandeep on 28-Apr-16.
 */
public class TrackObject implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.streamable ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeString(this.artwork_url);
        dest.writeInt(this.duration);
        dest.writeString(this.stream_url);
        dest.writeParcelable(this.user, flags);
    }

    public TrackObject() {
    }

    protected TrackObject(Parcel in) {
        this.id = in.readLong();
        this.streamable = in.readByte() != 0;
        this.title = in.readString();
        this.artwork_url = in.readString();
        this.duration = in.readInt();
        this.stream_url = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<TrackObject> CREATOR = new Parcelable.Creator<TrackObject>() {
        @Override
        public TrackObject createFromParcel(Parcel source) {
            return new TrackObject(source);
        }

        @Override
        public TrackObject[] newArray(int size) {
            return new TrackObject[size];
        }
    };
}
