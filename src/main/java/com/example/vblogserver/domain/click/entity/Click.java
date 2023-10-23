package com.example.vblogserver.domain.click.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;

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
    @ManyToOne
    @JoinColumn(name = "boardID")
    private Board board;

    // 게시글을 Click 한 사용자 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    private User user;

    // 클릭한 날짜 (년, 월, 일만 저장)
    @CreatedDate
    private LocalDateTime clickedDate;

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

    public void setClickedDate(LocalDateTime clickedDate) {
        this.clickedDate = clickedDate;
    }

    @PrePersist
    protected void onCreate() {
        clickedDate = LocalDateTime.now(); // 현재 서버 시간으로 설정
    }



}
