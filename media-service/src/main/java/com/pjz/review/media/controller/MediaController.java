package com.pjz.review.media.controller;

import com.pjz.review.common.service.MediaService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Resource
    private MediaService mediaService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        return mediaService.upload(file);
    }

    private final String baseDir = "D:\\"; // 和文件存储目录对应

    @GetMapping("/file")
    public ResponseEntity<org.springframework.core.io.Resource> getFile(@RequestParam("path") String relativePath) {
        try {
            // 防止路径穿越攻击（../）
            String filename = StringUtils.cleanPath(relativePath);

            Path file = Paths.get(baseDir).resolve(filename).normalize();

            if (!Files.exists(file) || !Files.isRegularFile(file)) {
                return ResponseEntity.notFound().build();
            }

            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());

            // 尝试自动探测文件类型
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName().toString() + "\"")
                    .body(resource);

        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
