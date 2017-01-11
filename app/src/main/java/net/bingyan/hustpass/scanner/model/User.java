package net.bingyan.hustpass.scanner.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lwenkun on 2017/1/8.
 */

public class User implements Parcelable {

    public int id = -1;
    public String email;
    public String pwdMd5;

    @Override
    public int describeContents() {
        return 0;
    }

    public User(String email, String pwdMd5){
        this.email = email;
        this.pwdMd5 = pwdMd5;
    }

    private User(Parcel in) {
        id = in.readInt();
        email = in.readString();
        pwdMd5 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(pwdMd5);
    }

    public static Creator<User> CREATOR = new Creator<User>() {
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
