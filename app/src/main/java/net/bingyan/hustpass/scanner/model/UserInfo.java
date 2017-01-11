package net.bingyan.hustpass.scanner.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * model for user info
 * Created by lwenkun on 2016/12/16.
 */

public class UserInfo implements Parcelable {

    public int id = -1;
    public String name;
    public String address;
    public String phoneNum;
    public String idNum;
    public boolean received = false;

    public UserInfo() {}

    private UserInfo(Parcel source) {
        this.id = source.readInt();
        this.name = source.readString();
        this.address = source.readString();
        this.phoneNum = source.readString();
        this.idNum = source.readString();
        this.received = (source.readInt() == 1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(phoneNum);
        parcel.writeString(idNum);
        parcel.writeInt(received ? 1 : 0);
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };


    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof UserInfo)) return false;
        UserInfo target = (UserInfo) obj;
        return target.id == id && target.name.equals(name)
                && target.address.equals(address)
                && target.phoneNum.equals(phoneNum)
                && target.idNum.equals(idNum)
                && target.received == received;
    }

    @Override
    public int hashCode() {
        return address.hashCode() + phoneNum.hashCode() + idNum.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() +
                "{id:" + id +
                ", name:\" " + name +
                "\", address:\"" + address +
                "\", phoneNum:\"" + phoneNum +
                "\", idNum:" + idNum + "\"}";
    }
}
