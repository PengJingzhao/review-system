package com.pjz.review.controller;

import com.pjz.commons.result.CommonResult;
import com.pjz.review.entity.dto.LoginFormDTO;
import com.pjz.review.entity.vo.UserVO;
import com.pjz.review.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("/code")
    public CommonResult<String> sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // 校验手机号，并生成和发送,缓存验证码
        return CommonResult.success(userService.sendCode(phone, session));
    }

    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody LoginFormDTO loginFormDTO, HttpSession session) {
        // 检验手机号，校验登录用户
        return CommonResult.success(userService.login(loginFormDTO, session));
    }

}
