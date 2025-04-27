package com.pjz.review.service.impl;

import cn.hutool.json.JSONUtil;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.common.service.RelationService;
import com.pjz.review.mapper.AttentionMapper;
import com.pjz.review.mapper.FollowerMapper;
import com.pjz.review.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Service
@DubboService
public class RelationServiceImpl implements RelationService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private FollowerMapper followerMapper;

    @Resource
    private AttentionMapper attentionMapper;


    @Override
    public List<UserVO> getFollowerList(Long userId, Integer type, Integer start, Integer stop) {
        // 校验当前用户是否有权查询

        // 区分是查询本人的还是查询其他用户的
        if (type == 0) {
            // 查询其他用户的
            return other(userId, 0, start, stop);
        } else {
            // 查询本人的
            return oneself(userId, 0, start, stop);
        }
    }

    @Override
    public List<Integer> getAttentionList(Long userId) {


        List<Integer> followedUserIds = attentionMapper.getFollowedUserIds(userId);

        return followedUserIds;
    }

    @Override
    public boolean isAttention(Long userId, Long attentionId) {

        return attentionMapper.isAttention(userId,attentionId);
    }

    private List<UserVO> oneself(Long userId, int type, Integer start, Integer stop) {
        if (type == 0) {
            // 并发量小，可以直接从数据库里面查询
            List<Integer> userIdList = followerMapper.getFollowerIds(userId, start, stop);
            List<String> userIds = userIdList.stream().map(Object::toString).toList();
            List<String> users = stringRedisTemplate.opsForValue().multiGet(userIds);
            if (users == null || users.isEmpty()) {
                return Collections.emptyList();
            }
            // 转换成UserVo
            return users.stream().map(s -> JSONUtil.toBean(s, UserVO.class)).toList();
        } else {
            return Collections.emptyList();
        }
    }

    private List<UserVO> other(Long userId, Integer type, Integer start, Integer stop) {
        if (type == 0) {
            // 先到缓存里面查询用户id列表
            List<String> userIds = stringRedisTemplate.opsForList().range("user:follower:list" + userId, start, stop);
            // 判断userId的缓存是否命中
            if (userIds == null || userIds.isEmpty()) {
                // 到数据库里面去查
                List<Integer> userIdList = followerMapper.getFollowerIds(userId, start, stop);
                userIds = userIdList.stream().map(Object::toString).toList();
                // 回写redis缓存
                stringRedisTemplate.opsForList().leftPushAll("user:follower:list" + userId, userIds);
            }
            // 缓存中查出用户信息
            List<String> users = stringRedisTemplate.opsForValue().multiGet(userIds);
            if (users == null || users.isEmpty()) {
                return Collections.emptyList();
            }
            // 转换成UserVo
            return users.stream().map(s -> JSONUtil.toBean(s, UserVO.class)).toList();
        } else {
            return Collections.emptyList();
        }
    }
}
