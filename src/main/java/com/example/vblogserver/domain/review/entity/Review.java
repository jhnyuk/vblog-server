package com.example.vblogserver.domain.review.entity;

import com.example.vblogserver.domain.board.entity.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

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
    private String createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Review(String content, Board board) {
        this.content = content;
        this.board = board;
    }
}