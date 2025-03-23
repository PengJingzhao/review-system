package com.pjz.review.service.impl;

import com.pjz.review.entity.SeckillVoucher;
import com.pjz.review.entity.Voucher;
import com.pjz.review.entity.VoucherOrder;
import com.pjz.review.entity.bo.VoucherBO;
import com.pjz.review.mapper.SeckillVoucherMapper;
import com.pjz.review.mapper.VoucherMapper;
import com.pjz.review.mapper.VoucherOrderMapper;
import com.pjz.review.service.VoucherService;
import com.pjz.review.util.RedisWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Resource
    private VoucherMapper voucherMapper;

    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;

    @Resource
    private RedisWorker redisWorker;

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

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

    @Override
    public Long secKillVoucher(Long id) {

        // 校验当前优惠券是否处于秒杀有效期内
        SeckillVoucher seckillVoucher = seckillVoucherMapper.getById(id);

        Assert.notNull(seckillVoucher, "优惠券不存在");

        Assert.isTrue(!seckillVoucher.getBeginTime().isAfter(LocalDateTime.now()), "秒杀尚未开始");

        Assert.isTrue(!seckillVoucher.getEndTime().isBefore(LocalDateTime.now()), "秒杀已经结束");

        // 扣减库存
        // 判断库存是否充足
        Integer stock = seckillVoucher.getStock();
        Assert.isTrue(stock >= 1, "库存不足");
        boolean success = seckillVoucherMapper.decrStockById(id, stock);
        Assert.isTrue(success, "库存扣减失败");

        // 创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // todo 获得用户id，通过redis缓存的用户登录信息来获取用户id
        // 获得订单id
        Long orderId = redisWorker.nextId("order");
        voucherOrder.setUserId(1L);
        voucherOrder.setVoucherId(id);
        voucherOrder.setId(orderId);
        voucherOrderMapper.add(voucherOrder);

        return orderId;
    }
}
