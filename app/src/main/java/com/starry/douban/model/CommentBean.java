package com.starry.douban.model;

/**
 * @author Starry Jerry
 * @since 21-7-10.
 */

public class CommentBean {

    private String userImageUrl;

    private String userName;

    private String commentTitle;

    private String commentTime;

    private String commentShortContent;

    private String rid;

    private String actionUp;

    private String actionDown;

    private String reply;

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
        this.commentShortContent = commentShortContent.trim();
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getActionUp() {
        return actionUp;
    }

    public void setActionUp(String actionUp) {
        this.actionUp = actionUp.trim();
    }

    public String getActionDown() {
        return actionDown;
    }

    public void setActionDown(String actionDown) {
        this.actionDown = actionDown.trim();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply.trim();
    }

    @Override
    public String toString() {
        return "MovieComment{" +
                "userImageUrl='" + userImageUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", commentTitle='" + commentTitle + '\'' +
                ", commentTime='" + commentTime + '\'' +
                ", commentShortContent='" + commentShortContent + '\'' +
                ", rid='" + rid + '\'' +
                ", actionUp='" + actionUp + '\'' +
                ", actionDown='" + actionDown + '\'' +
                ", reply='" + reply + '\'' +
                '}';
    }
}
