package com.pjz.review.service.impl;

import com.pjz.review.common.entity.Attention;
import com.pjz.review.common.entity.Follower;
import com.pjz.review.common.entity.User;
import com.pjz.review.common.entity.dto.LoginFormDTO;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.mapper.AttentionMapper;
import com.pjz.review.mapper.FollowerMapper;
import com.pjz.review.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private StringRedisTemplate mockStringRedisTemplate;
    @Mock
    private AttentionMapper mockAttentionMapper;
    @Mock
    private FollowerMapper mockFollowerMapper;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private MockedStatic<Assert> assertMockedStatic;

    @InjectMocks
    private UserServiceImpl userServiceImplUnderTest;

    @BeforeEach
    public void setUp() {
//        assertMockedStatic = Mockito.mockStatic(Assert.class);
    }

    @Test
    void testSendCode() {
        String phone = "18718963656";

        // Setup
        when(mockStringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // Run the test
        final String result = userServiceImplUnderTest.sendCode(phone);

        // Verify the results
        assertThat(result).isEqualTo("123456");
    }

    @Test
    void testLogin() {
        String phone = "18718963656";
        String code = "123456";
        // Setup
        final LoginFormDTO loginFormDTO = new LoginFormDTO(code, phone);
        when(mockStringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // Configure UserMapper.getUserByPhone(...).
        final User user = new User();
        user.setId(0);
        user.setName("name");
        user.setEmail("email");
        user.setPhone("phone");
        user.setPassword("password");
        when(mockUserMapper.getUserByPhone("phone")).thenReturn(user);

        // Configure UserMapper.addUserWithPhone(...).
        final User user1 = new User();
        user1.setId(0);
        user1.setName("name");
        user1.setEmail("email");
        user1.setPhone("phone");
        user1.setPassword("password");
        when(mockUserMapper.addUserWithPhone("phone")).thenReturn(user1);

        when(mockStringRedisTemplate.opsForHash()).thenReturn(null);

        // Run the test
        final String result = userServiceImplUnderTest.login(loginFormDTO);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockStringRedisTemplate).expire("key", 0L, TimeUnit.MINUTES);
    }

    @Test
    void testGetUser() {
        // Setup
        final UserVO expectedResult = new UserVO(1,"name", "email", "phone", 0, 0);
        when(mockStringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // Configure UserMapper.getUserById(...).
        final User user = new User();
        user.setId(0);
        user.setName("name");
        user.setEmail("email");
        user.setPhone("phone");
        user.setPassword("password");
        user.setAttentionCount(0);
        user.setFollowerCount(0);
        when(mockUserMapper.getUserById(0)).thenReturn(user);

        // Run the test
        final UserVO result = userServiceImplUnderTest.getUser(0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetUser_UserMapperReturnsNull() {
        // Setup
        when(mockStringRedisTemplate.opsForValue()).thenReturn(null);
        when(mockUserMapper.getUserById(0)).thenReturn(null);

        // Run the test
        final UserVO result = userServiceImplUnderTest.getUser(0);

        // Verify the results
        assertThat(result).isNull();
    }

    @Test
    void testFollowUser() {
        // Setup
        // Run the test
        userServiceImplUnderTest.followUser(0, 0);

        // Verify the results
        verify(mockUserMapper).incAttentionCnt(0);
        verify(mockStringRedisTemplate).delete("key");

        // Confirm AttentionMapper.addAttention(...).
        final Attention attention = new Attention();
        attention.setId(0);
        attention.setUserId(0);
        attention.setAttentionId(0);
        verify(mockAttentionMapper).addAttention(attention);
        verify(mockUserMapper).incFollowerCnt(0);

        // Confirm FollowerMapper.addFollower(...).
        final Follower follower = new Follower();
        follower.setId(0);
        follower.setUserId(0);
        follower.setFollowerId(0);
        verify(mockFollowerMapper).addFollower(follower);
    }
}
