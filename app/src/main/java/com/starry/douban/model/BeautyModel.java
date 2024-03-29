package com.starry.douban.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Starry Jerry
 * @since 18-12-29.
 */
public class BeautyModel extends PhotoModel implements Parcelable {

    /**
     * _id : 5b5e93499d21220fc64181a9
     * createdAt : 2018-07-30T12:25:45.937Z
     * desc : 2018-07-30
     * publishedAt : 2018-07-30T00:00:00.0Z
     * source : web
     * type : 福利
     * url : https://ww1.sinaimg.cn/large/0065oQSqgy1ftrrvwjqikj30go0rtn2i.jpg
     * used : true
     * who : lijinshanmx
     */

    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;

    private float dimensionRatio = (float) (Math.random() / 3.0f + 0.8f);

    @Override
    public String getPhotoTitle() {
        return desc;
    }

    @Override
    public String getPhotoUrl() {
        return url;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public float getDimensionRatio() {
        return dimensionRatio;
    }

    public void setDimensionRatio(float dimensionRatio) {
        this.dimensionRatio = dimensionRatio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.createdAt);
        dest.writeString(this.desc);
        dest.writeString(this.publishedAt);
        dest.writeString(this.source);
        dest.writeString(this.type);
        dest.writeString(this.url);
        dest.writeByte(this.used ? (byte) 1 : (byte) 0);
        dest.writeString(this.who);
    }

    public BeautyModel() {
        super();
    }

    protected BeautyModel(Parcel in) {
        super(in);
        this._id = in.readString();
        this.createdAt = in.readString();
        this.desc = in.readString();
        this.publishedAt = in.readString();
        this.source = in.readString();
        this.type = in.readString();
        this.url = in.readString();
        this.used = in.readByte() != 0;
        this.who = in.readString();
    }

    public static final Creator<BeautyModel> CREATOR = new Creator<BeautyModel>() {
        @Override
        public BeautyModel createFromParcel(Parcel source) {
            return new BeautyModel(source);
        }

        @Override
        public BeautyModel[] newArray(int size) {
            return new BeautyModel[size];
        }
    };
}
