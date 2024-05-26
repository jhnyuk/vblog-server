package com.example.vblogserver.domain.user.controller;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.domain.user.dto.UserInfoDto;
import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.service.UserAccountService;
import com.example.vblogserver.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    private String extractToken(HttpServletRequest request, Function<HttpServletRequest, Optional<String>> extractor) {
        return extractor.apply(request)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 제공되지 않았습니다."));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserSignUpDto userSignUpDto, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }
        userAccountService.signUp(userSignUpDto);
        return ResponseEntity.ok().body("\"회원가입이 완료되었습니다.\"");
    }

    @GetMapping("/jwt-test")
    public ResponseEntity<String> jwtTest() {
        return ResponseEntity.ok("jwtTest 요청 성공");
    }

    @GetMapping("/check-id")
    public ResponseEntity<ResponseDto> checkId(@RequestParam String loginId) {
        boolean isDuplicated = userAccountService.isLoginIdDuplicated(loginId);
        ResponseDto response = ResponseDto.createCheckIdResponse(isDuplicated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        String refreshToken = extractToken(request, jwtService::extractRefreshToken);
        userAccountService.deleteUser(refreshToken);
        return ResponseEntity.ok("\"회원 탈퇴가 완료되었습니다.\"");
    }

    @GetMapping("/myinfo")
    public ResponseEntity<UserInfoDto> getUserInfo(HttpServletRequest request) {
        String accessToken = extractToken(request, jwtService::extractAccessToken);
        User user = userAccountService.getUserByAccessToken(accessToken);
        return ResponseEntity.ok(new UserInfoDto(user));
    }

    @GetMapping("/myinfo/name")
    public ResponseEntity<UserInfoDto> getUserName(HttpServletRequest request) {
        String accessToken = extractToken(request, jwtService::extractAccessToken);
        User user = userAccountService.getUserByAccessToken(accessToken);
        return ResponseEntity.ok(new UserInfoDto(user));
    }

    @PatchMapping("/myinfo/users/name")
    public ResponseEntity<UserInfoDto> updateUsername(HttpServletRequest request, @RequestBody Map<String, String> updateRequest) throws Exception {
        String accessToken = jwtService.extractAccessToken(request)
            .orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

        User user = userAccountService.getUserByAccessToken(accessToken);

        // 클라이언트로부터 전달받은 새로운 이름으로 사용자의 이름 변경
        String newUsername = updateRequest.get("username");
        if (newUsername != null && !newUsername.isEmpty()) {
            userAccountService.updateUsername(user, newUsername);
        }

        return ResponseEntity.ok(new UserInfoDto(user));
    }
}