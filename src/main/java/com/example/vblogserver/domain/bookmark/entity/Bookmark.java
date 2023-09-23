package com.example.vblogserver.domain.bookmark.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    //찜 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    //찜 여부 (true, false)
    private Boolean bookmark;

    private Long contentId;

    // 게시글 ID - 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    private Board board;

    //찜한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder
    public Bookmark(Boolean bookmark, Board board, User user) {
        this.bookmark = bookmark;
        this.board = board;
        this.user = user;
    }

}
