package com.pjz.commons.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;


public class CaffeineCacheManager<T> implements CacheManager<T> {

    private static final Cache<String, String> cache;

    static {
        cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    @Override
    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void put(String key, String value, int expire, TimeUnit timeUnit) {

    }

    @Override
    public void put(String key, Object value, int expire, TimeUnit timeUnit) {

    }

    @Override
    public String get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public T get(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public void remove(String key) {
        cache.invalidate(key);
    }

}
