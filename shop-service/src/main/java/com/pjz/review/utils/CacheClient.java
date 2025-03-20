package com.pjz.review.utils;

import cn.hutool.json.JSONUtil;
import com.pjz.review.entity.RedisData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.pjz.review.utils.RedisConstants.CACHE_SHOP_KEY;

@Slf4j
@Component
public class CacheClient {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, Object value, Long time, TimeUnit timeUnit) {

        // 将任意对象序列化成string字符串，然后再缓存到redis中
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
    }

    public void setWithLogicExpire(String key, Object value, Long time, TimeUnit timeUnit) {
        // 设置逻辑过期字段
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));

        // 将数据写入到redis中
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit timeUnit) {
        String key = keyPrefix + id;

        String json = stringRedisTemplate.opsForValue().get(key);

        // 判断缓存是否命中
        if (StringUtils.hasText(json)) {
            // 缓存命中，将json反序列化出对象
            return JSONUtil.toBean(json, type);
        }

        // 判断是否命中空对象
        if (Objects.nonNull(json)) {
            // 命中空对象
            return null;
        }

        // 既没有命中有效数据，也没有命中空对象，需要将数据库中的新数据更新到缓存中
        // 具体的数据库交互逻辑，交由调用者通过函数式编程来进行指定
        R r = dbFallback.apply(id);

        // 如果在数据库没有找到相应的数据，为了避免缓存穿透问题，应该要将空对象回写缓存中
        this.set(key, r, time, timeUnit);

        return r;

    }
}
