package com.pjz.review.service;

import com.pjz.review.entity.vo.ShopVO;

import java.util.List;

public interface ShopService {


    ShopVO queryById(Long id);

    Void updateById(ShopVO shopVO);
}
