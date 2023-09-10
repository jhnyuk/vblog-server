package com.example.vblogserver.domain.board.dto;

import com.example.vblogserver.domain.board.entity.Board;

public class BannerDTO {
    private String imgUrl;
    private int likeCount;
    private int contentId;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public BannerDTO(String imgUrl, int likeCount, int contentId) {
        this.imgUrl = imgUrl;
        this.likeCount = likeCount;
        this.contentId = contentId;
    }
    public static BannerDTO fromBoard(Board board){
        return new BannerDTO(board.getThumbnails(), board.getLikeCount(), Math.toIntExact(board.getId()));
    }
}
