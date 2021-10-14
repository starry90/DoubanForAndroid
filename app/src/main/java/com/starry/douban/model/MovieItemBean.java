package com.starry.douban.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.starry.douban.db.RoomDBTable;

/**
 * @author Starry Jerry
 * @since 2021/6/29.
 */
@Entity(tableName = RoomDBTable.Movie.ITEM)
public class MovieItemBean {

    /**
     * episodes_info :
     * rate : 7.3
     * cover_x : 770
     * title : 夏日友晴天
     * url : https://movie.douban.com/subject/35161768/
     * playable : false
     * cover : https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2642900423.webp
     * id : 35161768
     * cover_y : 1100
     * is_new : false
     */

    private String episodes_info;
    private String rate;
    @Ignore
    private Integer cover_x;
    private String title;
    private String url;
    private Boolean playable;
    private String cover;
    @PrimaryKey
    @NonNull
    private String id;
    @Ignore
    private Integer cover_y;
    private Boolean is_new;

    private float dimensionRatio = (float) (Math.random() / 3.0f + 0.8f);

    public String getEpisodes_info() {
        return episodes_info;
    }

    public void setEpisodes_info(String episodes_info) {
        this.episodes_info = episodes_info;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Integer getCover_x() {
        return cover_x;
    }

    public void setCover_x(Integer cover_x) {
        this.cover_x = cover_x;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isPlayable() {
        return playable;
    }

    public void setPlayable(Boolean playable) {
        this.playable = playable;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCover_y() {
        return cover_y;
    }

    public void setCover_y(Integer cover_y) {
        this.cover_y = cover_y;
    }

    public Boolean isIs_new() {
        return is_new;
    }

    public void setIs_new(Boolean is_new) {
        this.is_new = is_new;
    }

    public float getDimensionRatio() {
        return dimensionRatio;
    }

    public void setDimensionRatio(float dimensionRatio) {
        this.dimensionRatio = dimensionRatio;
    }
}
