package com.example.vblogserver.domain.review.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.review.dto.RequestReviewDTO;
import com.example.vblogserver.domain.review.dto.SeleteReviewDTO;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.service.UserAccountServiceImpl;
import com.example.vblogserver.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final JwtService jwtService;
    private final UserAccountServiceImpl userServiceImpl;
    private final BoardRepository boardRepository;

    @Autowired
    public ReviewController(ReviewService reviewService, BoardService boardService, UserRepository userRepository, ReviewRepository reviewRepository, JwtService jwtService, UserAccountServiceImpl userServiceImpl, BoardRepository boardRepository) {
        this.reviewService = reviewService;
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.jwtService = jwtService;
        this.userServiceImpl = userServiceImpl;
        this.boardRepository = boardRepository;
    }

    // 리뷰 조회 - 최신순
    @GetMapping("/new/{boardId}")
    public ResponseEntity<List<SeleteReviewDTO>> readNewReview(@PathVariable Long boardId) {
        List<Review> reviews = reviewService.getReviewByBoardId(boardId);

        reviews.sort((r1, r2) -> r2.getCreatedDate().compareTo(r1.getCreatedDate()));

        List<SeleteReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> {
                    SeleteReviewDTO reviewDTO = new SeleteReviewDTO();
                    reviewDTO.setContentId(review.getBoard().getId());
                    reviewDTO.setReviewId(review.getId());
                    // 평점만 있고 리뷰 내용이 없을 경우 "no review" 로 내려줌
                    if(review.getContent()==null) reviewDTO.setReviewContent("No review");
                        // 리뷰 내용이 있는 경우
                    else reviewDTO.setReviewContent(review.getContent());
                    reviewDTO.setCreatedDate(review.getCreatedDate());
                    reviewDTO.setUserName(review.getUser().getLoginId());
                    reviewDTO.setGrade(review.getGrade());
                    return reviewDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);

    }

    // 리뷰 조회 - 평점순
    @GetMapping("/grade/{boardId}")
    public ResponseEntity<?> readGradeReview(@PathVariable Long boardId) {
        List<Review> reviews = reviewService.getReviewByBoardId(boardId);

        // 부동소수점 비교에 사용할 오차 범위
        float tolerance = 0.1f; // 0.1 이하의 오차를 허용 (소수점 첫째 자리까지)

        Comparator<Review> comparator = (r1, r2) -> {
            float grade1 = r1.getGrade();
            float grade2 = r2.getGrade();

            // 오차 범위 내에서 비교
            if (Math.abs(grade1 - grade2) <= tolerance) {
                return 0;
            } else if (grade1 > grade2) {
                return -1;
            } else {
                return 1;
            }
        };

        reviews.sort(comparator);

        List<SeleteReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> {
                    SeleteReviewDTO reviewDTO = new SeleteReviewDTO();
                    reviewDTO.setContentId(review.getBoard().getId());
                    reviewDTO.setReviewId(review.getId());
                    // 평점만 있고 리뷰 내용이 없을 경우 "no review" 로 내려줌
                    if(review.getContent()==null) reviewDTO.setReviewContent("No review");
                        // 리뷰 내용이 있는 경우
                    else reviewDTO.setReviewContent(review.getContent());
                    reviewDTO.setCreatedDate(review.getCreatedDate());
                    reviewDTO.setUserName(review.getUser().getLoginId());
                    reviewDTO.setGrade(review.getGrade());
                    return reviewDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);

    }

    //리뷰 작성
    @PostMapping("/{boardId}")
    public /*ResponseEntity<String>*/String createReview(HttpServletRequest request, @PathVariable Long boardId,  @RequestBody RequestReviewDTO requestReviewDTO) {
        // 액세스 토큰 추출
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

        // 액세스 토큰이 존재하지 않거나 유효하지 않다면 에러 응답 반환
        if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
            return "유효하지 않은 토큰입니다.";
        }

        // 액세스 토큰에서 로그인 아이디 추출
        Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());

        // 로그인 아이디가 존재하지 않으면 에러 응답 반환
        if (loginIdOpt.isEmpty()) {
            return "존재하지 않는 아이디입니다.";
        }

        Board board = boardService.getBoardById(boardId);

        if (board == null) {
            return "게시글이 존재하지 않습니다";
        }

        String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출
        //BoardID 로 게시글 조회

        String reviewContent = requestReviewDTO.getReviewContent();

        float grade = Float.parseFloat(requestReviewDTO.getGrade().toString());

        //LoginID 로 userID 조회
        User user;

        try {
            user = userRepository.findByLoginId(userId).orElseThrow(() -> new IllegalArgumentException(userId + "을 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            return userId+"을 찾을 수 없습니다";
        }


        Review newReview = Review.builder()
                .content(reviewContent)
                .board(board)
                .user(user)
                .grade(grade)
                .build();
        Review saveReview = reviewRepository.save(newReview);
        //리뷰 저장 성공 시 true, 실패 시 false
        if(saveReview != null){
            List<Review> reviews = reviewRepository.findReviewsByBoard(board);
            float sum = 0.0f;
            if(!reviews.isEmpty()){
                //모든 평점의 합산을 구함
                for(Review review : reviews){
                    sum+=review.getGrade();
                }
                //합산된 평점을 나누어 총 평점을 구함
                float avrGrade = sum / reviews.size();
                // 두 번째 자리 이후를 버리고 소수점 첫 번째 자리까지만 저장
                avrGrade = (float) (Math.floor(avrGrade * 10.0) / 10.0);
                //구한 총 평점을 board 의 업데이트 grade에 업데이트
                board.setReviewCount(reviews.size());
                board.setGrade(avrGrade);
                boardRepository.save(board);
            }
            return "저장성공";
        } else{
            return "저장실패";
        }

    }

    //리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> updateReview(HttpServletRequest request, @PathVariable Long reviewId, @RequestBody RequestReviewDTO requestReviewDTO) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/text;charset=UTF-8");

        // 액세스 토큰이 존재하고 유효하다면
        if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
            String userId = jwtService.extractId(accessTokenOpt.get()).orElse(null); // 액세스 토큰에서 사용자 ID 추출
            Review review = reviewRepository.findById(reviewId).orElse(null);

            // 수정할 리뷰가 없을 경우
            if (review == null) {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", "수정할 리뷰가 존재하지 않습니다"));
                //return ResponseEntity.ok("{\"result\" : false,\"reason\" : \"수정할 리뷰가 존재하지 않습니다\"}");
            }
            String newReviewContent = requestReviewDTO.getReviewContent();
            float newGrade = Float.parseFloat(requestReviewDTO.getGrade().toString());

            // 수정된 리뷰 내용+평점으로 저장
            review.setContent(newReviewContent);
            review.setGrade(newGrade);
            Review updatedReview = reviewRepository.save(review);

            if (updatedReview != null) {
                return ResponseEntity.ok().body(Map.of("result", true, "reason", "수정 성공"));
            } else {
                return ResponseEntity.ok().body(Map.of("result", false, "reason", "수정 실패"));
            }
        } else {
            // 유효하지 않은 토큰일 경우 405
            return ResponseEntity.notFound().build();
        }
    }


    //리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public String deleteReview(HttpServletRequest request, @PathVariable Long reviewId) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        // 액세스 토큰이 존재하지 않거나 유효하지 않다면 에러 응답 반환
        if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
            return "유효하지 않은 토큰입니다.";
        }
        // 액세스 토큰에서 로그인 아이디 추출
        Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());

        // 로그인 아이디가 존재하지 않으면 에러 응답 반환
        if (loginIdOpt.isEmpty()) {
            return "존재하지 않는 아이디입니다.";
        }

        // 리뷰 ID로 리뷰 조회
        Review review = reviewRepository.findById(reviewId).orElse(null);

        // 삭제할 리뷰가 없을 경우
        if (review == null) {
            return "삭제할 리뷰가 존재하지 않습니다";
            //return ResponseEntity.ok().body(Map.of("result", false, "reason", "삭제할 리뷰가 존재하지 않습니다"));
        }

        // 리뷰 삭제
        reviewRepository.delete(review);
        // 삭제 후 board 테이블의 review_count 업데이트
        Board board = review.getBoard();
        List<Review> reviews = reviewRepository.findReviewsByBoard(board);
        float sum = 0.0f;
        if(!reviews.isEmpty()){
            //모든 평점의 합산을 구함
            for(Review reviewi : reviews){
                sum+=reviewi.getGrade();
            }
            //합산된 평점을 나누어 총 평점을 구함
            float avrGrade = sum / reviews.size();
            // 두 번째 자리 이후를 버리고 소수점 첫 번째 자리까지만 저장
            avrGrade = (float) (Math.floor(avrGrade * 10.0) / 10.0);
            //구한 총 평점을 board 의 업데이트 grade에 업데이트
            board.setReviewCount(reviews.size());
            board.setGrade(avrGrade);
            boardRepository.save(board);
        }
        return "삭제 성공";
    }
}
