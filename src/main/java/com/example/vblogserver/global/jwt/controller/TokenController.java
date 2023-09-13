package com.example.vblogserver.global.jwt.controller;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	// 액세스 토큰 검증
	@GetMapping("/verify-access")
	public ResponseEntity<ResponseDto> verifyAccessToken(HttpServletRequest request) {
		Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

		// 액세스 토큰이 존재하고 유효하다면
		if (accessTokenOpt.isPresent() && jwtService.isTokenValid(accessTokenOpt.get())) {
			return ResponseEntity.ok().headers(responseHeaders).body(
				new ResponseDto(true, "유효한 액세스 토큰입니다.")
			);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(responseHeaders).body(
				new ResponseDto(false, "유효하지 않은 액세스 토큰입니다.")
			);
		}
	}

	/* 리프레시 토큰으로 액세스 토큰 재발급
	** 리프레시 토큰으로 새로운 액세스 토큰을 발급받는다.
	* 요청에서 리프레시 토큰을 추출한 후, 이를 검증한다.
	 */
	@PostMapping("/refresh-access")
	public ResponseEntity<ResponseDto> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
		Optional<String> refreshTokenOpt = jwtService.extractRefreshToken(request);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

		if (refreshTokenOpt.isPresent()) {
			String refreshToken = refreshTokenOpt.get();

			if (!jwtService.isTokenValid(refreshToken)) { // 리프레쉬 토큰이 없거나, 유효하지 않으면
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(responseHeaders).body(
						new ResponseDto(false, "유효하지 않은 리프레시 토큰입니다. 다시 로그인 해주세요.")
				);
			}

			try {
				User user = userRepository.findByRefreshToken(refreshToken)
						// 해당 리프래쉬 토큰과 연결된 사용자가 DB에서 찾아지지 않으면
						.orElseThrow(() -> new UsernameNotFoundException("제공된 리프레시 토큰과 연관된 사용자를 찾을 수 없습니다."));

				String loginId = user.getLoginId();
				String newAccessToken = jwtService.createAccessToken(loginId);

				jwtService.sendAccessToken(response, newAccessToken);

				return ResponseEntity.ok().headers(responseHeaders).body(
						new ResponseDto(true, "새로운 액세스 토큰이 발급되었습니다.")
				);

			} catch (UsernameNotFoundException e) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(responseHeaders).body(
						new ResponseDto(false, e.getMessage())
				);
			}

		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(responseHeaders).body(
					new ResponseDto(false, "리프레시 토큰이 제공되지 않았습니다.")
			);
		}
	}
}