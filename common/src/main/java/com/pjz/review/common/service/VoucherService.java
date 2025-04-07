package com.pjz.review.common.service;


import com.pjz.review.common.entity.SeckillVoucher;
import com.pjz.review.common.entity.bo.VoucherBO;

public interface VoucherService {
    Long addSecKillVoucher(VoucherBO voucherBO);

    Long secKillVoucher(Long id);

    Long createVoucherOrder(Long id, Long userId, SeckillVoucher seckillVoucher);
}
