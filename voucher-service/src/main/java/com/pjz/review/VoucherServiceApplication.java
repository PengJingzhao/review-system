package com.pjz.review;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@EnableDubbo
public class VoucherServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoucherServiceApplication.class, args);
    }
}
