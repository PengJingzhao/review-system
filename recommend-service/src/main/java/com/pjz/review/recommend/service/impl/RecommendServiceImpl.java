package com.pjz.review.recommend.service.impl;

import com.pjz.commons.cache.CacheManager;
import com.pjz.review.common.entity.po.Question;
import com.pjz.review.common.service.QuestionService;
import com.pjz.review.common.service.RecommendService;
import com.pjz.review.common.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@DubboService
@Slf4j
public class RecommendServiceImpl implements RecommendService {

    @DubboReference
    private UserService userService;

    @DubboReference
    private QuestionService questionService;

    @Resource
    private CacheManager<Question> questionCache;

    @Override
    public List<Question> getRecommendQuestions(String token) {

        Long userId = userService.getUserId(token);
        log.info("getRecommendQuestions userId = {}", userId);

        String cacheKey = "recommend:userId:" + userId;

        // 从缓存中查询当前用户的推荐列表
        List<Question> recommendList = questionCache.getList(cacheKey, Question.class, -1, -1);
        if (recommendList != null && !recommendList.isEmpty()) {
            return recommendList;
        }

        // 从数据库构建当前用户的推荐列表
        recommendList = questionService.getRecommendQuestions(userId);

        // 写回缓存
        int totalExpire = 600 + ThreadLocalRandom.current().nextInt(0, 200);
        questionCache.putList(cacheKey, recommendList, totalExpire, TimeUnit.SECONDS);

        return recommendList;
    }
}
