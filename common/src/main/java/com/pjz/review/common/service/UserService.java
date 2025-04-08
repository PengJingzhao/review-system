package com.pjz.review.common.service;


import com.pjz.review.common.entity.User;
import com.pjz.review.common.entity.dto.LoginFormDTO;
import com.pjz.review.common.entity.vo.UserVO;

import javax.servlet.http.HttpSession;

public interface UserService {


    String sendCode(String phone, HttpSession session);

    String login(LoginFormDTO loginFormDTO, HttpSession session);

    UserVO getUser(Integer userId);

    void followUser(Integer userId, Integer attentionId);
}
