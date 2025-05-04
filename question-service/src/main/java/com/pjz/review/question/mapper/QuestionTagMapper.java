package com.pjz.review.question.mapper;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pjz.review.common.entity.po.Question;
import com.pjz.review.common.entity.po.QuestionTag;
import com.pjz.review.common.entity.po.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.function.Function;

@Mapper
public interface QuestionTagMapper extends BaseMapper<QuestionTag> {
    default List<Long> getQuestionsByTagId(Long tagId) {
        LambdaQueryWrapper<QuestionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(QuestionTag::getQuestionId)
                .eq(QuestionTag::getTagId, tagId);
        return selectObjs(wrapper).stream().map(o -> (Long) o).toList();
    }

    int insertBatch(@Param("list") List<QuestionTag> questionTags);
}
