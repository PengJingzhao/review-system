package com.pjz.review.service.impl;

import com.pjz.commons.utils.ValidatorUtil;
import com.pjz.review.entity.dto.LoginFormDTO;
import com.pjz.review.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpSession;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Override
    public String sendCode(String phone, HttpSession session) {

        // 校验传入的手机号是否正确
        Assert.isTrue(ValidatorUtil.isValidPhone(phone), "手机号格式错误");

        // todo 生成验证码
        String code = "123456";

        // 缓存验证码
        session.setAttribute("code", code);

        log.info("生成验证码：{}", code);

        return code;
    }

    @Override
    public String login(LoginFormDTO loginFormDTO, HttpSession session) {

        // 检验手机号
        Assert.isTrue(ValidatorUtil.isValidPhone(loginFormDTO.getPhone()), "手机号格式错误");

        // 校验验证码


        // 校验用户是否存在

        return "";
    }
}
