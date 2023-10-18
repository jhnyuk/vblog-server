package com.example.vblogserver.domain.board.controller.search;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SearchController {
    private final BoardRepository boardRepository;

    public SearchController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping("/search")
    public List<Board> searchBoards(@RequestParam String keyword){
        return boardRepository.findByHashtagContainingOrDescriptionContainingOrTitleContaining(keyword, keyword, keyword);
    }
}
