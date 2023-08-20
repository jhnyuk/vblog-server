package com.example.vblogserver.domain.review.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.category.entity.CategoryS;
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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    private String content;
    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    //게시글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Review(String content, Board board, User user) {
        this.content = content;
        this.board = board;
        this.user = user;
    }

    //댓글 작성 시간을 서버의 시간으로 insert
    @PrePersist
    public void prePersist(){
        createdDate = LocalDateTime.now();
    }
}