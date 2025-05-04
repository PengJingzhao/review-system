package com.pjz.commons.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class StringRedisCacheManager<T> implements CacheManager<T> {

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public void put(String key, String value) {

    }

    @Override
    public void put(String key, String value, int expire, TimeUnit timeUnit) {

    }

    @Override
    public void put(String key, T value, int expire, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(value),
                    expire, timeUnit
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String key) {
        return "";
    }

    @Override
    public T get(String key, Class<T> clazz) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (StringUtils.hasText(json)) {
                return objectMapper.readValue(json, clazz);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void remove(String key) {

    }
}
