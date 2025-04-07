package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.pjz.review.common.entity.VoucherOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pjz
 * @since 2025-03-23
 */
@Mapper
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {

    default void add(VoucherOrder voucherOrder) {
        insert(voucherOrder);
    }

    default Long getCountByVoucherId(Long userId, Long voucherId){
        LambdaQueryWrapper<VoucherOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoucherOrder::getUserId,userId)
                .eq(VoucherOrder::getVoucherId,voucherId);
        return selectCount(wrapper);
    }
}
