package com.starry.douban.model;

/**
 * @author Starry Jerry
 * @since 2021/08/06.
 */
public class BookDetailBean {

    private String title;

    private String imageUrl;

    private String author;

    private String publisher;

    private String publishDate;

    private String ratingNumber;

    private String ratingPeopleCount;

    private String bookSummary;

    private String authorSummary;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getRatingNumber() {
        return ratingNumber;
    }

    public void setRatingNumber(String ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    public String getRatingPeopleCount() {
        return ratingPeopleCount;
    }

    public void setRatingPeopleCount(String ratingPeopleCount) {
        this.ratingPeopleCount = ratingPeopleCount;
    }

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public String getAuthorSummary() {
        return authorSummary;
    }

    public void setAuthorSummary(String authorSummary) {
        this.authorSummary = authorSummary;
    }
}
