package com.example.vblogserver.domain.user.controller;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.domain.user.dto.UserInfoDto;
import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.service.UserAccountService;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.InvalidTokenException;
import com.example.vblogserver.global.jwt.util.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserSignUpDto userSignUpDto, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }
        userAccountService.signUp(userSignUpDto);
        return ResponseEntity.ok().body("\"회원가입이 완료되었습니다.\"");
    }

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }

    @GetMapping("/check-id")
    public ResponseEntity<ResponseDto> checkId(@RequestParam String loginId) {
        ResponseDto response = new ResponseDto();
        try {
            boolean isDuplicated = userServiceImpl.isLoginIdDuplicated(loginId);
            response.setResult(!isDuplicated);
            response.setMessage(isDuplicated ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");
        } catch (IllegalArgumentException e) {
            response.setResult(false);
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        String refreshToken = jwtService.extractRefreshToken(request)
                .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰이 제공되지 않았습니다."));

        userAccountService.deleteUser(refreshToken);

        return ResponseEntity.ok("\"회원 탈퇴가 완료되었습니다.\"");
    }

    @GetMapping("/users/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new InvalidTokenException("액세스 토큰이 없습니다."));

        try {
            User user = userAccountService.getUserByAccessToken(accessToken);
            return ResponseEntity.ok(new UserInfoDto(user));
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDto(false, "만료된 액세스 토큰입니다.")
            );
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDto(false, "유효하지 않은 액세스 토큰입니다.")
            );
        }
    }

    @GetMapping("/myinfo/users/name")
    public ResponseEntity<UserInfoDto> getUserName(HttpServletRequest request) throws Exception {
        String accessToken = jwtService.extractAccessToken(request)
            .orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

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