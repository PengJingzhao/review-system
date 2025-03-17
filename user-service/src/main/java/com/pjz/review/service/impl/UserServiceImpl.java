package com.pjz.review.service.impl;

import com.pjz.commons.utils.ValidatorUtil;
import com.pjz.review.entity.User;
import com.pjz.review.entity.dto.LoginFormDTO;
import com.pjz.review.entity.vo.UserVO;
import com.pjz.review.mapper.UserMapper;
import com.pjz.review.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

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
    public UserVO login(LoginFormDTO loginFormDTO, HttpSession session) {

        // 检验手机号
        Assert.isTrue(ValidatorUtil.isValidPhone(loginFormDTO.getPhone()), "手机号格式错误");

        // 校验验证码
        Object cacheCode = session.getAttribute("code");
        String code = loginFormDTO.getCode();
        Assert.isTrue(cacheCode != null && cacheCode.toString().equals(code), "验证码错误");

        // 校验用户是否存在
        User user = userMapper.getUserByPhone(loginFormDTO.getPhone());
        if (Objects.isNull(user)) {
            // 创建新用户
            user = userMapper.addUserWithPhone(loginFormDTO.getPhone());
        }

        // 将用户缓存起来
        session.setAttribute("user", user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }
}
