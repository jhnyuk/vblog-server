package com.example.vblogserver.controller.board;


import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BoardController {
    private final BoardRepository boardRepository;

    public BoardController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    // vlog 조회
    @GetMapping("/test/v/list")
    public List<Board> getBoardsByCategoryG(){
        CategoryG categoryGId = new CategoryG();
        categoryGId.setId(1L);
        return boardRepository.findByCategoryG(categoryGId);
    }

}
