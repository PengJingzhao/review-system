package com.pjz.review.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSONUtil;
import com.pjz.commons.utils.ValidatorUtil;
import com.pjz.review.common.entity.po.User;
import com.pjz.review.common.entity.dto.LoginFormDTO;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.mapper.FollowerMapper;
import com.pjz.review.mapper.UserMapper;
import com.pjz.review.common.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static com.pjz.review.util.ExceptionConstants.CODE_NOT_CORRECT;
import static com.pjz.review.util.ExceptionConstants.PHONE_FORMAT_NOT_CORRECT;
import static com.pjz.review.util.RedisConstants.*;

import com.pjz.review.mapper.AttentionMapper;
import com.pjz.review.common.entity.po.Attention;
import com.pjz.review.common.entity.po.Follower;

@Slf4j
@Service
@DubboService
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AttentionMapper attentionMapper;

    @Resource
    private FollowerMapper followerMapper;

    private static final ConcurrentMap<String, UserVO> userVOMap = new ConcurrentHashMap<>();

    @Override
    public String sendCode(String phone) {

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
    public String login(LoginFormDTO loginFormDTO) {

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

        HashMap<String, Object> userVoMap = new HashMap<>();

        BeanUtil.beanToMap(userVO, userVoMap, new CopyOptions().setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue) -> fieldValue == null ? null : String.valueOf(fieldValue)));

        String userVOKey = LOGIN_TOKEN_KEY + token;

        stringRedisTemplate.opsForHash().putAll(userVOKey, userVoMap);

        // 设置缓存过期时间
        stringRedisTemplate.expire(userVOKey, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        return token;
    }

    @Override
    public UserVO getUser(String token) {
        System.out.println("user-service" + token);

        String userVOKey = LOGIN_TOKEN_KEY + token;

        String result = (String) stringRedisTemplate.opsForHash().get(userVOKey, "id");
        assert result != null;
        Integer userId = Integer.valueOf(result);
        System.out.println(Integer.valueOf(result));
        System.out.println(result);

        UserVO userVO;

        //本地缓存
        userVO = userVOMap.get("user:" + userId);
        if (userVO != null) {
            return userVO;
        }


        //远程缓存
        String userVOJson = stringRedisTemplate.opsForValue().get("user:" + userId);
        if (userVOJson != null && userVOJson.isEmpty()) {
            return null;
        }
        if (userVOJson != null) {
            userVO = JSONUtil.toBean(userVOJson, UserVO.class, true);

            // 统计数据应该是额外维护
            String followerCount = stringRedisTemplate.opsForValue().get("user:followerCount:" + userId);
            String attentionCount = stringRedisTemplate.opsForValue().get("user:attentionCount:" + userId);

            if (followerCount != null) {
                userVO.setFollowerCount(Integer.parseInt(followerCount));
            }

            if (attentionCount != null) {
                userVO.setAttentionCount(Integer.parseInt(attentionCount));
            }

            userVOMap.put("user:" + userId, userVO);
            return userVO;
        }

        //数据库
        User user = userMapper.getUserById(Long.valueOf(userId));

        if (user == null) {
            // 注意不存在的时候要解决缓存穿透的问题
            stringRedisTemplate.opsForValue().set("user:" + userId, "");
            return null;
        }

        userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 回写缓存
        stringRedisTemplate.opsForValue().set("user:" + userId, JSONUtil.toJsonStr(userVO));
        userVOMap.put("user:" + userId, userVO);

        return userVO;
    }

    @Override
    public void followUser(Integer userId, Integer attentionId) {

        // 当前用户attentionCount加1
        userMapper.incAttentionCnt(userId);
        // 删除缓存
        stringRedisTemplate.delete("user:attentionCount:" + userId);
        // 插入attention表
        Attention attention = new Attention();
        attention.setUserId(userId);
        attention.setAttentionId(attentionId);
        attentionMapper.addAttention(attention);


        // 当前用户要关注的用户的followerCount+1
        userMapper.incFollowerCnt(attentionId);
        // 删除缓存
        stringRedisTemplate.delete("user:followerCount:" + userId);
        // 插入follower表
        Follower follower = new Follower();
        follower.setUserId(attentionId);
        follower.setFollowerId(userId);
        followerMapper.addFollower(follower);


    }

    @Override
    public UserVO getUserDetail(String token) {
        String userVOKey = LOGIN_TOKEN_KEY + token;

        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(userVOKey);

        return null;
    }

    @Override
    public UserVO getUserById(Long userId) {
        User user = userMapper.getUserById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
