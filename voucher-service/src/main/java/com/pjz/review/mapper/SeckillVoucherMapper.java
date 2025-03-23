package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

    default SeckillVoucher getById(Long id) {
        LambdaQueryWrapper<SeckillVoucher> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SeckillVoucher::getBeginTime, SeckillVoucher::getEndTime, SeckillVoucher::getStock)
                .eq(SeckillVoucher::getVoucherId, id);
        return selectOne(wrapper);
    }

    default boolean decrStockById(Long id) {
        LambdaUpdateWrapper<SeckillVoucher> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SeckillVoucher::getVoucherId, id)
                .setSql("stock = stock - 1");
        return update(null, wrapper) == 1;
    }
}
