package com.pjz.review.common.service;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public interface FileService {

    @PostMapping( "/uploadFile")
    String uploadFile(@RequestParam("file") MultipartFile file);

    @GetMapping("/getReviewUrl")
    String getReviewUrl(@RequestParam("objectName") String objectName);

}
