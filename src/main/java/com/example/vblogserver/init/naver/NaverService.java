package com.example.vblogserver.init.naver;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NaverService {
    private final NaverResponseData naverResponseData;

    public NaverService(NaverResponseData naverService) {
        this.naverResponseData = naverService;
    }

    public JSONObject getNaverData(String keyword, int maxResults) {

        String apiUrl = "https://developers.naver.com/proxyapi/openapi/v1/search/blog?display="+maxResults+"&start=1&sort=sim&filter=all&query="+keyword;

        NaverEntity response = naverResponseData.requestDataFromNaverApi(apiUrl);
        String insertdata = "{\"items\":[";
        List<NaverEntity.Item> items = response.getItems();
        for (NaverEntity.Item item : items) {
            insertdata +="{\"title\":\"" + item.getTitle()+"\",";
            insertdata +="\"writer\":\"" + item.getBloggername()+"\",";
            insertdata +="\"createDate\":\"" + item.getPostdate()+"\",";
            insertdata +="\"link\":\"" + item.getLink()+"\",";
            insertdata +="\"description\":\"" + item.getDescription()+"\",";

            String hashtagdata = "";
            String gettitle[] = item.getDescription().split("#");
            for(int y=1; y<gettitle.length; y++){
                String hastag[] = gettitle[y].split(" ");
                hashtagdata += "#"+hastag[0];
            }
            insertdata +="\"heshtag\":\""+hashtagdata+"\"},";
            insertdata += "]}";

        }
        JSONObject returndata = new JSONObject(insertdata);
        return returndata;
    }
}