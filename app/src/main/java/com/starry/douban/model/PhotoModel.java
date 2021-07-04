package com.starry.douban.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Starry Jerry
 * @since 21-7-4.
 */

public class PhotoModel implements Parcelable {


    public String getPhotoTitle() {
        return "";
    }

    public String getPhotoUrl() {
        return "";
    }

    public PhotoModel() {
    }

    protected PhotoModel(Parcel in) {
    }

    public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel in) {
            return new PhotoModel(in);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
