package com.example.vblogserver.domain.board.controller.search;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SearchController {
    private final BoardRepository boardRepository;

    public SearchController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping("/vlog/search")
    public List<Board> searchVlog(@RequestParam String keyword) {
        return searchBoards(keyword,"vlog");
    }

    @GetMapping("/blog/search")
    public List<Board> searchBlog(@RequestParam String keyword) {
        return searchBoards(keyword,"blog");
    }

    public List<Board> searchBoards(String keyword, String isVblog){
        List<Board> searchResults = boardRepository.findByHashtagContainingOrDescriptionContainingOrTitleContaining(keyword, keyword, keyword);

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

        return searchResults;
    }
}
