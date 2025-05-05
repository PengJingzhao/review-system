package com.pjz.commons.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public void putList(String key, Collection<T> value, int expire, TimeUnit timeUnit) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
            return;
        }

        List<String> list = value.stream()
                .filter(Objects::nonNull)
                .map(t -> {
                    try {
                        return objectMapper.writeValueAsString(t);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(key, list);

        if (expire > 0) {
            stringRedisTemplate.expire(key, expire, timeUnit);
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
    public List<T> getList(String key, Class<T> clazz, long start, long end) {

        if (key == null || key.isEmpty()) {
            return Collections.emptyList();
        }

        boolean getAll = start < 0 || end < 0 || end < start;

        List<String> list;

        if (getAll) {
            // 取全部元素
            list = stringRedisTemplate.opsForList().range(key, 0, -1);
        } else {
            // 取指定范围
            list = stringRedisTemplate.opsForList().range(key, start, end);
        }

        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(json -> {
                    System.out.println(json);
                    try {
                        System.out.println(objectMapper.readValue(json, clazz));
                        return objectMapper.readValue(json, clazz);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void remove(String key) {

    }
}
