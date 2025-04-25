package com.pjz.review.common.service;


import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String uploadFile(MultipartFile file);

    String getReviewUrl(String objectName);

}
