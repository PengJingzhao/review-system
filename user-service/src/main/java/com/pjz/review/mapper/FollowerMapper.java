package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pjz.review.common.entity.po.Follower;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pjz
 * @since 2025-04-08
 */
@Mapper
public interface FollowerMapper extends BaseMapper<Follower> {

    default void addFollower(Follower follower) {
        insert(follower);
    }

    default List<Integer> getFollowerIds(Long userId,Integer start,Integer stop) {
        Page<Follower> page = Page.of(start, stop - start);

        LambdaQueryWrapper<Follower> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(Follower::getFollowerId)
                .eq(Follower::getUserId, userId);

        return selectPage(page, wrapper).getRecords().stream().map(Follower::getFollowerId).toList();
    }
}
