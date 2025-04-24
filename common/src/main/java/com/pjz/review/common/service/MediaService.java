package com.pjz.review.common.service;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/api/media")
public interface MediaService {

    String upload( MultipartFile file);


}
