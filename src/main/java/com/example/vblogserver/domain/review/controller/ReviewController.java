package com.example.vblogserver.domain.review.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.review.dto.ReviewDTO;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewController(ReviewService reviewService, BoardService boardService, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<List<ReviewDTO>> getReviewByBoardId(@PathVariable Long boardId) {
        List<Review> reviews = reviewService.getReviewByBoardId(boardId);

        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> {
                    ReviewDTO reviewDTO = new ReviewDTO();
                    reviewDTO.setId(review.getId());
                    reviewDTO.setContent(review.getContent());
                    reviewDTO.setCreatedDate(review.getCreatedDate());
                    reviewDTO.setUserEmail(review.getUser());  // Assuming you have a User object in Review
                    return reviewDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);

    }

    @PostMapping("/{boardId}")
    public ResponseEntity<String> createReview(@PathVariable Long boardId, @RequestBody Map<String, String> request) {
        //BoardID 로 게시글 조회
        Board board = boardService.getBoardById(boardId);

        if (board == null) {
            return ResponseEntity.ok("{\"result\" : false,\"reason\" : \"게시글이 존재하지 않습니다\"}");
        }
        String content = request.get("content");
        String userEmail = request.get("userEmail");

        //user Email 로 계정 조회
        User user;
        try {
            user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException(userEmail + "을 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok("{\"result\" : false,\"reason\" : \""+userEmail+"을 찾을 수 없습니다\"}");
        }
        Review newReview = Review.builder()
                .content(content)
                .board(board)
                .user(user)
                .build();

        Review saveReview = reviewRepository.save(newReview);
        //리뷰 저장 성공 시 true, 실패 시 false
        if(saveReview != null){
            return ResponseEntity.ok("{\"result\" : true,\"reason\" : \"저장 성공\"}");
        } else{
            return ResponseEntity.ok("{\"result\" : false,\"reason\" : \"저장 실패\"}");
        }
    }

}