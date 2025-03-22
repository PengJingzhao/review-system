package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.entity.SeckillVoucher;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pjz
 * @since 2025-03-22
 */
@Mapper
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {

    default void add(SeckillVoucher seckillVoucher) {
        insert(seckillVoucher);
    }
}
