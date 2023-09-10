package com.example.vblogserver.domain.board.controller;

import com.example.vblogserver.domain.board.dto.BannerDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BannerController {

    private final BoardRepository boardRepository;

    public BannerController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }


    @GetMapping("/vlog/banner")
    public BannerDTO getBennerData() {
        Board board = boardRepository.findFirstByCategoryGIdOrderByLikeCountDesc(1L);

        return BannerDTO.fromBoard(board);
    }

}
