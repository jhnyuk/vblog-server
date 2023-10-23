package com.example.vblogserver.domain.review.dto;

public class RequestReviewDTO {

    //리뷰 작성 내용
    private String content;

    //평점
    private Number grade;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Number getGrade() {
        return grade;
    }

    public void setGrade(Number grade) {
        this.grade = grade;
    }
}
