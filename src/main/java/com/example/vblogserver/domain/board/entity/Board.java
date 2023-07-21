package com.example.vblogserver.domain.board.entity;

import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    private String title;
    private String link;
    private String description;
    private String thumbnails;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private String createdDate;

    @ColumnDefault("0")
    private Integer reviewCount;
    @ColumnDefault("0")
    private Integer likeCount;
    @ColumnDefault("0")
    private Integer disLikeCount;
    private Float grade;

    @Builder
    public Board(String title, String link, String description, User user) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.user = user;
    }
}
