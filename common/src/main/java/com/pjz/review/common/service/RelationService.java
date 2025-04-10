package com.pjz.review.common.service;

import com.pjz.review.common.entity.vo.UserVO;

import java.util.List;

public interface RelationService {

    List<UserVO> getFollowerList(Integer userId, Integer type, Integer start, Integer stop);

}
