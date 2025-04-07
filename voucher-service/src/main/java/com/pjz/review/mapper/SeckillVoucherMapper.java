package com.pjz.review.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.pjz.review.common.entity.SeckillVoucher;
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
        wrapper.select(SeckillVoucher::getBeginTime, SeckillVoucher::getEndTime, SeckillVoucher::getStock, SeckillVoucher::getVoucherId)
                .eq(SeckillVoucher::getVoucherId, id);
        return selectOne(wrapper);
    }

    default boolean decrStockById(Long id, Integer stock) {
        LambdaUpdateWrapper<SeckillVoucher> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SeckillVoucher::getVoucherId, id)
                // 只有在stock的现值与一开始查询到的原值相等，即stock没有被其他线程修改时，才会去更新数据，
                // 否则就不更新然后退出重试，
                // 体现了cas的思想，确保了线程安全
                // 但是cas乐观锁有一个弊端就是凡是修改过了的数据都无法继续操作，只能退出重试，这样就会导致业务的成功率大大降低
//                .eq(SeckillVoucher::getStock, stock)
                .gt(SeckillVoucher::getStock, 0)
                .setSql("stock = stock - 1");
        return update(null, wrapper) == 1;
    }
}
