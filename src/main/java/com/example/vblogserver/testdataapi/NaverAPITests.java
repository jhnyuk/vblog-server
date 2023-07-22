package com.example.vblogserver.testdataapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api")
public class NaverAPITests {
    @GetMapping("/b")
    public String getExample2() {
        MyApiClient myApiClient = new MyApiClient();
        String apiUrl = "https://developers.naver.com/proxyapi/openapi/v1/search/blog?display=10&start=1&sort=sim&filter=all&query=강원도 여행";

        MyApiResponse response = myApiClient.requestDataFromNaverApi(apiUrl);

        List<MyApiResponse.Item> items = response.getItems();
        for (MyApiResponse.Item item : items) {
            System.out.println("boardid : 1");
            System.out.println("cateGid : Blog");
            System.out.println("cateMid : 여행");
            System.out.println("cateSid : 강원도");
            System.out.println("title : " + item.getTitle());//글제목
            System.out.println("channelTitle: " + item.getBloggername());//게시자이름
            System.out.println("createDate : " + item.getPostdate()); //게시일자
            System.out.println("link : " + item.getLink());
            System.out.println("profile_img : 블로그는 요청받아올 수 없음");
            System.out.println("thumbnails : 블로그는 요청받아올 수 없음");
            System.out.println("description: " + item.getDescription());
            System.out.println("reviewCount : 100");
            System.out.println("bloggerlink: " + item.getBloggerlink());
            System.out.println("likeCount : 100");
            System.out.println("disLikeCount : 100");
            System.out.println("grade : 100");
            System.out.println("┏-------해시태그 추출------┐");
            String gettitle[] = item.getDescription().split("#");
            for(int y=1; y<gettitle.length; y++){
                //System.out.println(getdescription[y]);
                //System.out.println(gettitle[y]);
                String hastag[] = gettitle[y].split(" ");
                System.out.println("#"+hastag[0]);
            }
            System.out.println("└-------해시태그 추출------┘");

            System.out.println();
        }
        return null;
    }
}