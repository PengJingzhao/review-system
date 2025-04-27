package com.pjz.review.count.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pjz.review.common.entity.po.BizCount;
import com.pjz.review.common.service.BizCountService;
import com.pjz.review.count.mapper.BizCountMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@DubboService
public class BizCountServiceImpl extends ServiceImpl<BizCountMapper, BizCount> implements BizCountService {

    @Override
    public BizCount getCount(String bizType, String bizId, String countType) {
        return baseMapper.selectByBizKey(bizType, bizId, countType);
    }

    @Override
    public List<BizCount> getAllCounts(String bizType, String bizId) {
        return baseMapper.selectByBiz(bizType, bizId);
    }

    @Override
    @Transactional
    public void addCount(BizCount record) {
        save(record);
    }

    @Override
    @Transactional
    public void incrementCount(String bizType, String bizId, String countType, long delta) {
        // 尝试增加计数，如果不存在可考虑事先插入初始数据
        int rows = baseMapper.incrementCountValue(bizType, bizId, countType, delta);
        if (rows == 0) {
            // 计数未初始化，插入一条初始数据后再增
            BizCount newCount = new BizCount();
            newCount.setBizType(bizType);
            newCount.setBizId(bizId);
            newCount.setCountType(countType);
            newCount.setCountValue(delta > 0 ? delta : 0);
            newCount.setUpdateTime(java.time.LocalDateTime.now());
            save(newCount);
        }
    }

    @Override
    @Transactional
    public void resetCount(String bizType, String bizId, String countType, long value) {
        int rows = baseMapper.resetCountValue(bizType, bizId, countType, value);
        if (rows == 0) {
            BizCount newCount = new BizCount();
            newCount.setBizType(bizType);
            newCount.setBizId(bizId);
            newCount.setCountType(countType);
            newCount.setCountValue(value);
            newCount.setUpdateTime(java.time.LocalDateTime.now());
            save(newCount);
        }
    }
}