package com.pjz.review.service.impl;

import com.pjz.review.entity.SeckillVoucher;
import com.pjz.review.entity.Voucher;
import com.pjz.review.entity.bo.VoucherBO;
import com.pjz.review.mapper.SeckillVoucherMapper;
import com.pjz.review.mapper.VoucherMapper;
import com.pjz.review.service.VoucherService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Resource
    private VoucherMapper voucherMapper;

    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;

    @Override
    public Long addSecKillVoucher(VoucherBO voucherBO) {

        // 将优惠券插入数据库
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherBO, voucher);

        voucherMapper.add(voucher);

        // 校验优惠券id是否存在
        Long voucherId = voucher.getId();
        Assert.notNull(voucherId, "优惠券id不能为空");

        // 将秒杀券插入数据库
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        BeanUtils.copyProperties(voucherBO, seckillVoucher);

        // 记得要将相应的优惠券的id写回秒杀券
        seckillVoucher.setVoucherId(voucherId);

        seckillVoucherMapper.add(seckillVoucher);

        return voucherId;
    }
}
