package com.pjz.review.service.impl;

import com.pjz.commons.utils.ValidatorUtil;
import com.pjz.review.mapper.AttentionMapper;
import com.pjz.review.mapper.FollowerMapper;
import com.pjz.review.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static com.pjz.review.util.ExceptionConstants.PHONE_FORMAT_NOT_CORRECT;
import static com.pjz.review.util.RedisConstants.LOGIN_CODE_KEY;
import static com.pjz.review.util.RedisConstants.LOGIN_CODE_TTL;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest2 {

    @InjectMocks
    @Spy
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private AttentionMapper attentionMapper;

    @Mock
    private FollowerMapper followerMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private MockedStatic<ValidatorUtil> validatorUtil;

    @BeforeEach
    public void setUp() {
        validatorUtil = Mockito.mockStatic(ValidatorUtil.class);
    }

    @Test
    public void testSendCode() {
        Mockito.doReturn(valueOperations).when(stringRedisTemplate).opsForValue();

        String phone = "18718963656";

        validatorUtil.when(() -> ValidatorUtil.isValidPhone(phone)).thenReturn(true);

        String code = userService.sendCode(phone);

        Assertions.assertEquals("123456", code);

        Mockito.verify(valueOperations).set(Mockito.eq(LOGIN_CODE_KEY + phone), Mockito.eq(code), Mockito.eq(LOGIN_CODE_TTL), Mockito.eq(TimeUnit.MINUTES));
    }

    @Test
    public void testSendCodeWithThrowIllegalArgumentException() {
        String phone = "123";

        validatorUtil.when(() -> ValidatorUtil.isValidPhone(phone)).thenReturn(false);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userService.sendCode(phone));

        Assertions.assertEquals(PHONE_FORMAT_NOT_CORRECT, exception.getMessage());
    }

}
