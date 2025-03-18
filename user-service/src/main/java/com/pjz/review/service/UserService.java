package com.pjz.review.service;

import com.pjz.review.entity.dto.LoginFormDTO;
import com.pjz.review.entity.vo.UserVO;

import javax.servlet.http.HttpSession;

public interface UserService {


    String sendCode(String phone, HttpSession session);

    String login(LoginFormDTO loginFormDTO, HttpSession session);
}
