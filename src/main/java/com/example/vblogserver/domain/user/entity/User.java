package com.example.vblogserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    private String pw;

    @Column(nullable = false, unique = true)
    private String userId; // 유저 아이디
    private String nickname;
    @Column(unique = true, nullable = false)
    private String email; // 가입 이메일
    private Integer age;
    private String gender;
    private String profileUrl; // 프로필 사진
    @CreatedDate
    private LocalDateTime createDate; // 가입 날짜

    //OAuth2
    private String provider; // google, naver, kakao
    private String providerId; // OAuth의 key(id)

    @Builder
    public User(String email, String userId, String nickname, String profileUrl, Integer age, String gender, String provider, String providerId) {
        this.email = email;
        this.userId = userId;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.profileUrl = profileUrl;
        this.provider = provider;
        this.providerId = providerId;
    }
}