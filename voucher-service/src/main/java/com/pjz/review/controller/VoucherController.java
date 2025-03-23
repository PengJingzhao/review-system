package com.pjz.review.controller;

import com.pjz.commons.result.CommonResult;
import com.pjz.review.entity.bo.VoucherBO;
import com.pjz.review.service.VoucherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/voucher")
public class VoucherController {

    @Resource
    private VoucherService voucherService;


    @PostMapping("/seckill")
    public CommonResult<Long> addSecKillVoucher(@RequestBody VoucherBO voucherBO) {
        return CommonResult.success(voucherService.addSecKillVoucher(voucherBO));
    }

    @PostMapping("/secKillVoucher/{id}")
    public CommonResult<Long> secKillVoucherById(@PathVariable Long id) {
        return CommonResult.success(voucherService.secKillVoucher(id));
    }

}
