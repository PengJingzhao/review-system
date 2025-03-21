package com.pjz.review.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisWorker {

    private static final long BEGIN_TIMESTAMP = 1735689600L;

    private static final int COUNT_BITS = 32;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public Long nextId(String keyPrefix) {

        // 生成时间戳：当前时间-开始时间（选择一个时间作为计算的基准）
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;


        // 生成序列号
        // 使用redis进行自增长，并且使用日期作为key，便于进行归档，避免key过大，而且有利于一时间维度去统计全局id
        // 以天为单位进行归档
        String dateKey = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + dateKey);

        // 依次拼接符号位+时间戳+自增数 得到一个完整的全局唯一id
        // 拼接成全局唯一递增安全id
        // 由于要得到一个long类型的id，所以要借助位运算，而不是简单的字符串拼接


        return timestamp << COUNT_BITS | count;
    }

}
