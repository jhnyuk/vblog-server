package com.example.vblogserver.domain.board.controller.detail;

import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {
    private final BoardRepository boardRepository;

    public LikeController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // 좋아요, 싫어요 true, false
}
