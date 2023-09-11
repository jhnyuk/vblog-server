package com.example.vblogserver.init;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import com.example.vblogserver.init.naver.NaverService;
import com.example.vblogserver.init.tmp.TmpLikeCount;
import com.example.vblogserver.init.youtube.YoutubeService;
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
    private final NaverService naverService;
    private final TmpLikeCount tmpLikeCount;

    @Autowired
    public BoardComponent(BoardRepository boardRepository, YoutubeService youtubeService, NaverService naverService, TmpLikeCount tmpLikeCount){
        this.boardRepository = boardRepository;
        this.youtubeService = youtubeService;
        this.naverService = naverService;
        this.tmpLikeCount = tmpLikeCount;
    }

    @Override
    public void run(String... arg){
        // 카테고리 항목
        String[][] category_name = {
                {"1","1|2","국내여행|해외여행"},
                {"2","3|4|5|6|7|8","롤|발로란트|메이플|피파 온라인 4|디아블로4|로스트아크"},
                {"3","9|10|11","건강기능식품|운동|식이요법"},
                {"4","12|13|14","카페추천|양식|한식"},
                {"5","15|16","예능|드라마"},
                {"6","17|18","남성뷰티|여성뷰티"}
        };

        // 조회 최대 건수
        int maxResults = 10;
        for (int z=1; z<3; z++) {

            // 대분류 카테고리
            CategoryG categoryG = new CategoryG();
            categoryG.setId(Long.valueOf(z));

            // 중분류 카테고리
            for (int x = 0; x < category_name.length; x++) {
                CategoryM categoryM = new CategoryM();
                categoryM.setId(Long.parseLong(category_name[x][0]));

                // 소분류 카테고리
                String[] categoryS_id = category_name[x][1].split("\\|");
                String[] categoryS_name = category_name[x][2].split("\\|");
                System.out.println(categoryS_name[0]);
                for (int y = 0; y < categoryS_name.length; y++) {
                    CategoryS categoryS = new CategoryS();
                    categoryS.setId(Long.parseLong(categoryS_id[y]));

                    // 소분류 카테고리로 컨텐츠 검색 결과 반환
                    JSONObject response = new JSONObject();

                    //vlog
                    if (z==1) response = youtubeService.getYoutubeData(categoryS_name[y], maxResults);
                        //blog
                    else if (z==2) response = naverService.getNaverData(categoryS_name[y], maxResults);

                    JSONArray items = response.getJSONArray("items");

                    List<Board> boards = new ArrayList<>();

                    for (int i = 0; i < items.length(); i++) {
                        //System.out.println("items : " + items.toString());
                        JSONObject json = items.getJSONObject(i);
                        Board board = new Board();
                        board.setCategoryG(categoryG);
                        board.setCategoryM(categoryM);
                        board.setCategoryS(categoryS);
                        String get_title = json.optString("title");
                        board.setTitle(get_title.replaceAll("<b>|</b>", ""));
                        board.setLink(json.optString("link"));

                        String get_description = json.optString("description");
                        board.setDescription(get_description.replaceAll("<b>|</b>", ""));
                        board.setThumbnails(json.optString("thumbnails"));
                        board.setHashtag(json.optString("heshtag"));
                        board.setWriter(json.optString("writer"));
                        String get_createDate = json.optString("createDate").split("T")[0];
                        board.setCreatedDate(get_createDate.replaceAll("-", "."));

                        //System.out.println("board : " + board.toString());
                        boards.add(board);
                    }
                    boardRepository.saveAll(boards);
                    /*
                        데이터 조회 시
                        429 Too Many Requests: "{"errorMessage":"Rate limit exceeded. (속도 제한을 초과했습니다.)","errorCode":"012"}"
                        오류가 발생하여 반복 실행 시 시간차를 두고 반복 실행
                     */
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

            }

        }

        // 테스트용 데이터 inset : id가 5인 게시글의 좋아요 30 으로 설정
        tmpLikeCount.updateBoardLikeCount();



    }
}
