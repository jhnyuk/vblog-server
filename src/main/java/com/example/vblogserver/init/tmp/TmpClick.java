package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TmpClick {
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final ClickRepository clickRepository;

    public TmpClick(BoardService boardService, UserRepository userRepository, ClickRepository clickRepository) {
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.clickRepository = clickRepository;
    }

    public void updateTmpClick() {

        for(int i = 1; i < 361; i += 100) {
            // ID가 1, 101, 201, 301번인 게시글 조회
            Long int_to_long = (long) i;
            Board board = boardService.getBoardById(int_to_long);
            if (board == null) {
                System.out.println("게시글이 존재하지 않음");
                return;
            }
            // user 조회 (임시 계정 testuser)
            User user;
            try {
                user = userRepository.findByLoginId("testuser").orElseThrow(() -> new IllegalArgumentException("testuser 를 찾을 수 없습니다(Click)"));
            } catch (IllegalArgumentException e) {
                System.out.println("userID를 찾을 수 없습니다.(찜)");
                return;
            }
            //클릭 여부 저장
            Click setclick = Click.builder()
                    .board(board)
                    .user(user)
                    .build();
            Click saveClick = clickRepository.save(setclick);
            // click 여부 저장 성공 시 true, 실패 시 false
            if (saveClick != null) {
                System.out.println("Tmp Click 저장 성공");
            } else {
                System.out.println("Tmp Click 저장 실패");
            }
        }
    }
}
