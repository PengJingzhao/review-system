package com.pjz.review.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.pjz.review.entity.RedisData;
import com.pjz.review.entity.Shops;
import com.pjz.review.mapper.ShopsMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.pjz.review.utils.RedisConstants.*;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopsMapper shopsMapper;

    public boolean tryLock(String key) {

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, LOCK_VALUE, LOCK_TTL, TimeUnit.SECONDS);

        return BooleanUtil.isTrue(flag);
    }

    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    public void saveShop2Redis(Long id, Long expireSeconds) {
        Shops shops = shopsMapper.getById(id);

        RedisData redisData = new RedisData();
        redisData.setData(shops);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));

        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }

}
