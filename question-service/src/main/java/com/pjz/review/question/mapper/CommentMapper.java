package com.pjz.review.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.pjz.review.common.entity.po.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}