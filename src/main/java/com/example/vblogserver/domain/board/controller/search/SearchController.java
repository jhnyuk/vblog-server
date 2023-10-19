package com.example.vblogserver.domain.board.controller.search;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.main.DTOConvertServcie;
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

    private final DTOConvertServcie dtoConvertServcie;

    public SearchController(BoardRepository boardRepository, DTOConvertServcie dtoConvertServcie) {
        this.boardRepository = boardRepository;
        this.dtoConvertServcie = dtoConvertServcie;
    }

    @GetMapping("/vlog/search")
    public List<MainBoardDTO> searchVlog(@RequestParam String keyword) {
        return searchBoards(keyword,"vlog");
    }

    @GetMapping("/blog/search")
    public List<MainBoardDTO> searchBlog(@RequestParam String keyword) {
        return searchBoards(keyword,"blog");
    }

    public List<MainBoardDTO> searchBoards(String keyword, String isVblog){
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

        return dtoConvertServcie.BoardToMainBoard(searchResults);
    }
}
