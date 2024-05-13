package com.example.vblogserver.domain.bookmark.entity;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // vlog or blog
    private String type;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "folder")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "folder")
    private List<Board> boards = new ArrayList<>();

    public Folder(String name, String type, User user) {
        this.name = name;
        this.type = type;
        this.user = user;
    }
}

