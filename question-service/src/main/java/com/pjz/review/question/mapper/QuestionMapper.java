package com.pjz.review.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pjz.review.common.entity.po.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    List<Question> selectQuestionsByTagsPage(@Param("page") Page<?> page,
                                              @Param("offset") Integer offset,
                                              @Param("size") Integer size,
                                              @Param("difficulty") Integer difficulty,
                                              @Param("keyword") String keyword,
                                              @Param("tagIds") List<Long> tagIds);
}
