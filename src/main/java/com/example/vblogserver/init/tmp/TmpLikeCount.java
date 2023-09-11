package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TmpLikeCount{
    private final BoardRepository boardRepository;

    public TmpLikeCount(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }


    public void updateBoardLikeCount() {
        Board board1 = boardRepository.findById(5L).orElse(null);
        board1.setLikeCount(30);
        boardRepository.save(board1);

        Board board2 = boardRepository.findById(294L).orElse(null);
        board2.setLikeCount(45);
        boardRepository.save(board2);
    }
}
