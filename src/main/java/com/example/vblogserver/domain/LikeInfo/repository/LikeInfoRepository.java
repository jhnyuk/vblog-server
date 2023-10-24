package com.example.vblogserver.domain.LikeInfo.repository;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.LikeInfo.entity.LikeInfo;
import com.example.vblogserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeInfoRepository extends JpaRepository<LikeInfo, Long> {

    // 특정 게시글에 대한 특정 유저가 좋아요, 싫어요를 저장한 정보 조회
    Optional<LikeInfo> findByBoardAndUser(Board board, User user);
    // 좋아요, 싫어요를 count 하여 Board 의 like_count , dis_like_count를 업데이트하기 위한 용도
    int countByLikeInfoAndBoard(Boolean likeInfo, Board board);
}
