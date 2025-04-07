package com.pjz.review.common.service;


import com.pjz.review.common.entity.vo.ShopVO;

public interface ShopService {


    ShopVO queryById(Long id);

    Void updateById(ShopVO shopVO);
}
