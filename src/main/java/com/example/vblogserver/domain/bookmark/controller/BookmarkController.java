package com.example.vblogserver.domain.bookmark.controller;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.bookmark.dto.BookmarkDTO;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.bookmark.repository.FolderRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookmarkController {
    private final BookmarkRepository bookmarkRepository;
    private final JwtService jwtService;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final BoardRepository boardRepository;

    public BookmarkController(BookmarkRepository bookmarkRepository, JwtService jwtService, BoardService boardService, UserRepository userRepository, FolderRepository folderRepository, BoardRepository boardRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.jwtService = jwtService;
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.boardRepository = boardRepository;
    }

    // 스크랩 정보 insert
    @PostMapping("/bookmark/{contentId}/{folderId}")
    public ResponseEntity<Map<String, Object>> clickOnBookMark(HttpServletRequest request,
        @PathVariable Long contentId,
        @PathVariable Long folderId) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

        if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
            return null;
        }

        Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());


        if (loginIdOpt.isEmpty()) {
            return null;
        }

        User user = userRepository.findByLoginId(loginIdOpt.get()).orElseThrow(() -> new RuntimeException("User not found"));

        Board board = boardRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("Board not found"));

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Folder not found"));


        Bookmark saveBookmark = new Bookmark();
        saveBookmark.setFolder(folder);
        saveBookmark.setUser(user);
        saveBookmark.setBoard(board);
        Bookmark resultSave = bookmarkRepository.save(saveBookmark);
        //찜 저장 성공 시 true, 실패 시 false
        if (resultSave != null) {
            return ResponseEntity.ok().body(Map.of("result", true, "reason", "저장 성공"));
        } else {
            return ResponseEntity.ok().body(Map.of("result", false, "reason", "저장 실패"));
        }
    }

}
