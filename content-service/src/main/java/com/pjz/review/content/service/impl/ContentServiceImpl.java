package com.pjz.review.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.common.service.ContentService;
import com.pjz.review.common.service.UserService;
import com.pjz.review.content.mapper.ContentMapper;
import com.pjz.review.common.entity.Content;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@DubboService
public class ContentServiceImpl implements ContentService {

    @DubboReference
    private UserService userService;

    private final ContentMapper contentMapper;

    public ContentServiceImpl(ContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }

    @Override
    public Content createContent(Content content,String token) {
        UserVO user = userService.getUser(token);
        System.out.println(user);
        Integer userId = user.getId();
        content.setUserId(Long.valueOf(userId));
        contentMapper.insert(content);
        return content;
    }

    @Override
    public Optional<Content> getContentById(Long id) {
        return Optional.ofNullable(contentMapper.selectById(id));
    }

    @Override
    public List<Content> getContentsByUserId(Long userId) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<>();
        query.eq(Content::getUserId, userId);
        return contentMapper.selectList(query);
    }

    @Override
    public Content updateContent(Long id, Content updatedContent) {
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new RuntimeException("Content not found");
        }
        content.setTextContent(updatedContent.getTextContent());
        content.setMediaUrls(updatedContent.getMediaUrls());
        content.setTags(updatedContent.getTags());
        content.setLocation(updatedContent.getLocation());
        content.setStatus(updatedContent.getStatus());

        contentMapper.updateById(content);
        return content;
    }

    @Override
    public void deleteContent(Long id) {
        contentMapper.deleteById(id);
    }

}
