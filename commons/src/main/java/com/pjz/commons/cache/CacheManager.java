package com.pjz.commons.cache;

public interface CacheManager {


    void put(String key, String value);

    String get(String key);

    void remove(String key);



}
