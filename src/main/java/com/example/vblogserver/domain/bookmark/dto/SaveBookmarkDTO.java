package com.example.vblogserver.domain.bookmark.dto;

public class SaveBookmarkDTO {
    // 폴더 이름
    private String name;
    // 게시글 ID
    private Long contentId;


    public String getName() {
        return name;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
