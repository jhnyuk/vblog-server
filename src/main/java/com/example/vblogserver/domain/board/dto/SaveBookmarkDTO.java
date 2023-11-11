package com.example.vblogserver.domain.board.dto;

public class SaveBookmarkDTO {
    // 폴더 이름
    private String folderName;
    // 게시글 ID
    private Long contentId;


    public String getFolderName() {
        return folderName;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
