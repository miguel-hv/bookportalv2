package com.bookportal.backend.dto;

public class BookPatchRequest {

    private String title;
    private String author;
    private String review;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
}
