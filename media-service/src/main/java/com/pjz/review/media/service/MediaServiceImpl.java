package com.pjz.review.media.service;

import com.pjz.review.common.entity.po.MediaFile;
import com.pjz.review.common.service.MediaService;
import com.pjz.review.media.mapper.MediaFileMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@DubboService
public class MediaServiceImpl implements MediaService {

    @Resource
    private MediaFileMapper mediaFileMapper;

    @Override
    public String upload(MultipartFile file) {

        try {
            // 1. 获取当前时间
            LocalDateTime now = LocalDateTime.now();

            // 2. 构造相对路径
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String datePath = now.format(formatter);

            // 3. 原始文件名
            String originalFileName = file.getOriginalFilename();

            // 4. 组合成完整的相对路径：按时间归档 + 文件名
            String relativePath = datePath + "/" + originalFileName;

            // 5. 保存文件到磁盘的逻辑（假设根目录在 /data/files/）
            String baseDir = "D:\\";
            File targetFile = new File(baseDir + relativePath);
            // 确保目录存在
            targetFile.getParentFile().mkdirs();
            file.transferTo(targetFile);

            // 6. 填充 MediaFile 对象
            MediaFile mediaFile = new MediaFile();
            mediaFile.setCreateAt(now)
                    .setPath(relativePath)         // 设置相对路径
                    .setType(file.getContentType());

            // 7. TODO: 保存 mediaFile 到数据库（如果有）
            mediaFileMapper.insert(mediaFile);

            // 8. 返回文件访问URL，可以拼接服务器地址+相对路径
            String fileUrl = "http://10.21.32.95:30618/api/media/file?path=" + relativePath.replace("\\", "/");

            return fileUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
