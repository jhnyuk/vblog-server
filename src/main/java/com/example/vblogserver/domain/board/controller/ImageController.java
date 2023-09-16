package com.example.vblogserver.domain.board.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageController {
    //@Value("${imgdir}")
    private String imgdir="/home/ubuntu/Img";

    //네이버 썸네일 이미지 호출 시 서버 도메인으로 변경하여 호출
    @GetMapping("/img/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> saveFile(@PathVariable String fileName){
        Path filePath = Paths.get(imgdir).resolve(fileName);
        Resource resource = null;
        try {
            // application.yml 에 정의된 파일 경로에 있는 이미지 파일 호출
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        System.out.println(5);
        if(resource.exists() && resource.isReadable()){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // 이미지 형식에 따라 변경

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
