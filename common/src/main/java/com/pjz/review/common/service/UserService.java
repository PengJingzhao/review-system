package com.pjz.review.common.service;


import com.pjz.review.common.entity.User;
import com.pjz.review.common.entity.dto.LoginFormDTO;
import com.pjz.review.common.entity.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RequestMapping("/api/user")
public interface UserService {

    @GetMapping("/sendCode/{phone}")
    String sendCode(@PathVariable("phone") String phone);

    @PostMapping("/login")
    String login(@RequestBody LoginFormDTO loginFormDTO);

    @DeleteMapping("/{userId}")
    UserVO getUser(@PathVariable("userId") Integer userId);

    @GetMapping("/followUser")
    void followUser(@RequestParam("userId") Integer userId, @RequestParam("attentionId") Integer attentionId);
}
