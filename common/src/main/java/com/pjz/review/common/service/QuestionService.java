package com.pjz.review.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pjz.review.common.entity.dto.QuestionCreateRequest;
import com.pjz.review.common.entity.dto.QuestionPageRequest;
import com.pjz.review.common.entity.po.Question;
import com.pjz.review.common.entity.po.QuestionTag;
import com.pjz.review.common.entity.po.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/question")
public interface QuestionService {

    /**
     * 创建一道面试题
     *
     * @param createRequest 创建请求
     * @return 新建的题目实体（带ID）
     */
    @PostMapping("/createQuestion")
    Question createQuestion(@Validated @RequestBody QuestionCreateRequest createRequest);

    @PostMapping("/pageQuestionList")
    IPage<Question> pageQuestionList(@RequestBody QuestionPageRequest params);

    List<Question> getRecommendQuestions(Long userId);

    @GetMapping("/getQuestionDetail/{id}")
    Question getQuestionDetail(@PathVariable("id") Integer id) throws JsonProcessingException;

    @GetMapping("/getTags")
    List<Tag> getTags();

    @GetMapping("/getByTag/{tagId}")
    IPage<Question> getQuestionByTag(@PathVariable("tagId") Long tagId, @RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/getNext")
    Long getNextQuestionId(@RequestParam("currentId") Long currentId, @RequestParam("tagId") Long tagId);

    @GetMapping("/getPrev")
    Long getPrevQuestionId(@RequestParam("currentId") Long currentId, @RequestParam("tagId") Long tagId);

    @PostMapping("/like/{questionId}")
    Long like(@PathVariable("questionId") Long questionId, @RequestHeader("authorization") String token);

    void insertQuestion(QuestionCreateRequest createRequest, Question question);


}
