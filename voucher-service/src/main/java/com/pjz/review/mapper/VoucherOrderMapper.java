package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.entity.VoucherOrder;
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
}
