package com.pjz.review.recommend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjz.commons.cache.CacheManager;
import com.pjz.commons.cache.StringRedisCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@Configuration
public class RedisCacheConfig {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public <T> CacheManager<T> cacheManager() {
        return new StringRedisCacheManager<>(stringRedisTemplate, objectMapper);
    }

}
