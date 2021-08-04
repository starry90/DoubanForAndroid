package com.starry.douban.model;

import java.io.Serializable;

/**
 * @author Starry Jerry
 * @since 21-7-24.
 */

public class VideoItemBean implements Serializable {

    private static final long serialVersionUID = -4084477940785000047L;

    private String videoTitle;
    private String subjectUrl;
    private String videoImage;
    private String videoPlayerUrl;

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getSubjectUrl() {
        return subjectUrl;
    }

    public void setSubjectUrl(String subjectUrl) {
        this.subjectUrl = subjectUrl;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    public String getVideoPlayerUrl() {
        return videoPlayerUrl;
    }

    public void setVideoPlayerUrl(String videoPlayerUrl) {
        this.videoPlayerUrl = videoPlayerUrl;
    }
}
