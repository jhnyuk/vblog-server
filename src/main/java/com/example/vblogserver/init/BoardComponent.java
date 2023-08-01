package com.example.vblogserver.init;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoardComponent implements CommandLineRunner {
    private final BoardRepository boardRepository;
    private final YoutubeService youtubeService;

    @Autowired
    public BoardComponent(BoardRepository boardRepository, YoutubeService youtubeService){
        this.boardRepository = boardRepository;
        this.youtubeService = youtubeService;
    }

    @Override
    public void run(String... arg){
        CategoryG categoryG = new CategoryG();
        CategoryM categoryM = new CategoryM();
        CategoryS categoryS = new CategoryS();
        categoryG.setId(1L);
        categoryM.setId(1L);
        categoryS.setId(1L);

        String keyword = "국내여행";
        int maxResults = 5;
        YoutubeService youtubeService = new YoutubeService();
        String data = youtubeService.getYoutubeData(keyword,maxResults);

        System.out.println("11");
        JSONObject response = new JSONObject(data);
        JSONArray items = response.getJSONArray("items");

        List<Board> boards = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
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
            boards.add(board);
        }
        boardRepository.saveAll(boards);
    }
}
