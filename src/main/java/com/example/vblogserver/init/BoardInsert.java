package com.example.vblogserver.init;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import org.json.JSONArray;
import org.json.JSONObject;

public class BoardInsert {

    private final BoardRepository boardRepository;

    public BoardInsert(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public void InsertData(JSONArray items, int i, CategoryG categoryG, CategoryM categoryM, CategoryS categoryS) {
        JSONObject json = items.getJSONObject(i);
        Board board = new Board();
        board.setCategoryG(categoryG);
        board.setCategoryM(categoryM);
        board.setCategoryS(categoryS);
        board.setTitle(json.optString("title"));
        board.setLink(json.optString("link"));
        board.setDescription(json.optString("description"));
        board.setThumbnails(json.optString("thumbnails"));
        board.setHashtag(json.optString("heshtag"));
        board.setWriter(json.optString("writer"));
        board.setCreatedDate(json.optString("createDate"));
        boardRepository.save(board);
    }
}
