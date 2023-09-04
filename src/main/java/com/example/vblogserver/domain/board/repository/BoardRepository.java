package com.example.vblogserver.domain.board.repository;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByCategoryG(CategoryG categoryG);

    List<Board> findByCategoryGAndCategoryM(CategoryG categoryG, CategoryM categoryM);
}
