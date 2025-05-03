package com.pjz.review.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.po.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    // 如果需要自定义复杂SQL，可在这里写自定义方法
}
