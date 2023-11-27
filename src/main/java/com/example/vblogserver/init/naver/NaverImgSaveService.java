package com.example.vblogserver.init.naver;


import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

@Service
public class NaverImgSaveService {

    //@PostConstruct
    public String scrapeFirstImage2(String imageUrl, Long boardId) throws IOException {
        String Thumbnails = "";
        try {
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream();
                 OutputStream out = new FileOutputStream("C:\\Img\\"+boardId+".png")) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("이미지 다운로드 완료: "+boardId+".png");
        } catch (IOException e) {
            System.out.println("저장실패");
            e.printStackTrace();
        }
        return Thumbnails;
    }
}