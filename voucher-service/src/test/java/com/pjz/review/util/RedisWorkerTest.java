package com.pjz.review.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class RedisWorkerTest {

    @Resource
    private RedisWorker redisWorker;

    @Test
    public void testNextId() {
        for (int i = 0; i < 100; i++) {
            Long id = redisWorker.nextId("order");
            log.info(Long.toBinaryString(id));
        }
    }

}
