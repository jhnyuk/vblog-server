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

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .link(link)
                .description(description)
                .build();
    }
}
