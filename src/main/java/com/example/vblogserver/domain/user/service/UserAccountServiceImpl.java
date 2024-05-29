package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.bookmark.repository.FolderRepository;
import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.util.UserNotFoundException;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.InvalidTokenException;
import com.example.vblogserver.global.jwt.util.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountServiceImpl implements UserAccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FolderRepository folderRepository;

    /*
	자체 로그인 회원 가입 시 사용하는 회원 가입 API 로직
	자체 로그인이기 때문에 클라이언트 요청에서 localId, userName 등 추가 정보까지 다 받아와서
	Builder로 추가 정보를 포함한 User Entity 생성 후 DB에 저장
	 */
    @Override
    public User signUp(UserSignUpDto userSignUpDto) throws Exception {
        validateSignUp(userSignUpDto);

        User user = createUserFromSignUpDto(userSignUpDto);
        userRepository.save(user);

        createDefaultFoldersForUser(user);

        return user;
    }

    private void validateSignUp(UserSignUpDto userSignUpDto) throws Exception {
        Optional<User> existingUser = userRepository.findByLoginId(userSignUpDto.getLoginId());

        if (existingUser.isPresent()) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }

        if (userSignUpDto.getLoginId().length() < 4 || userSignUpDto.getLoginId().length() > 12) {
            throw new IllegalArgumentException("아이디의 길이는 4~12자여야 합니다.");
        }

        if (!userSignUpDto.getLoginId().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("아이디는 알파벳 대소문자와 숫자로만 이루어져야 합니다.");
        }
    }

    private User createUserFromSignUpDto(UserSignUpDto userSignUpDto) {
        return User.builder()
                .password(passwordEncoder.encode(userSignUpDto.getPassword()))
                .loginId(userSignUpDto.getLoginId())
                .username(userSignUpDto.getUsername())
                .role(Role.USER)
                .build();
    }

    private void createDefaultFoldersForUser(User user) {
        Folder defaultVlogFolder = new Folder("모든 게시글", "vlog", user);
        folderRepository.save(defaultVlogFolder);

        Folder defaultBlogFolder = new Folder("모든 게시글", "blog", user);
        folderRepository.save(defaultBlogFolder);
    }

    @Override
    public boolean isLoginIdDuplicated(String loginId) {
        // 아이디 길이 체크
        if (loginId.length() < 4 || loginId.length() > 12) {
            throw new IllegalArgumentException("아이디의 길이는 4~12자여야 합니다.");
        }

        // 알파벳 대소문자와 숫자로만 이루어져 있는지 체크
        if (!loginId.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("아이디는 알파벳 대소문자와 숫자로만 이루어져야 합니다.");
        }

        return userRepository.findByLoginId(loginId).isPresent();
    }

    public User login(String loginId, String password) throws Exception {
        User user = userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new Exception("아이디 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        return user;
    }

    /**
     * 탈퇴 메서드
     * @param refreshToken
     * 사용자의 리프레시 토큰을 찾지만, 여기서는 해당 사용자를 데이터베이스에서 완전히 삭제
     */
    @Override
    public void deleteUser(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        userRepository.delete(user);
    }

    @Override
    public User getUserByAccessToken(String accessToken) throws InvalidTokenException, UserNotFoundException {
        try {
            String loginId = jwtService.extractId(accessToken)
                    .orElseThrow(() -> new InvalidTokenException("유효하지 않은 액세스 토큰입니다."));

            return userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        } catch (InvalidTokenException | TokenExpiredException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다.", e);
        }
    }

    // 이름 수정 메서드
    @Override
    public void updateUsername(User user, String newUsername) {
        user.setUsername(newUsername);
        userRepository.save(user);
    }

}