package com.pjz.review.count.service.impl;

import com.pjz.review.common.service.CountService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@DubboService
public class CountServiceImpl implements CountService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void incrementReadCount(Integer postId) {

        // 更新缓存
        stringRedisTemplate.opsForHash().increment("counter:" + postId, "readCount", 1);

        // 异步线程定时刷新到db
    }
}
