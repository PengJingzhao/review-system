package com.pjz.review.service.impl;

import com.pjz.review.common.entity.dto.LoginFormDTO;
import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.common.service.UserService;
import com.pjz.review.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest1 {

    @Mock
    private UserService userService;

    @InjectMocks
    @Spy
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserMapper userMapper;

    @Test
    public void testSendCode() {

        Mockito.doReturn("success").when(userService).sendCode(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpSession.class));

        Assertions.assertEquals("success", userService.sendCode("", new HttpSession() {
            @Override
            public long getCreationTime() {
                return 0;
            }

            @Override
            public String getId() {
                return "";
            }

            @Override
            public long getLastAccessedTime() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public void setMaxInactiveInterval(int i) {

            }

            @Override
            public int getMaxInactiveInterval() {
                return 0;
            }

            @Override
            public HttpSessionContext getSessionContext() {
                return null;
            }

            @Override
            public Object getAttribute(String s) {
                return null;
            }

            @Override
            public Object getValue(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String[] getValueNames() {
                return new String[0];
            }

            @Override
            public void setAttribute(String s, Object o) {

            }

            @Override
            public void putValue(String s, Object o) {

            }

            @Override
            public void removeAttribute(String s) {

            }

            @Override
            public void removeValue(String s) {

            }

            @Override
            public void invalidate() {

            }

            @Override
            public boolean isNew() {
                return false;
            }
        }));
    }

    @Test
    public void testLogin() {
        Mockito.doReturn("success").when(userService).login(ArgumentMatchers.any(LoginFormDTO.class), ArgumentMatchers.any(HttpSession.class));

        Assertions.assertEquals("success", userService.login(new LoginFormDTO(), new HttpSession() {
            @Override
            public long getCreationTime() {
                return 0;
            }

            @Override
            public String getId() {
                return "";
            }

            @Override
            public long getLastAccessedTime() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public void setMaxInactiveInterval(int i) {

            }

            @Override
            public int getMaxInactiveInterval() {
                return 0;
            }

            @Override
            public HttpSessionContext getSessionContext() {
                return null;
            }

            @Override
            public Object getAttribute(String s) {
                return null;
            }

            @Override
            public Object getValue(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String[] getValueNames() {
                return new String[0];
            }

            @Override
            public void setAttribute(String s, Object o) {

            }

            @Override
            public void putValue(String s, Object o) {

            }

            @Override
            public void removeAttribute(String s) {

            }

            @Override
            public void removeValue(String s) {

            }

            @Override
            public void invalidate() {

            }

            @Override
            public boolean isNew() {
                return false;
            }
        }));
    }

    @Test
    public void testGetUser() {
        UserVO userVO = new UserVO();
        userVO.setName("test");
        Mockito.doReturn(userVO).when(userService).getUser(1);

        Assertions.assertEquals(userVO, userService.getUser(1));
    }

    @Test
    public void testFollowUser() {
        Mockito.doNothing().when(userService).followUser(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        userService.followUser(1, 2);
        Mockito.verify(userService, Mockito.times(1)).followUser(1, 2);
    }

    @Test
    public void testDoThrow() {
        Mockito.doThrow(RuntimeException.class).when(userService).getUser(ArgumentMatchers.anyInt());
        try {
            userService.getUser(1);
        } catch (Exception e) {
            Assertions.assertInstanceOf(RuntimeException.class, e);
        }
    }

    @Test
    public void testMultiStub() {
        Mockito.doReturn(1).doReturn(2).doReturn(3).when(userService).getUser(ArgumentMatchers.anyInt());
    }

    @Test
    public void testInjectMocks(){
        Mockito.doReturn(new UserVO()).when(userServiceImpl).getUser(ArgumentMatchers.anyInt());

        UserVO user = userServiceImpl.getUser(10);
        System.out.println(userServiceImpl);
    }

}
