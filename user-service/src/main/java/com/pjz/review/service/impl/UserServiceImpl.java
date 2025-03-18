package com.pjz.review.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.pjz.commons.utils.ValidatorUtil;
import com.pjz.review.entity.User;
import com.pjz.review.entity.dto.LoginFormDTO;
import com.pjz.review.entity.vo.UserVO;
import com.pjz.review.mapper.UserMapper;
import com.pjz.review.service.UserService;
import com.pjz.review.util.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.pjz.review.util.ExceptionConstants.CODE_NOT_CORRECT;
import static com.pjz.review.util.ExceptionConstants.PHONE_FORMAT_NOT_CORRECT;
import static com.pjz.review.util.RedisConstants.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String sendCode(String phone, HttpSession session) {

        // 校验传入的手机号是否正确
        Assert.isTrue(ValidatorUtil.isValidPhone(phone), PHONE_FORMAT_NOT_CORRECT);

        // todo 生成验证码
        String code = "123456";

        // 缓存验证码
//        session.setAttribute("code", code);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        log.info("生成验证码：{}", code);

        return code;
    }

    @Override
    public String login(LoginFormDTO loginFormDTO, HttpSession session) {

        // 检验手机号
        Assert.isTrue(ValidatorUtil.isValidPhone(loginFormDTO.getPhone()), PHONE_FORMAT_NOT_CORRECT);

        // 校验验证码
//        Object cacheCode = session.getAttribute("code");
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + loginFormDTO.getPhone());
        String code = loginFormDTO.getCode();
        Assert.isTrue(Objects.nonNull(cacheCode) && cacheCode.equals(code), CODE_NOT_CORRECT);

        // 校验用户是否存在
        User user = userMapper.getUserByPhone(loginFormDTO.getPhone());
        if (Objects.isNull(user)) {
            // 创建新用户
            user = userMapper.addUserWithPhone(loginFormDTO.getPhone());
        }

        // 将用户缓存起来,缓存到远程缓存redis，并且采用hash的格式,随机生成一个随机值作为缓存的key
//        session.setAttribute("user", user);
        String token = UUID.randomUUID().toString();

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        Map<String, Object> userVOMap = BeanUtil.beanToMap(userVO);

        String userVOKey = LOGIN_TOKEN_KEY + token;

        stringRedisTemplate.opsForHash().putAll(userVOKey, userVOMap);

        // 设置缓存过期时间
        stringRedisTemplate.expire(userVOKey, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        return token;
    }
}
