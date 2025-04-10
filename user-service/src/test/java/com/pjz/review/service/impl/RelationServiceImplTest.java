package com.pjz.review.service.impl;

import cn.hutool.json.JSONUtil;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.mapper.FollowerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RelationServiceImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private FollowerMapper followerMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ListOperations<String, String> listOperations;

    @InjectMocks
    private RelationServiceImpl relationService;

    @BeforeEach
    void setUp() {
//        // 配置StringRedisTemplate返回的操作对象
//        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
    }

    /**
     * 测试：查询其他用户的关注者列表 - 缓存命中的情况
     */
    @Test
    void getFollowerList_queryOtherUser_cacheHit() {
        // 配置StringRedisTemplate返回的操作对象
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        // 准备测试数据
        Integer userId = 1;
        Integer type = 0; // 查询其他用户
        Integer start = 0;
        Integer stop = 10;

        // 模拟Redis缓存命中，返回关注者ID列表
        List<String> cachedUserIds = Arrays.asList("2", "3", "4");
        when(listOperations.range("user:follower:list" + userId, start.longValue(), stop.longValue()))
                .thenReturn(cachedUserIds);

        // 模拟Redis返回的用户JSON数据
        List<String> userJsonList = Arrays.asList(
                "{\"id\":2,\"name\":\"user2\"}",
                "{\"id\":3,\"name\":\"user3\"}",
                "{\"id\":4,\"name\":\"user4\"}"
        );
        when(valueOperations.multiGet(cachedUserIds)).thenReturn(userJsonList);

        // 模拟JSON工具类转换行为
        try (MockedStatic<JSONUtil> jsonUtilMock = mockStatic(JSONUtil.class)) {
            // 为每个JSON字符串配置转换结果
            for (int i = 0; i < userJsonList.size(); i++) {
                UserVO userVO = new UserVO();
                userVO.setId(i + 2);
                userVO.setName("user" + (i + 2));

                int finalI = i;
                jsonUtilMock.when(() -> JSONUtil.toBean(userJsonList.get(finalI), UserVO.class))
                        .thenReturn(userVO);
            }

            // 执行被测试方法
            List<UserVO> result = relationService.getFollowerList(userId, type, start, stop);

            // 验证结果
            assertEquals(3, result.size());
            assertEquals(2, result.get(0).getId());
            assertEquals(3, result.get(1).getId());
            assertEquals(4, result.get(2).getId());
        }

        // 验证交互
        verify(listOperations).range("user:follower:list" + userId, start.longValue(), stop.longValue());
        verify(valueOperations).multiGet(cachedUserIds);
        verify(followerMapper, never()).getFollowerIds(any(), any(), any()); // 确认未查询数据库
    }

    /**
     * 测试：查询其他用户的关注者列表 - 缓存未命中的情况
     */
    @Test
    void getFollowerList_queryOtherUser_cacheMiss() {
        // 配置StringRedisTemplate返回的操作对象
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        // 准备测试数据
        Integer userId = 1;
        Integer type = 0; // 查询其他用户
        Integer start = 0;
        Integer stop = 10;

        // 模拟Redis缓存未命中
        when(listOperations.range("user:follower:list" + userId, start.longValue(), stop.longValue()))
                .thenReturn(null);

        // 模拟数据库查询结果
        List<Integer> followerIds = Arrays.asList(2, 3, 4);
        when(followerMapper.getFollowerIds(userId, start, stop)).thenReturn(followerIds);

        // 转换为字符串ID列表
        List<String> stringUserIds = followerIds.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        // 模拟Redis返回的用户JSON数据
        List<String> userJsonList = Arrays.asList(
                "{\"id\":2,\"username\":\"user2\"}",
                "{\"id\":3,\"username\":\"user3\"}",
                "{\"id\":4,\"username\":\"user4\"}"
        );
        when(valueOperations.multiGet(stringUserIds)).thenReturn(userJsonList);

        // 模拟JSON工具类转换行为
        try (MockedStatic<JSONUtil> jsonUtilMock = mockStatic(JSONUtil.class)) {
            for (int i = 0; i < userJsonList.size(); i++) {
                UserVO userVO = new UserVO();
                userVO.setId(i + 2);
                userVO.setName("user" + (i + 2));

                int finalI = i;
                jsonUtilMock.when(() -> JSONUtil.toBean(userJsonList.get(finalI), UserVO.class))
                        .thenReturn(userVO);
            }

            // 执行被测试方法
            List<UserVO> result = relationService.getFollowerList(userId, type, start, stop);

            // 验证结果
            assertEquals(3, result.size());
            assertEquals(2, result.get(0).getId());
            assertEquals(3, result.get(1).getId());
            assertEquals(4, result.get(2).getId());
        }

        // 验证交互
        verify(listOperations).range("user:follower:list" + userId, start.longValue(), stop.longValue());
        verify(followerMapper).getFollowerIds(userId, start, stop);
        verify(listOperations).leftPushAll(eq("user:follower:list" + userId), eq(stringUserIds)); // 验证写入缓存
        verify(valueOperations).multiGet(stringUserIds);
    }

    /**
     * 测试：查询本人的关注者列表
     */
    @Test
    void getFollowerList_queryOneself() {
        // 配置StringRedisTemplate返回的操作对象
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        // 准备测试数据
        Integer userId = 1;
        Integer type = 1; // 查询本人
        Integer start = 0;
        Integer stop = 10;

        // 模拟数据库查询结果
        List<Integer> followerIds = Arrays.asList(2, 3, 4);
        when(followerMapper.getFollowerIds(userId, start, stop)).thenReturn(followerIds);

        // 转换为字符串ID列表
        List<String> stringUserIds = followerIds.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        // 模拟Redis返回的用户JSON数据
        List<String> userJsonList = Arrays.asList(
                "{\"id\":2,\"username\":\"user2\"}",
                "{\"id\":3,\"username\":\"user3\"}",
                "{\"id\":4,\"username\":\"user4\"}"
        );
        when(valueOperations.multiGet(stringUserIds)).thenReturn(userJsonList);

        // 模拟JSON工具类转换行为
        try (MockedStatic<JSONUtil> jsonUtilMock = mockStatic(JSONUtil.class)) {
            for (int i = 0; i < userJsonList.size(); i++) {
                UserVO userVO = new UserVO();
                userVO.setId(i + 2);
                userVO.setName("user" + (i + 2));

                int finalI = i;
                jsonUtilMock.when(() -> JSONUtil.toBean(userJsonList.get(finalI), UserVO.class))
                        .thenReturn(userVO);
            }

            // 执行被测试方法
            List<UserVO> result = relationService.getFollowerList(userId, type, start, stop);

            // 验证结果
            assertEquals(3, result.size());
            assertEquals(2, result.get(0).getId());
            assertEquals(3, result.get(1).getId());
            assertEquals(4, result.get(2).getId());
        }

        // 验证交互
        verify(followerMapper).getFollowerIds(userId, start, stop); // 验证直接查询数据库
        verify(valueOperations).multiGet(stringUserIds);
        verify(listOperations, never()).range(anyString(), anyLong(), anyLong()); // 确认未查询缓存
    }

    /**
     * 测试：查询其他用户的关注者列表 - 无数据的情况
     */
    @Test
    void getFollowerList_queryOtherUser_noData() {
        // 配置StringRedisTemplate返回的操作对象
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        // 准备测试数据
        Integer userId = 1;
        Integer type = 0; // 查询其他用户
        Integer start = 0;
        Integer stop = 10;

        // 模拟Redis缓存未命中
        when(listOperations.range("user:follower:list" + userId, start.longValue(), stop.longValue()))
                .thenReturn(null);

        // 模拟数据库也无数据
        when(followerMapper.getFollowerIds(userId, start, stop)).thenReturn(Collections.emptyList());

        // 模拟Redis查询结果为空
        when(valueOperations.multiGet(Collections.emptyList())).thenReturn(null);

        // 执行被测试方法
        List<UserVO> result = relationService.getFollowerList(userId, type, start, stop);

        // 验证结果
        assertTrue(result.isEmpty());

        // 验证交互
        verify(listOperations).range("user:follower:list" + userId, start.longValue(), stop.longValue());
        verify(followerMapper).getFollowerIds(userId, start, stop);
        verify(listOperations).leftPushAll(eq("user:follower:list" + userId), eq(Collections.emptyList()));
        verify(valueOperations).multiGet(Collections.emptyList());
    }

    /**
     * 测试：查询本人的关注者列表 - 无数据的情况
     */
    @Test
    void getFollowerList_queryOneself_noData() {
        // 配置StringRedisTemplate返回的操作对象
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        // 准备测试数据
        Integer userId = 1;
        Integer type = 1; // 查询本人
        Integer start = 0;
        Integer stop = 10;

        // 模拟数据库无数据
        when(followerMapper.getFollowerIds(userId, start, stop)).thenReturn(Collections.emptyList());

        // 模拟Redis查询结果为空
        when(valueOperations.multiGet(Collections.emptyList())).thenReturn(null);

        // 执行被测试方法
        List<UserVO> result = relationService.getFollowerList(userId, type, start, stop);

        // 验证结果
        assertTrue(result.isEmpty());

        // 验证交互
        verify(followerMapper).getFollowerIds(userId, start, stop);
        verify(valueOperations).multiGet(Collections.emptyList());
    }
}
