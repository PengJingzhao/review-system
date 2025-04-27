package com.pjz.review.common.service;


import com.pjz.review.common.entity.dto.LoginFormDTO;
import com.pjz.review.common.entity.vo.UserVO;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
public interface UserService {

    @GetMapping("/sendCode/{phone}")
    String sendCode(@PathVariable("phone") String phone);

    @PostMapping("/login")
    String login(@RequestBody LoginFormDTO loginFormDTO);

    @GetMapping("/getUser")
    UserVO getUser(@RequestHeader("authorization") String token);

    @GetMapping("/followUser")
    void followUser(@RequestParam("userId") Integer userId, @RequestParam("attentionId") Integer attentionId);

//    @GetMapping
    UserVO getUserDetail(@RequestHeader("authorization") String token);

    UserVO getUserById(Long userId);
}
