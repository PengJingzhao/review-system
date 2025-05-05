package com.pjz.review.common.service;

import com.pjz.review.common.entity.po.Question;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/recommend")
public interface RecommendService {

    @GetMapping("/questions")
    List<Question> getRecommendQuestions(@RequestHeader("authorization") String token);

}
