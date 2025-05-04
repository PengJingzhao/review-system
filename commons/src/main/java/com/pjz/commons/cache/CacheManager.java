package com.pjz.commons.cache;

import java.util.concurrent.TimeUnit;

public interface CacheManager<T> {


    void put(String key, String value);

    void put(String key, String value, int expire, TimeUnit timeUnit);

    void put(String key, T value, int expire, TimeUnit timeUnit);

    String get(String key);

    T get(String key, Class<T> clazz);

    void remove(String key);


}
