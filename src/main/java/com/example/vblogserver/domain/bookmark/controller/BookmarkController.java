package com.example.vblogserver.domain.bookmark.controller;

import com.example.vblogserver.domain.bookmark.dto.SaveBookmarkDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.bookmark.entity.Bookmark;
import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.bookmark.repository.BookmarkRepository;
import com.example.vblogserver.domain.bookmark.repository.FolderRepository;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    @PostMapping("/scrap")
    public String clickOnBookMark(HttpServletRequest request, @RequestBody SaveBookmarkDTO saveBookmarkDTO) {
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        // 토큰 유효성 검증
        if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
            return "유효하지 않은 토큰입니다.";
        }

        //아이디 유효성 검증
        Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());

        if (loginIdOpt.isEmpty()) {
            return "존재하지 않는 아이디입니다.";
        }

        User user = userRepository.findByLoginId(loginIdOpt.get()).orElseThrow(() -> new RuntimeException("User not found"));
        //게시글 유효성 검증
        Board board = boardRepository.findById(saveBookmarkDTO.getContentId())
                .orElseThrow(() -> new NotFoundException("게시글이 존재하지 않습니다"));

        //폴더 유효성 검증
        Folder folder = folderRepository.findByNameAndUserAndType(saveBookmarkDTO.getName(), user, board.getCategoryG().getCategoryName());
        if(folder == null) return "존재하지 않는 폴더입니다";

        //중복된 데이터가 있는 경우
        Bookmark bookmark = bookmarkRepository.findByBoardAndFolderAndUser(board,folder,user);
        if(bookmark != null) return "이미 스크랩한 게시글입니다";

        Bookmark saveBookmark = new Bookmark();
        saveBookmark.setFolder(folder);
        saveBookmark.setUser(user);
        saveBookmark.setBoard(board);
        Bookmark resultSave = bookmarkRepository.save(saveBookmark);

        //찜 저장 성공 시 true, 실패 시 false
        if (resultSave != null) {
            return "저장 성공";
        } else {
            return "저장 실패";
        }
    }



}

