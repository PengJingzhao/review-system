package com.pjz.review.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import com.pjz.review.common.entity.po.Content;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pjz
 * @since 2025-04-24
 */
@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    default Page<Content> selectByUserId(Integer userId, Long current, Long size) {
        long pageNum = (current != null && current > 0) ? current : 1;
        long pageSize = (size != null && size > 0) ? size : 10;

        Page<Content> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getUserId, userId)
                .orderByDesc(Content::getCreatedAt);

        return this.selectPage(page, queryWrapper);
    }

    default Page<Content> selectFeedByUserId(Integer userId, Long current, Long size, List<Integer> followedUserIds) {
        long pageNum = (current != null && current > 0) ? current : 1;
        long pageSize = (size != null && size > 0) ? size : 10;

        Page<Content> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Content::getUserId, followedUserIds)
                .orderByDesc(Content::getCreatedAt);

        return this.selectPage(page, queryWrapper);
    }

    default Content getContentById(Long contentId) {
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Content::getId, contentId);
        return selectOne(wrapper);
    }
}
