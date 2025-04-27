package com.pjz.review.common.service;

import com.pjz.review.common.entity.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/user/relation")
public interface RelationService {

    List<UserVO> getFollowerList(Integer userId, Integer type, Integer start, Integer stop);

    List<Integer> getAttentionList(Integer userId);

}
