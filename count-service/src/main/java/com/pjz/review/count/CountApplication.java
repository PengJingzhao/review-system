package com.pjz.review.count;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class CountApplication {
    public static void main(String[] args) {
        SpringApplication.run(CountApplication.class, args);
    }
}
