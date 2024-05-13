package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.entity.User;

public interface UserAccountService {
    User signUp(UserSignUpDto userSignUpDto) throws Exception;
    boolean isLoginIdDuplicated(String loginId);
    void deleteUser(String refreshToken);
    User getUserByAccessToken(String accessToken);
    void updateUsername(User user, String newUsername);
}
