package com.sande.soundown.GsonFiles;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sandeep on 28-Apr-16.
 */
public class User implements Parcelable {
    String username;

    public String getUsername() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.username = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
