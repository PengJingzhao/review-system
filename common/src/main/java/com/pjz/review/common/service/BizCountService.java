package com.pjz.review.common.service;

import com.pjz.review.common.entity.po.BizCount;

import java.util.List;

public interface BizCountService {

    BizCount getCount(String bizType, String bizId, String countType);

    List<BizCount> getAllCounts(String bizType, String bizId);

    void addCount(BizCount record);

    void incrementCount(String bizType, String bizId, String countType, long delta);

    void resetCount(String bizType, String bizId, String countType, long value);
}