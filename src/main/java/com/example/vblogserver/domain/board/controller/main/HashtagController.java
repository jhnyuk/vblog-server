package com.example.vblogserver.domain.board.controller.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HashtagController {
    private final BoardRepository boardRepository;

    public HashtagController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping("/vlog/hashtags")
    public List<String> searchVlog() {
        List<String> hashtags = new ArrayList<>();
        hashtags.add("세계여행");
        hashtags.add("빵지순례");
        hashtags.add("디즈니플러스");
        hashtags.add("카페");
        hashtags.add("놀면뭐하니");
        hashtags.add("다이어트운동");
        hashtags.add("반드시 가야하는 여행지");
        return hashtags;

        /*
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        return searchHashtags(categoryG);
         */
    }

    @GetMapping("/blog/hashtags")
    public List<String> searchBlog() {
        List<String> hashtags = new ArrayList<>();
        hashtags.add("내돈내산");
        hashtags.add("지락실2");
        hashtags.add("디즈니플러스");
        hashtags.add("연인");
        hashtags.add("나혼자산다");
        hashtags.add("다이어트운동");
        hashtags.add("리그오브레전드");
        return hashtags;
        /*
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        return searchHashtags(categoryG);
         */
    }

    public List<String> searchHashtags(CategoryG vblog){
        List<Board> searchResults = boardRepository.findByCategoryGOrderByLikeCountDesc(vblog);
        List<String> hashtags = searchResults.stream()
                .map(Board::getHashtag)
                .filter(hashtag -> hashtag!=null && !hashtag.isEmpty())
                .map(tag -> tag.split("#")[1])
                .limit(10)
                .collect(Collectors.toList());

        return hashtags;
    }
}
