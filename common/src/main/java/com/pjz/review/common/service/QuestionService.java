package com.pjz.review.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pjz.review.common.entity.dto.QuestionCreateRequest;
import com.pjz.review.common.entity.dto.QuestionPageRequest;
import com.pjz.review.common.entity.po.Question;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/question")
public interface QuestionService {

    /**
     * 创建一道面试题
     * @param createRequest 创建请求
     * @return 新建的题目实体（带ID）
     */
    @PostMapping("/createQuestion")
    Question createQuestion(@Validated @RequestBody QuestionCreateRequest createRequest);

    @PostMapping("/pageQuestionList")
    IPage<Question> pageQuestionList(@RequestBody QuestionPageRequest params);

    @GetMapping("/getQuestionDetail/{id}")
    Question getQuestionDetail(@PathVariable("id") Integer id);

}
