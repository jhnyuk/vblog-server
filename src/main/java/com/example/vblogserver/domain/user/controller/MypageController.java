package com.example.vblogserver.domain.user.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.review.controller.ReviewService;
import com.example.vblogserver.domain.review.dto.ReviewDTO;
import com.example.vblogserver.domain.review.entity.Review;
import com.example.vblogserver.domain.review.repository.ReviewRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {
	private final UserService userService;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;

	//@GetMapping("/scrap")

	@GetMapping("/blog/review/{boardId}")
	public ResponseEntity<Map<String, Object>> myReviews(HttpServletRequest request, @PathVariable Long reviewId) {
		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

		// 액세스 토큰이 존재하고 유효하다면
		if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
			// 리뷰 ID로 리뷰 조회
			Review review = reviewRepository.findById(reviewId).orElse(null);

			return ResponseEntity.ok().body(Map.of("result", true, "reason", "조회 성공"));
		} else {
			return ResponseEntity.ok().body(Map.of("result", true, "reason", "유효하지 않은 액세스 토큰입니다."));
		}
	}

	// 리뷰 조회
	@GetMapping("/{boardId}")
	public ResponseEntity<List<ReviewDTO>> readReview(@PathVariable Long boardId) {
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
				reviewDTO.setUserId(review.getUser().getLoginId());
				reviewDTO.setGrade(review.getGrade());
				return reviewDTO;
			})
			.collect(Collectors.toList());

		return ResponseEntity.ok(reviewDTOs);

	}

	@GetMapping("/{userId}/reviews")
	public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		List<Review> reviews = user.getReviews();

		if (reviews.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<ReviewDTO> reviewDTOs = reviews.stream()
			.map(review -> {
				ReviewDTO reviewDTO = new ReviewDTO();
				reviewDTO.setId(review.getId());
				reviewDTO.setContent(review.getContent());
				reviewDTO.setCreatedDate(review.getCreatedDate());
				// Board 정보도 필요하다면 추가로 설정해줄 수 있습니다.
				// reviewDTO.setBoardId(review.getBoard().getId());
				reviewDTO.setGrade(review.getGrade());

				return reviewDTO;
			})
			.collect(Collectors.toList());

		return ResponseEntity.ok(reviewDTOs);
	}

	@GetMapping("/{userId}/reviews/blog")
	public ResponseEntity<List<ReviewDTO>> getUserBlogReviews(@PathVariable Long userId) {
		return getUserReviewsByCategory(userId, "blog");
	}

	@GetMapping("/{userId}/reviews/vlog")
	public ResponseEntity<List<ReviewDTO>> getUserVlogReviews(@PathVariable Long userId) {
		return getUserReviewsByCategory(userId, "vlog");
	}

	private ResponseEntity<List<ReviewDTO>> getUserReviewsByCategory(Long userId, String category) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		List<Review> reviews = user.getReviews().stream()
			.filter(review -> review.getBoard().getCategoryG().getCategoryName().equalsIgnoreCase(category))
			.collect(Collectors.toList());

		if (reviews.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<ReviewDTO> reviewDTOs = reviews.stream()
			.map(review -> {
				ReviewDTO reviewDTO = new ReviewDTO();
				reviewDTO.setId(review.getId());
				reviewDTO.setContent(review.getContent());
				reviewDTO.setCreatedDate(review.getCreatedDate());

				// Board 정보도 필요하다면 추가로 설정해줄 수 있습니다.
				// 예: reviewDTO.setBoardId(review.getBoard().getId());

				// Category 정보 설정
				if (review.getBoard().getCategoryG() != null) {
					reviewDTO.setCategory(review.getBoard().getCategoryG().getCategoryName());  // CategoryG의 getName() 메소드는 해당 객체에서 구현되어야 함.
				}

				reviewDTO.setGrade(review.getGrade());

				return reviewDTO;
			})
			.collect(Collectors.toList());

		return ResponseEntity.ok(reviewDTOs);
	}


}
