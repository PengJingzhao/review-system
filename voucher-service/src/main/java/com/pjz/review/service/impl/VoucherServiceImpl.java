package com.pjz.review.service.impl;


import com.pjz.review.common.entity.po.SeckillVoucher;
import com.pjz.review.common.entity.po.Voucher;
import com.pjz.review.common.entity.po.VoucherOrder;
import com.pjz.review.common.entity.bo.VoucherBO;
import com.pjz.review.common.service.VoucherService;
import com.pjz.review.mapper.SeckillVoucherMapper;
import com.pjz.review.mapper.VoucherMapper;
import com.pjz.review.mapper.VoucherOrderMapper;
import com.pjz.review.util.RedisWorker;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@DubboService
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

        Long userId = 4L;

        // 由于事务之内是等到方法调用结束之后再提交
        // 那么在锁释放之后和事务提交之前，数据库的数据还是旧的
        // 就会导致线程安全问题
        // 如果不使用代理对象进行调用的话就会导致事务失效
        // 一人一单，需要判断当前用户是否已经购买过该优惠券
        // 若购买过，则不允许再次购买
        synchronized (userId.toString().intern()) {
            VoucherService proxy = (VoucherService) AopContext.currentProxy();
            return proxy.createVoucherOrder(id, userId, seckillVoucher);
        }
    }


    /**
     * 使用arthas调试发现的执行耗时最长的方法，是当前调用链路的性能瓶颈
     *
     * @param id
     * @param userId
     * @param seckillVoucher
     * @return
     */
    @Transactional
    public Long createVoucherOrder(Long id, Long userId, SeckillVoucher seckillVoucher) {
        // 悲观锁保证线程安全
        // 业务流程就是到订单表中查询，该用户的订单，看是否有该优惠券的订单
        Long count = voucherOrderMapper.getCountByVoucherId(userId, seckillVoucher.getVoucherId());
        Assert.isTrue(count <= 0, "已经购买了该优惠券");

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
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(id);
        voucherOrder.setId(orderId);
        voucherOrderMapper.add(voucherOrder);
        return orderId;
    }
}
