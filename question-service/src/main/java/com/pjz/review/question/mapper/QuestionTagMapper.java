package com.pjz.review.question.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.po.QuestionTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionTagMapper extends BaseMapper<QuestionTag> {
    // 自定义标签相关查询可以写这里
}
