package com.pjz.review.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pjz.commons.constants.CountConstants;
import com.pjz.commons.constants.UserContentRelationConstants;
import com.pjz.review.common.entity.po.BizCount;
import com.pjz.review.common.entity.po.UserContentRelation;
import com.pjz.review.common.entity.vo.ContentDetailVO;
import com.pjz.review.common.entity.vo.ContentVO;
import com.pjz.review.common.entity.vo.PageVO;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.common.service.*;
import com.pjz.review.content.mapper.ContentMapper;
import com.pjz.review.common.entity.po.Content;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@DubboService
@Slf4j
public class ContentServiceImpl implements ContentService {

    @DubboReference
    private UserService userService;

    @DubboReference
    private RelationService relationService;

    @DubboReference
    private BizCountService countService;

    @Resource
    private UserContentRelationService userContentRelationService;

    private final ContentMapper contentMapper;

    public ContentServiceImpl(ContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }

    @Override
    public Content createContent(Content content, String token) {
        content.setUserId(userService.getUser(token).getId())
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
        Long userId = user.getId();

        List<Integer> attentionList = relationService.getAttentionList(userId);

        Page<Content> contentPage = contentMapper.selectFeedByUserId(userId, current, size, attentionList);

        return getContentVOPageVO(user, contentPage);
    }

    @Override
    public ContentDetailVO getContentDetail(Long contentId, String token) {
        UserVO self = userService.getUser(token);

        Content content = contentMapper.getContentById(contentId);
        ContentDetailVO contentDetailVO = new ContentDetailVO();
        BeanUtils.copyProperties(content, contentDetailVO);

        UserVO user = userService.getUserById(content.getUserId());
        contentDetailVO.setUser(user);
        user.setAttention(relationService.isAttention(self.getId(), user.getId()));

        return contentDetailVO;
    }

    @Override
    @Transactional
    public Long like(Long contentId, String token) {

        Long userId = userService.getUser(token).getId();
        // 判断是否点过赞了
        if (userContentRelationService.relationExists(userId, contentId, UserContentRelationConstants.LIKE)) {
            // 取消点赞
            userContentRelationService.removeRelation(userId,contentId,UserContentRelationConstants.LIKE);

            // 计数服务增加点赞数
            countService.incrementCount(CountConstants.BLOG, String.valueOf(contentId), CountConstants.LIKE, -1);
            // 查询点赞数
            BizCount count = countService.getCount(CountConstants.BLOG, String.valueOf(contentId), CountConstants.LIKE);

            return count.getCountValue();
        }

        UserContentRelation relation = UserContentRelation.builder()
                .contentId(contentId)
                .userId(userId)
                .relationType(UserContentRelationConstants.LIKE)
                .build();

        userContentRelationService.addRelation(relation);

        // 计数服务增加点赞数
        countService.incrementCount(CountConstants.BLOG, String.valueOf(contentId), CountConstants.LIKE, 1);

        BizCount newCount = countService.getCount(CountConstants.BLOG, String.valueOf(contentId), CountConstants.LIKE);
        return newCount.getCountValue();
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
