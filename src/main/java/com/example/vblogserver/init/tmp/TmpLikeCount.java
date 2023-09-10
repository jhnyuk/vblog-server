package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class TmpLikeCount implements CommandLineRunner {
    private final BoardRepository boardRepository;

    public TmpLikeCount(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Board board = boardRepository.findById(5L).orElse(null);
        board.setLikeCount(30);
        boardRepository.save(board);
    }
}
