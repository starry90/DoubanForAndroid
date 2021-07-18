package com.starry.douban.model;

/**
 * 书籍实体类
 */
public class BookItemBean {

    /**
     * image : https:\/\/img9.doubanio.com\/view\/subject\/s\/public\/s27279654.jpg
     * subject : https:\/\/book.douban.com\/subject\/4913064\/
     * publish : 余华 / 作家出版社 / 2012-8-1 / 20.00元
     * title : 活着
     * rating : 9.4
     * ratingNumber : (607180人评价)
     * summary : 《活着(新版)》讲述了农村人福贵悲惨的人生遭遇。福贵本是个阔少爷，可他嗜赌如命，终于赌光了家业，一贫如洗。他的父亲被他活活气死，母亲则在穷困中患了重病，福贵...
     */

    private String rating;
    private String ratingNumber;
    private String image;
    private String subjectUrl;
    private String publish;
    private String title;
    private String summary;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRatingNumber() {
        return ratingNumber;
    }

    public void setRatingNumber(String ratingNumber) {
        this.ratingNumber = ratingNumber.trim();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubjectUrl() {
        return subjectUrl;
    }

    public void setSubjectUrl(String subjectUrl) {
        this.subjectUrl = subjectUrl;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
