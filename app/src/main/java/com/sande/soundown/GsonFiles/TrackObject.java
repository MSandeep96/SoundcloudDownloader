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


    protected TrackObject(Parcel in) {
        id = in.readLong();
        streamable = in.readByte() != 0;
        title = in.readString();
        artwork_url = in.readString();
        duration = in.readInt();
        stream_url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeByte((byte) (streamable ? 1 : 0));
        dest.writeString(title);
        dest.writeString(artwork_url);
        dest.writeInt(duration);
        dest.writeString(stream_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrackObject> CREATOR = new Creator<TrackObject>() {
        @Override
        public TrackObject createFromParcel(Parcel in) {
            return new TrackObject(in);
        }

        @Override
        public TrackObject[] newArray(int size) {
            return new TrackObject[size];
        }
    };

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
