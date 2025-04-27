package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.pjz.review.common.entity.po.Voucher;
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
public interface VoucherMapper extends BaseMapper<Voucher> {

    default void add(Voucher voucher) {
        insert(voucher);
    }
}
