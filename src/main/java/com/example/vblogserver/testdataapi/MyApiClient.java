package com.example.vblogserver.testdataapi;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class MyApiClient {

    public MyApiResponse requestDataFromNaverApi(String apiUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", "OAKFv7FLkX0OK6VWUFbj"); // 네이버 API 클라이언트 ID
        headers.set("X-Naver-Client-Secret", "c7WlmSezPS"); // 네이버 API 클라이언트 Secret
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MyApiResponse> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, new HttpEntity<>(headers), MyApiResponse.class);

        return response.getBody();
    }
}