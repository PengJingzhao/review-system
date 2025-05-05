package com.pjz.commons.cache;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface CacheManager<T> {


    void put(String key, String value);

    void put(String key, String value, int expire, TimeUnit timeUnit);

    void put(String key, T value, int expire, TimeUnit timeUnit);

    void putList(String key, Collection<T> value, int expire, TimeUnit timeUnit);

    String get(String key);

    T get(String key, Class<T> clazz);

    List<T> getList(String key, Class<T> clazz,long start,long end);

    void remove(String key);


}
