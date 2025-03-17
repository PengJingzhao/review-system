package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.entity.User;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Arrays;

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
        user.setName("user_"+ Arrays.toString(RandomUtils.nextBytes(10)));
        insert(user);
        return user;
    }
}
