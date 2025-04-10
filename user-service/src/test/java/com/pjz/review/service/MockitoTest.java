package com.pjz.review.service;

import com.pjz.review.common.entity.vo.UserVO;
import com.pjz.review.common.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MockitoTest {

    @Mock
    private UserService userService;

    @Spy
    private UserService userServiceSpy;

    @Test
    public void testMockInit(){
        MockingDetails details = Mockito.mockingDetails(userService);
        System.out.println(details.isMock());
        MockingDetails detailsSpy = Mockito.mockingDetails(userServiceSpy);
        System.out.println(detailsSpy.isMock());
    }

    @Test
    public void testMockReturn(){
        Mockito.doReturn(new UserVO()).when(userService).getUser(1);

        UserVO user = userService.getUser(1);
        System.out.println(user);
    }

}
