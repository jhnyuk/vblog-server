package com.example.vblogserver.domain.review.dto;

public class RequestReviewDTO {

    //리뷰 작성 내용
    private String content;

    //평점
    private float grade;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }
}
