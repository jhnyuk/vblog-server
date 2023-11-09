package com.example.vblogserver.domain.board.controller.search;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.main.DTOConvertServcie;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SearchController {
    private final BoardRepository boardRepository;

    private final DTOConvertServcie dtoConvertServcie;

    public SearchController(BoardRepository boardRepository, DTOConvertServcie dtoConvertServcie) {
        this.boardRepository = boardRepository;
        this.dtoConvertServcie = dtoConvertServcie;
    }

    /*
        /vlog/search/category
        /blog/search/category
        : 검색 시 검색란에 '세계' 까지만 입력했을 때 연관검색어 표시용
     */
    @GetMapping("/vlog/search/category")
    public List<String> searchVlogCategory(@RequestParam String keyword) {
        return searchCategory(keyword, "vlog");
    }

    @GetMapping("/blog/search/category")
    public List<String> searchBlogCategory(@RequestParam String keyword) {
        return searchCategory(keyword,"blog");
    }

    /*
        /vlog/search
        /blog/search
        : 키워드 검색 결과 게시글 정보
          (제목 + 내용이 아닌 해시태그와 일치하는 게시글만 내려줌)
     */
    @GetMapping("/vlog/search")
    public List<MainBoardDTO> searchVlog(@RequestParam String keyword) {
        return searchBoards(keyword, "vlog");
    }

    @GetMapping("/blog/search")
    public List<MainBoardDTO> searchBlog(@RequestParam String keyword) {
        return searchBoards(keyword,"blog");
    }

    private List<String> searchCategory(String keyword, String isVblog) {
        List<Board> searchResults = boardRepository.findByHashtagContainingOrderByCreatedDateDesc(keyword);
        Set<String> matchingHashtags = new HashSet<>();

        // 검색 결과 필터링 (vlog, blog)
        if (isVblog.equals("vlog")) {
            searchResults = searchResults.stream()
                    .filter(board -> board.getCategoryG().getId() == 1)
                    .collect(Collectors.toList());
        } else if (isVblog == "blog") {
            searchResults = searchResults.stream()
                    .filter(board -> board.getCategoryG().getId() == 2)
                    .collect(Collectors.toList());
        }

        for (Board board : searchResults) {
            String[] hashtags = board.getHashtag().split("#");

            for (String hashtag : hashtags) {
                if (hashtag.contains(keyword)) {
                    matchingHashtags.add("#" + hashtag);
                }
            }
        }

        return new ArrayList<>(matchingHashtags);
    }



    public List<MainBoardDTO> searchBoards(String keyword, String isVblog){
        List<Board> searchResults = boardRepository.findByHashtagContainingOrderByCreatedDateDesc(keyword);

        // 검색 결과 필터링 (vlog, blog)
        if (isVblog.equals("vlog")) {
            searchResults = searchResults.stream()
                    .filter(board -> board.getCategoryG().getId() == 1)
                    .collect(Collectors.toList());
        } else if (isVblog == "blog") {
            searchResults = searchResults.stream()
                    .filter(board -> board.getCategoryG().getId() == 2)
                    .collect(Collectors.toList());
        }

        return dtoConvertServcie.BoardToMainBoard(searchResults);
    }
}
