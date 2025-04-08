package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.pjz.review.common.entity.Attention;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjz
 * @since 2025-04-08
 */
@Mapper
public interface AttentionMapper extends BaseMapper<Attention> {

    default void addAttention(Attention attention){
        insert(attention);
    }
}
