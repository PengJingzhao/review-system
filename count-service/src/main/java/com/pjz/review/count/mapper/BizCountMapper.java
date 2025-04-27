package com.pjz.review.count.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pjz.review.common.entity.po.BizCount;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BizCountMapper extends BaseMapper<BizCount> {

    // 通过业务类型，业务ID和计数类型查询计数记录
    default BizCount selectByBizKey(String bizType, String bizId, String countType) {
        LambdaQueryWrapper<BizCount> query = new LambdaQueryWrapper<>();
        query.eq(BizCount::getBizType, bizType)
                .eq(BizCount::getBizId, bizId)
                .eq(BizCount::getCountType, countType);
        return selectOne(query);
    }

    // 查询某业务对象全部计数
    default List<BizCount> selectByBiz(String bizType, String bizId) {
        LambdaQueryWrapper<BizCount> query = new LambdaQueryWrapper<>();
        query.eq(BizCount::getBizType, bizType)
                .eq(BizCount::getBizId, bizId);
        return selectList(query);
    }

    // 原子增加计数
    default int incrementCountValue(String bizType, String bizId, String countType, long delta) {
        LambdaUpdateWrapper<BizCount> update = new LambdaUpdateWrapper<>();
        update.setSql("count_value = count_value + " + delta)
                .set(BizCount::getUpdateTime, java.time.LocalDateTime.now())
                .eq(BizCount::getBizType, bizType)
                .eq(BizCount::getBizId, bizId)
                .eq(BizCount::getCountType, countType);
        return update(null, update);
    }

    // 重置计数
    default int resetCountValue(String bizType, String bizId, String countType, long value) {
        LambdaUpdateWrapper<BizCount> update = new LambdaUpdateWrapper<>();
        update.set(BizCount::getCountValue, value)
                .set(BizCount::getUpdateTime, java.time.LocalDateTime.now())
                .eq(BizCount::getBizType, bizType)
                .eq(BizCount::getBizId, bizId)
                .eq(BizCount::getCountType, countType);
        return update(null, update);
    }
}
