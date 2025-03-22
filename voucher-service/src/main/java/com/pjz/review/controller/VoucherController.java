package com.pjz.review.controller;

import com.pjz.commons.result.CommonResult;
import com.pjz.review.entity.bo.VoucherBO;
import com.pjz.review.service.VoucherService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
