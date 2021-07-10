package com.starry.douban.model;

/**
 * @author Starry Jerry
 * @since 21-7-10.
 */

public class MovieComment {

    private String userImageUrl;

    private String userName;

    private String commentTitle;

    private String commentTime;

    private String commentShortContent;

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentShortContent() {
        return commentShortContent;
    }

    public void setCommentShortContent(String commentShortContent) {
        this.commentShortContent = commentShortContent;
    }

    @Override
    public String toString() {
        return "MovieComment{" +
                "userImageUrl='" + userImageUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", commentTitle='" + commentTitle + '\'' +
                ", commentTime='" + commentTime + '\'' +
                ", commentShortContent='" + commentShortContent + '\'' +
                '}';
    }
}
