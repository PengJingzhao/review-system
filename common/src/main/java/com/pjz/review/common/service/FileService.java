package com.pjz.review.common.service;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public interface FileService {

    @PostMapping( "/uploadFile")
    String uploadFile(@RequestParam("file") MultipartFile file);

    String getReviewUrl(String objectName);

}
