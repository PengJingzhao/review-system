package com.pjz.review.file.service.impl;

import com.pjz.review.common.service.FileService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
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
        try (InputStream fis = file.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .contentType(file.getContentType())
                    .object(fileName)
                    .stream(fis, fis.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileName;
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
