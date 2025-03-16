package com.pjz.review.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestDeploy {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
