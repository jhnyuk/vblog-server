package com.example.vblogserver.domain.board.controller;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryMListController {
    private final BoardRepository boardRepository;

    @Autowired
    public CategoryMListController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    //vlog 카테고리별 조회
    //categoryName 은 CategoryM에 속하는 카테고리 Name이 영문으로 들어옴.
    //categoryName : "Travel"(여행), "Game"(게임), "Health"(건강), "Restaurant"(맛집), "Broadcasting"(방송), "Beauty"(뷰티)
    @GetMapping("/vlog/category/{categoryName}")
    public List<MainBoardDTO> getVlogCategoryData(@PathVariable String categoryName) {
        return getCategoryData(categoryName, 1L);
    }

    @GetMapping("/blog/category/{categoryName}")
    public List<MainBoardDTO> getBlogCategoryData(@PathVariable String categoryName) {
        return getCategoryData(categoryName, 2L);
    }

    private List<MainBoardDTO> getCategoryData(String categoryName, Long categoryGId) {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(categoryGId);

        CategoryM categoryM = getCategoryMByName(categoryName);
        List<Board> boardsWithCategoryGAndM = boardRepository.findByCategoryGAndCategoryM(categoryG, categoryM);

        List<MainBoardDTO> mainBoardDTOList = new ArrayList<>();
        for (Board board : boardsWithCategoryGAndM) {
            MainBoardDTO mainBoardDTO = new MainBoardDTO();
            mainBoardDTO.setContentId(board.getId());
            mainBoardDTO.setContentDate(board.getCreatedDate());
            mainBoardDTO.setContentTitle(board.getTitle());
            mainBoardDTO.setContent(board.getDescription());
            mainBoardDTO.setHeart(board.getLikeCount() != null ? board.getLikeCount() : 0);
            mainBoardDTO.setReview(board.getReviewCount() != null ? board.getReviewCount() : 0);
            mainBoardDTO.setUserName(board.getWriter());
            mainBoardDTO.setHashtags(board.getHashtag());
            mainBoardDTO.setImgurl(board.getThumbnails()); // 이미지 URL 처리 추가 필요

            mainBoardDTOList.add(mainBoardDTO);
        }

        return mainBoardDTOList;
    }
    private CategoryM getCategoryMByName(String categoryName) {
        System.out.println(categoryName);
        CategoryM categoryM = new CategoryM();
        Long categoryId = 0L;
        switch (categoryName) {
            case "Travel":
                categoryId = 1L;
                break;
            case "Game":
                categoryId = 2L;
                break;
            case "Health":
                categoryId = 3L;
                break;
            case "Restaurant":
                categoryId = 4L;
                break;
            case "Broadcasting":
                categoryId = 5L;
                break;
            case "Beauty":
                categoryId = 6L;
                break;
        }
        System.out.println(categoryId);
        categoryM.setId(categoryId);
        return categoryM;
    }



}
