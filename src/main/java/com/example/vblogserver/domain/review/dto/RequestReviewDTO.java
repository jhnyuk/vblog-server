package com.example.vblogserver.domain.review.dto;

public class RequestReviewDTO {

    //리뷰 작성 내용
    private String reviewContent;

    //평점
    private Number grade;

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public Number getGrade() {
        return grade;
    }

    public void setGrade(Number grade) {
        this.grade = grade;
    }
}
