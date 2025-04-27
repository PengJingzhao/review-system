package com.pjz.review.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pjz.review.common.entity.vo.ContentVO;
import com.pjz.review.common.entity.vo.PageVO;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.common.service.ContentService;
import com.pjz.review.common.service.RelationService;
import com.pjz.review.common.service.UserService;
import com.pjz.review.content.mapper.ContentMapper;
import com.pjz.review.common.entity.Content;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@DubboService
public class ContentServiceImpl implements ContentService {

    @DubboReference
    private UserService userService;

    @DubboReference
    private RelationService relationService;

    private final ContentMapper contentMapper;

    public ContentServiceImpl(ContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }

    @Override
    public Content createContent(Content content, String token) {
        content.setUserId(Long.valueOf(userService.getUser(token).getId()))
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
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
        content.setTextContent(updatedContent.getTextContent())
                .setMediaUrls(updatedContent.getMediaUrls())
                .setTags(updatedContent.getTags())
                .setLocation(updatedContent.getLocation())
                .setStatus(updatedContent.getStatus());

        contentMapper.updateById(content);
        return content;
    }

    @Override
    public void deleteContent(Long id) {
        contentMapper.deleteById(id);
    }

    @Override
    public PageVO<ContentVO> getSelfPosts(String token, Long current, Long size) {

        UserVO user = userService.getUser(token);

        Page<Content> contentPage = contentMapper.selectByUserId(user.getId(), current, size);

        return getContentVOPageVO(user, contentPage);
    }

    @Override
    public PageVO<ContentVO> getUserPosts(Long userId) {
        return null;
    }

    @Override
    public PageVO<ContentVO> getSelfFollowerFeed(String token, Long current, Long size) {

        UserVO user = userService.getUser(token);
        Integer userId = user.getId();

        List<Integer> attentionList = relationService.getAttentionList(userId);

        Page<Content> contentPage = contentMapper.selectFeedByUserId(userId, current, size, attentionList);

        return getContentVOPageVO(user, contentPage);
    }

    @NotNull
    private PageVO<ContentVO> getContentVOPageVO(UserVO user, Page<Content> contentPage) {
        List<ContentVO> contentVOList = contentPage.getRecords().stream()
                .map(content -> {
                    ContentVO contentVO = new ContentVO();
                    BeanUtils.copyProperties(content, contentVO);
                    contentVO.setUser(user);
                    return contentVO;
                }).toList();

        PageVO<ContentVO> pageVO = new PageVO<>();
        pageVO.setCurrent(contentPage.getCurrent());
        pageVO.setSize(contentPage.getSize());
        pageVO.setTotal(contentPage.getTotal());
        pageVO.setRecords(contentVOList);

        return pageVO;
    }

}
