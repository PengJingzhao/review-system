package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.po.User;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pjz
 * @since 2025-03-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    default User getUserByPhone(String phone) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return selectOne(wrapper);
    }

    default User addUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setCreatedAt(LocalDateTime.now());
        user.setName("user_" + UUID.randomUUID());
        insert(user);
        return user;
    }

    default User getUserById(Long userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userId);
        return selectOne(wrapper);
    }

    default void incAttentionCnt(Integer userId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
                .setSql("attention_count = attention_count + 1");
        update(null, wrapper);
    }

    default void incFollowerCnt(Integer attentionId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, attentionId)
                .setSql("follower_count = follower_count + 1");
        update(null, wrapper);
    }
}
