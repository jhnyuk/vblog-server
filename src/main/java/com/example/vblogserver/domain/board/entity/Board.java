package com.example.vblogserver.domain.board.entity;

import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    private String writer;
    private String title;
    private String link;
    private String description;
    private String thumbnails;
    private String hashtag;

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

    @OneToOne
    @JoinColumn(name = "category_g")
    private CategoryG categoryG;
    @OneToOne
    @JoinColumn(name = "category_m")
    private CategoryM categoryM;
    @ManyToOne
    @JoinColumn(name = "category_s")
    private CategoryS categoryS;


    @Builder
    public Board(String title, String link, String description, User user) {
        this.title = title;
        this.link = link;
        this.description = description;

    }
}
