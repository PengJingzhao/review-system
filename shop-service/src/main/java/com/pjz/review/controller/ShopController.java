package com.pjz.review.controller;

import com.pjz.commons.result.CommonResult;
import com.pjz.review.entity.vo.ShopVO;
import com.pjz.review.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Resource
    private ShopService shopService;

    @GetMapping("/{id}")
    public CommonResult<ShopVO> queryShopById(@PathVariable("id") Long id) {
        return CommonResult.success(shopService.queryById(id));
    }

    @PostMapping
    public CommonResult<Void> updateShopById(@RequestBody ShopVO shopVO) {
        return CommonResult.success(shopService.updateById(shopVO));
    }

}
