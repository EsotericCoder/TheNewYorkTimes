package com.esotericcoder.www.thenewyorktimes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Multimedium implements Parcelable {
    @SerializedName("subtype")
    @Expose
    private String subtype;
    @SerializedName("url")
    @Expose
    private String url;

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Multimedium() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subtype);
        dest.writeString(this.url);
    }

    protected Multimedium(Parcel in) {
        this.subtype = in.readString();
        this.url = in.readString();
    }

    public static final Creator<Multimedium> CREATOR = new Creator<Multimedium>() {
        @Override
        public Multimedium createFromParcel(Parcel source) {
            return new Multimedium(source);
        }

        @Override
        public Multimedium[] newArray(int size) {
            return new Multimedium[size];
        }
    };
}
