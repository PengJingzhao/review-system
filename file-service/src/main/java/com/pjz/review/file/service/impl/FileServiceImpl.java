package com.pjz.review.file.service.impl;

import com.pjz.review.common.service.FileService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@DubboService
@Service
public class FileServiceImpl implements FileService {

    @Value("${minio.bucketName}")
    private String bucketName;

    @Resource
    private MinioClient minioClient;

    @Override
    public String uploadFile(MultipartFile file) {

        String fileName = System.currentTimeMillis() + file.getOriginalFilename();

        // 文件流，大小，Content-Type
        try (var inputStream = file.getInputStream()) {
            // 直接将文件流上传到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)             // MinIO桶名
                            .object(fileName)     // 存储文件名
                            .stream(inputStream, file.getSize(), -1)   // 文件流，长度，分块大小（-1默认）
                            .contentType(file.getContentType())     // 文件MIME类型
                            .build()
            );
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getReviewUrl(String objectName) {
        try {
            GetPresignedObjectUrlArgs presignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .method(Method.GET)
                    .build();
            return minioClient.getPresignedObjectUrl(presignedObjectUrlArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
