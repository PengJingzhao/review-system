package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.Follower;
import org.apache.ibatis.annotations.Mapper;

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
}
