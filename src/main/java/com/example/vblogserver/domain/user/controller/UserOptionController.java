package com.example.vblogserver.domain.user.controller;

import com.example.vblogserver.domain.user.entity.OptionType;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.service.UserOptionService;
import com.example.vblogserver.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserOptionController {
    private final UserOptionService userOptionService;
    private final JwtService jwtService;

    public UserOptionController(UserOptionService userOptionService, JwtService jwtService, UserRepository userRepository) {
        this.userOptionService = userOptionService;
        this.jwtService = jwtService;
    }

    @GetMapping("/options")
    public ResponseEntity<List<OptionType>> getAllOptions() {
        List<OptionType> options = Arrays.asList(OptionType.values());
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    @PostMapping("/options")
    public ResponseEntity<List<Object>> saveUserOptions(HttpServletRequest request, @RequestBody List<OptionType> options) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtService.extractId(token)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));

        Map<String, Object> response = userOptionService.saveUserOptions(loginId, options);
        return ResponseEntity.ok(mapToList(response));
    }

    @PatchMapping("/myinfo/options")
    public ResponseEntity<List<Object>> updateUserOptions(HttpServletRequest request, @RequestBody List<OptionType> options){
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtService.extractId(token)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));

        Map<String, Object> response = userOptionService.updateUserOptions(loginId, options);
        return ResponseEntity.ok(mapToList(response));
    }

    public List<Object> mapToList(Map<String, Object> results){
        List<Object> response = results.values().stream()
                .filter(value -> value instanceof List)
                .flatMap(value -> ((List<String>) value).stream())
                .collect(Collectors.toCollection(ArrayList::new));
        return response;
    }
}
