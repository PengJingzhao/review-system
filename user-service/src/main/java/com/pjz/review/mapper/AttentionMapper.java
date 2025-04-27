package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import com.pjz.review.common.entity.Attention;

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
public interface AttentionMapper extends BaseMapper<Attention> {

    default void addAttention(Attention attention) {
        insert(attention);
    }

    default List<Integer> getFollowedUserIds(Integer userId) {
        LambdaQueryWrapper<Attention> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Attention::getAttentionId)   // 只查询followedUserId字段
                .eq(Attention::getUserId, userId);   // 条件 user_id = ?
        // selectObjs 返回 List<Object>，可以强制转成 List<Integer>
        List<Object> objList = this.selectObjs(queryWrapper);
        // 转成List<Integer>
        return objList.stream().map(o -> (Integer) o).toList();
    }
}
