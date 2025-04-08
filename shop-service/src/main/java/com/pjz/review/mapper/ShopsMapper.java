package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.Shops;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pjz
 * @since 2025-03-18
 */
@Mapper
public interface ShopsMapper extends BaseMapper<Shops> {

    default Shops getById(Long id) {
        LambdaQueryWrapper<Shops> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shops::getId, id);
        return selectOne(wrapper);
    }
}
