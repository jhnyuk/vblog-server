package com.example.vblogserver.domain.click.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Click {

    // Click ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clickId", updatable = false)
    private Long clickId;

    // Click 한 게시글 ID
    @OneToOne
    @JoinColumn(name = "boardID")
    private Board board;

    // 게시글을 Click 한 사용자 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    private User user;

    @Builder
    public Click(Board board, User user) {
        this.board = board;
        this.user = user;
    }

    public Long getClickId() {
        return clickId;
    }

    public void setClickId(Long clickId) {
        this.clickId = clickId;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

}
