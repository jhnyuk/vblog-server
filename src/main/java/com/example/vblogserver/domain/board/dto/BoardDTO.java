package com.example.vblogserver.domain.board.dto;

import com.example.vblogserver.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BoardDTO {
    private String title;
    private String link;
    private String description;

    public BoardDTO(Board board) {
        this.title = board.getTitle();
        this.link = board.getLink();
        this.description = board.getDescription();
    }
}
